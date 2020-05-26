package au.org.ala.pipelines.interpreters;

import au.org.ala.kvs.ALAKvConfig;
import au.org.ala.kvs.ALAKvConfigFactory;
import au.org.ala.kvs.cache.ALACollectionKVStoreFactory;
import au.org.ala.kvs.client.ALACollectionLookup;
import au.org.ala.kvs.client.ALACollectionMatch;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.kvs.KeyValueStore;
import org.gbif.pipelines.io.avro.ALAAttributionRecord;
import org.gbif.pipelines.io.avro.ExtendedRecord;
import org.gbif.rest.client.configuration.ClientConfiguration;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;

public class ALAAttributionInterpreterTest {

    @Test
    public void testCollectionLookup() throws Exception {

        ClientConfiguration cc = ClientConfiguration.builder().withBaseApiUrl("https://collections.ala.org.au").build();
        ALAKvConfig alaKvConfig = ALAKvConfigFactory.create(new Properties());

        KeyValueStore<ALACollectionLookup, ALACollectionMatch> kvs = ALACollectionKVStoreFactory.alaCollectionKVStore(cc, alaKvConfig);

        BiConsumer<ExtendedRecord, ALAAttributionRecord> fcn = ALAAttributionInterpreter.interpretCodes(kvs);

        Map<String, String> map = new HashMap<String, String>();
        map.put(DwcTerm.institutionCode.namespace() + DwcTerm.institutionCode.simpleName(), "CSIRO");
        map.put(DwcTerm.collectionCode.namespace() + DwcTerm.collectionCode.simpleName(), "ANIC");

        ALAAttributionRecord aar = ALAAttributionRecord.newBuilder().setId("1").build();

        fcn.accept(ExtendedRecord.newBuilder().setId("1").setCoreTerms(map).build(), aar);
        assert aar.getCollectionUid() != null;
        assert aar.getCollectionUid().equals("co13");
    }

    @Test
    public void testCollectionLookupBadValues() throws Exception {

        ClientConfiguration cc = ClientConfiguration.builder().withBaseApiUrl("https://collections.ala.org.au").build();
        ALAKvConfig alaKvConfig = ALAKvConfigFactory.create(new Properties());

        KeyValueStore<ALACollectionLookup, ALACollectionMatch> kvs = ALACollectionKVStoreFactory.alaCollectionKVStore(cc, alaKvConfig);

        BiConsumer<ExtendedRecord, ALAAttributionRecord> fcn = ALAAttributionInterpreter.interpretCodes(kvs);

        Map<String, String> map = new HashMap<String, String>();
        map.put(DwcTerm.institutionCode.namespace() + DwcTerm.institutionCode.simpleName(), "CSIRO");
        map.put(DwcTerm.collectionCode.namespace() + DwcTerm.collectionCode.simpleName(), "ANICXXXXXX");

        ALAAttributionRecord aar = ALAAttributionRecord.newBuilder().setId("1").build();

        fcn.accept(ExtendedRecord.newBuilder().setId("1").setCoreTerms(map).build(), aar);
        assert aar.getCollectionUid() == null;
    }
}
