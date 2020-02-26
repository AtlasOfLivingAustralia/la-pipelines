package au.org.ala.kvs.cache;

import au.org.ala.kvs.client.LatLng;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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

    Map<String, SamplingCache2> refCache2 =  new HashMap<String, SamplingCache2>();

    private SamplingKeyValueStoreFactory(){}

    public static SamplingKeyValueStoreFactory getInstance() {
        if(instance == null){
            instance  = new SamplingKeyValueStoreFactory();
        }
        return instance;
    }

    public static void setupFor(String datasetID) {
        try {
            KeyValueStore<LatLng, Map<String, String>> cache = buildForDataset(datasetID);
            getInstance().refCache.put(datasetID, cache);
        } catch (Exception e){
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public static void setupFor2(String datasetID) {
        try {
            SamplingCache2 cache = openForDataset2(datasetID);
            getInstance().refCache2.put(datasetID, cache);
        } catch (Exception e){
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public static KeyValueStore<LatLng, Map<String, String>> getForDataset(String datasetID)  {
        return getInstance().refCache.get(datasetID);
    }

    public static SamplingCache2 getForDataset2(String datasetID)  {
        return getInstance().refCache2.get(datasetID);
    }

    public static KeyValueStore<LatLng, Map<String, String>> buildForDataset(String datasetID) throws Exception {

        Instant start = Instant.now();

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

            log.info("Populating MapDB  - Reading file {} ", file.getAbsolutePath());
            int counter = 0;

            //read file
            CSVReader csvReader = new CSVReader(file, "UTF-8", ",", '"', 1);
            String [] header = csvReader.header;
            Map<LatLng, Map<String, String>> buff = new HashMap<LatLng, Map<String, String>>();

            while (csvReader.hasNext()){
                counter ++;

                if (counter % 1000 == 0){
                    log.info("Populating MapDB  - Reading file {}, at line number {}", file.getAbsolutePath(), counter);
                }

                String[] line  = csvReader.next();

                if (header.length != line.length){
                    throw new RuntimeException("Error in sampling data format");
                } else {

                    String latitude = line[0];
                    String longitude = line[1];

                    if (latitude != null && longitude != null && !longitude.equals("null") && !latitude.equals("null")) {
                        Map<String, String> map = new HashMap<String, String>();
                        for (int i = 2; i < header.length; i++) {
                            if (StringUtils.isNotEmpty(line[i])) {
                                map.put(header[i], line[i]);
                            }
                        }

                        LatLng ll = LatLng.builder()
                                .latitude(Double.parseDouble(latitude))
                                .longitude(Double.parseDouble(longitude))
                                .build();

                        if (counter % 1000 == 0){
                            cache.putAll(buff);
                            buff.clear();
                        }
                        buff.put(ll, map);
                    }
                }
            }
            cache.putAll(buff);
            buff.clear();

            csvReader.close();
            log.info("Populating MapDB  - Finished file {}, lines read {}", file.getAbsolutePath(), counter);
        }

        log.info("Cache built in {}", Duration.between(start, Instant.now()));

        return cache;
    }

    public static SamplingCache2 openForDataset2(String datasetID) throws Exception {
        String cacheDir = "/data/pipelines-data/" + datasetID + "/1/caches";
        FileUtils.forceMkdir(new File(cacheDir));
        return new SamplingCache2(cacheDir);
    }

    public static SamplingCache2 buildForDataset2(String datasetID) throws Exception {

        Instant start = Instant.now();

        File existing = new File("/data/pipelines-data/" + datasetID + "/1/caches/sample-cache");
        if (existing.exists()){
            FileUtils.forceDelete(existing);
        }

        //read sampling
        String cacheDir = "/data/pipelines-data/" + datasetID + "/1/caches";
        FileUtils.forceMkdir(new File(cacheDir));

        SamplingCache2 cache = new SamplingCache2(cacheDir);

        File[] files = new File("/data/pipelines-data/" + datasetID+ "/1/sampling").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".csv");
            }
        });

        for (File file: files){

            log.info("Populating MapDB  - Reading file {} ", file.getAbsolutePath());
            int counter = 0;

            //read file
            CSVReader csvReader = new CSVReader(file, "UTF-8", ",", '"', 1);
            String [] header = csvReader.header;

            while (csvReader.hasNext()){
                counter ++;

                if (counter % 1000 == 0){
                    log.info("Populating MapDB  - Reading file {}, at line number {}", file.getAbsolutePath(), counter);
                }

                String[] line  = csvReader.next();

                if (header.length != line.length){
                    throw new RuntimeException("Error in sampling data format");
                } else {

                    String latitude = line[0];
                    String longitude = line[1];

                    if (latitude != null && longitude != null && !longitude.equals("null") && !latitude.equals("null")) {
                        Map<String, String> map = new HashMap<String, String>();
                        for (int i = 2; i < header.length; i++) {
                            if (StringUtils.isNotEmpty(line[i])) {
                                map.put(header[i], line[i]);
                            }
                        }
                        cache.addToCache(Double.parseDouble(latitude), Double.parseDouble(longitude), map);
                    }
                }
            }

            cache.closeForWriting();

            csvReader.close();
            log.info("Populating MapDB  - Finished file {}, lines read {}", file.getAbsolutePath(), counter);
        }

        log.info("Cache built in {}", Duration.between(start, Instant.now()));

        return cache;
    }
}
