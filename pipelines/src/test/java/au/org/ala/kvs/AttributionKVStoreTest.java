package au.org.ala.kvs;

import au.org.ala.kvs.cache.ALAAttributionKVStoreFactory;
import au.org.ala.kvs.cache.ALACollectionKVStoreFactory;
import au.org.ala.kvs.client.ALACollectionLookup;
import au.org.ala.kvs.client.ALACollectionMatch;
import au.org.ala.kvs.client.ALACollectoryMetadata;
import au.org.ala.kvs.client.ConnectionParameters;
import org.gbif.kvs.KeyValueStore;
import org.gbif.rest.client.configuration.ClientConfiguration;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.fail;

/**
 * Unit tests for Attribution KV store
 */
public class AttributionKVStoreTest {

    @Test
    public void testAttributionLookup() throws Exception {

        ClientConfiguration cc = ClientConfiguration.builder().withBaseApiUrl("https://collections.ala.org.au").build();
        ALAKvConfig alaKvConfig = ALAKvConfigFactory.create(new Properties());
        KeyValueStore<String, ALACollectoryMetadata> kvs = ALAAttributionKVStoreFactory.alaAttributionKVStore(cc, alaKvConfig);
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

        kvs.close();
    }

    @Test
    public void testAttributionConnectionIssues() throws Exception {

        ClientConfiguration cc = ClientConfiguration.builder().withBaseApiUrl("https://collections.ala.org.auXXXX").build();
        ALAKvConfig alaKvConfig = ALAKvConfigFactory.create(new Properties());
        KeyValueStore<String, ALACollectoryMetadata> kvs = ALAAttributionKVStoreFactory.alaAttributionKVStore(cc, alaKvConfig);
        try {
            ALACollectoryMetadata m = kvs.get("dr893XXXXXX");
            fail("Exception not thrown");
        } catch(RuntimeException e){
            //expected
        }
        kvs.close();
    }

    @Test
    public void testAttributionLookupFail() throws Exception {

        ClientConfiguration cc = ClientConfiguration.builder().withBaseApiUrl("https://collections.ala.org.au").build();
        ALAKvConfig alaKvConfig = ALAKvConfigFactory.create(new Properties());
        KeyValueStore<String, ALACollectoryMetadata> kvs = ALAAttributionKVStoreFactory.alaAttributionKVStore(cc, alaKvConfig);
        try {
            ALACollectoryMetadata m = kvs.get("dr893XXXXXXX");
            fail("Exception not thrown");
        } catch(RuntimeException e){
            //expected
        }
    }

    @Test
    public void testCollectionLookup() throws Exception {

        ClientConfiguration cc = ClientConfiguration.builder().withBaseApiUrl("https://collections.ala.org.au").build();
        ALAKvConfig alaKvConfig = ALAKvConfigFactory.create(new Properties());
        KeyValueStore<ALACollectionLookup, ALACollectionMatch> kvs = ALACollectionKVStoreFactory.alaCollectionKVStore(cc, alaKvConfig);
        ALACollectionLookup lookup = ALACollectionLookup.builder().institutionCode("CSIRO").collectionCode("ANIC").build();
        ALACollectionMatch m = kvs.get(lookup);
        assert m.getCollectionUid() != null;
        assert m.getCollectionUid().equals("co13");
    }

    @Test
    public void testCollectionLookupFail() throws Exception {

        ClientConfiguration cc = ClientConfiguration.builder().withBaseApiUrl("https://collections.ala.org.au").build();
        ALAKvConfig alaKvConfig = ALAKvConfigFactory.create(new Properties());
        KeyValueStore<ALACollectionLookup, ALACollectionMatch> kvs = ALACollectionKVStoreFactory.alaCollectionKVStore(cc, alaKvConfig);
        ALACollectionLookup lookup = ALACollectionLookup.builder().institutionCode("CSIROCXXX").collectionCode("ANIC").build();
        ALACollectionMatch m = kvs.get(lookup);
        assert m.getCollectionUid() == null;
        assert m.equals(ALACollectionMatch.EMPTY);
    }
}
