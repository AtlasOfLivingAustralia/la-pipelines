package au.org.ala.kvs.cache;

import au.org.ala.kvs.client.LatLng;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.FileUtils;
import org.gbif.kvs.KeyValueStore;
import org.gbif.utils.file.csv.CSVReader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SamplingKeyValueStoreFactory {

    static SamplingKeyValueStoreFactory instance;

    Map<String, KeyValueStore<LatLng, Map<String, String>>> refCache =  new HashMap<String, KeyValueStore<LatLng, Map<String, String>>>();

    private SamplingKeyValueStoreFactory(){}

    public static SamplingKeyValueStoreFactory getInstance() {
        if(instance == null){
            instance  = new SamplingKeyValueStoreFactory();
        }
        return instance;
    }

    public static void setupFor(String datasetID) {
//        String cacheDir = "/data/pipelines-data/" + datasetID + "/1/caches";
//
//        //read sampling
//        KeyValueStore kvs = new KeyValueStore<LatLng, Map<String, String>>() {
//            @Override
//            public Map<String, String> get(LatLng key) {
//                return new HashMap<String, String>();
//            }
//            @Override
//            public void close() throws IOException {}
//        };
//
//        WarmableCache cache = MapDBKeyValueStore.cache(cacheDir, kvs, LatLng.class, Map.class);
        try {
            KeyValueStore<LatLng, Map<String, String>> cache =  buildForDataset(datasetID);
            getInstance().refCache.put(datasetID, cache);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static KeyValueStore<LatLng, Map<String, String>> getForDataset(String datasetID)  {
        return getInstance().refCache.get(datasetID);
    }

    public static KeyValueStore<LatLng, Map<String, String>> buildForDataset(String datasetID) throws Exception {

        Instant start = Instant.now();
        SamplingKeyValueStoreFactory skvs = new SamplingKeyValueStoreFactory();

        //read sampling
        KeyValueStore kvs = new KeyValueStore<LatLng, Map<String, String>>() {
            @Override
            public Map<String, String> get(LatLng key) {
                return new HashMap<String, String>();
            }
            @Override
            public void close() throws IOException {}
        };

        String cacheDir = "/data/pipelines-data/" + datasetID + "/1/caches";
        FileUtils.forceMkdir(new File(cacheDir));

        WarmableCache cache = MapDBKeyValueStore.cache(cacheDir, kvs, LatLng.class, Map.class);


        File[] files = new File("/data/pipelines-data/" + datasetID+ "/1/sampling").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".csv");
            }
        });

        for (File file: files){

            //read file
            CSVReader csvReader = new CSVReader(file, "UTF-8", ",", '"', 1);
            String [] header = csvReader.header;
            while (csvReader.hasNext()){
                String[] line  = csvReader.next();

                if (header.length != line.length){
                    throw new RuntimeException("Error in sampling data format");
                } else {

                    String latitude = line[0];
                    String longitude = line[1];

                    if(latitude != null && longitude != null && !longitude.equals("null") && !latitude.equals("null")) {
                        Map<String, String> map = new HashMap<String, String>();
                        for (int i = 2; i < header.length; i++) {
                            map.put(header[i], line[i]);
                        }

                        LatLng ll = LatLng.builder()
                                .latitude(Double.parseDouble(latitude))
                                .longitude(Double.parseDouble(longitude))
                                .build();

                        cache.put(ll, map);
                    }
                }
            }
        }

        log.info("Cache built in {}", Duration.between(start, Instant.now()));

        return cache;
    }
}
