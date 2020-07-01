package au.org.ala.kvs;

import au.org.ala.kvs.cache.GeocodeKvStoreFactory;
import au.org.ala.util.TestUtils;
import org.gbif.kvs.KeyValueStore;
import org.gbif.kvs.cache.KeyValueCache;
import org.gbif.kvs.geocode.LatLng;
import org.gbif.pipelines.ingest.java.utils.PipelinesConfigFactory;
import org.gbif.pipelines.parsers.config.model.PipelinesConfig;
import org.gbif.rest.client.geocode.GeocodeResponse;
import org.junit.Test;

import java.io.File;

public class GeocodeServiceTest {

    /**
     * Tests the Get operation on {@link KeyValueCache} that wraps a simple KV store backed by a HashMap.
     */
    @Test
    public void testInsideCountry() throws Exception {
        KeyValueStore<LatLng,GeocodeResponse> geoService = GeocodeKvStoreFactory.createSupplier(TestUtils.getConfig()).get();
        GeocodeResponse resp = geoService.get(LatLng.builder().withLongitude(146.2).withLatitude(-27.9).build());
        assert !resp.getLocations().isEmpty();
        assert resp.getLocations().iterator().next().getCountryName().equals("AU");
    }

    /**
     * Tests the Get operation on {@link KeyValueCache} that wraps a simple KV store backed by a HashMap.
     */
    @Test
    public void testInsideEEZ() throws Exception {
        KeyValueStore<LatLng,GeocodeResponse> geoService = GeocodeKvStoreFactory.createSupplier(TestUtils.getConfig()).get();
        GeocodeResponse resp = geoService.get(LatLng.builder().withLongitude(151.329751).withLatitude(-36.407357).build());
        assert !resp.getLocations().isEmpty();
        assert resp.getLocations().iterator().next().getCountryName().equals("AU");
    }
}
