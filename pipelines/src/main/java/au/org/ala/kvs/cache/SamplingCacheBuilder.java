package au.org.ala.kvs.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.codehaus.plexus.util.FileUtils;
import org.gbif.utils.file.csv.CSVReader;

import java.io.File;
import java.io.FilenameFilter;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SamplingCacheBuilder {

    public static void main(String[] args) throws Exception {

        if(args.length == 2 ){
            log.info("Starting the build of {}", args[0]);
            buildForDataset(args[0], args[1]);
            log.info("Finished the build of {}", args[0]);
        } else {
            System.err.println("Required arguments [data resource uid] [working directory] [target directory]");
        }
    }

    /**
     * Builds a cache in the supplied working directory and then copies the file to the target directory.
     *
     * @param datasetID
     * @param workingDirectory
     * @throws Exception
     */
    public static void buildForDataset(String datasetID, String workingDirectory) throws Exception {

        File targetFile = new File("/data/pipelines-data/" + datasetID + "/1/caches/sample-cache");

        Instant start = Instant.now();

        SamplingCache cache = SamplingCache.createNewCache(workingDirectory, "sample-cache-" + datasetID);

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

                    //Format of sampling files has first 2 columns latitude, longitude.
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

            csvReader.close();
            log.info("Populating MapDB  - Finished file {}, lines read {}", file.getAbsolutePath(), counter);
        }

        cache.closeForWriting();

        log.info("Cache built in {}", Duration.between(start, Instant.now()));

        FileUtils.copyFile(new File(workingDirectory + "/" + "sample-cache-" + datasetID), targetFile);
    }
}
