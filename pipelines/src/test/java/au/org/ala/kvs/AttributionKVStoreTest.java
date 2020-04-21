package au.org.ala.kvs;

import au.org.ala.kvs.cache.ALAAttributionKVStoreFactory;
import au.org.ala.kvs.client.ALACollectoryMetadata;
import au.org.ala.kvs.client.ConnectionParameters;
import org.gbif.kvs.KeyValueStore;
import org.gbif.rest.client.configuration.ClientConfiguration;
import org.junit.Test;

/**
 * Unit tests for Attribution KV store
 */
public class AttributionKVStoreTest {

    @Test
    public void testAttributionLookup() throws Exception {

        ClientConfiguration cc = ClientConfiguration.builder().withBaseApiUrl("https://collections.ala.org.au").build();
        KeyValueStore<String, ALACollectoryMetadata> kvs = ALAAttributionKVStoreFactory.alaAttributionKVStore(cc);
        ALACollectoryMetadata m = kvs.get("dr893");
        ConnectionParameters connParams = m.getConnectionParameters();

        assert m.getName() != null;
        assert connParams != null;
        assert connParams.getUrl() != null;
        assert connParams.getTermsForUniqueKey() != null;
        assert connParams.getTermsForUniqueKey().size() > 0;
        assert m.getDefaultDarwinCoreValues() != null;
        assert m.getDefaultDarwinCoreValues().size() > 0;
        assert m.getProvenance() != null;
        assert m.getTaxonomyCoverageHints() != null;
        assert m.getTaxonomyCoverageHints().size() == 0;
    }
}
