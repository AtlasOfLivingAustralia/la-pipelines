package au.org.ala.kvs.cache;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SamplingCache {

    public static void main(String[] args) throws Exception {
        log.info("Starting the build of {}", args[0]);
        SamplingKeyValueStoreFactory.buildForDataset2(args[0]);
        log.info("Finished the build of {}", args[0]);
    }
}
