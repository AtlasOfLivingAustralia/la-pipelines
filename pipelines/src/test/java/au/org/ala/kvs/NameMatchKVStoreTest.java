package au.org.ala.kvs;

import au.org.ala.kvs.cache.ALANameMatchKVStoreFactory;
import au.org.ala.kvs.client.ALANameUsageMatch;
import au.org.ala.kvs.client.ALASpeciesMatchRequest;
import org.gbif.kvs.KeyValueStore;
import org.gbif.kvs.cache.KeyValueCache;
import org.gbif.rest.client.configuration.ClientConfiguration;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.IntStream;

public class NameMatchKVStoreTest {

  /**
   * Tests the Get operation on {@link KeyValueCache} that wraps a simple KV store backed by a
   * HashMap.
   */
  @Test
  public void getCacheTest() throws Exception {

    ClientConfiguration cc = ClientConfiguration.builder()
        .withBaseApiUrl("http://localhost:9179") //GBIF base API url
        .withTimeOut(10000l) //Geocode service connection time-out
        .build();
    ALAKvConfig alaKvConfig = ALAKvConfigFactory.create(new Properties());

    KeyValueStore<ALASpeciesMatchRequest, ALANameUsageMatch> kvs = ALANameMatchKVStoreFactory
        .alaNameMatchKVStore(cc, alaKvConfig);
    ALASpeciesMatchRequest req = ALASpeciesMatchRequest.builder().scientificName("Macropus rufus")
        .build();
    ALANameUsageMatch match = kvs.get(req);
    assert match.getTaxonConceptID() != null;

    ALASpeciesMatchRequest req2 = ALASpeciesMatchRequest.builder()
        .scientificName("Osphranter rufus").build();
    ALANameUsageMatch match2 = kvs.get(req2);
    assert match2.getTaxonConceptID() != null;
  }
}
