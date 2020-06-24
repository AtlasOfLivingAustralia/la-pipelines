package au.org.ala.kvs;

import au.org.ala.kvs.cache.GeocodeServiceFactory;
import java.util.Optional;
import org.gbif.kvs.cache.KeyValueCache;
import org.gbif.kvs.geocode.LatLng;
import org.gbif.pipelines.parsers.config.factory.KvConfigFactory;
import org.gbif.pipelines.parsers.config.model.KvConfig;
import org.gbif.pipelines.parsers.parsers.location.GeocodeKvStore;
import org.gbif.rest.client.geocode.GeocodeResponse;
import org.gbif.rest.client.geocode.Location;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class GeocodeServiceTest {

    /**
     * Tests the Get operation on {@link KeyValueCache} that wraps a simple KV store backed by a HashMap.
     */
    @Test
    public void testInsideCountry() throws Exception {

        Properties p = new Properties();
        p.load(new FileInputStream(new File("src/test/resources/pipelines.properties")));

        KvConfig kvConfig = KvConfigFactory.create(p, KvConfigFactory.GEOCODE_PREFIX);
        ALAKvConfig alaConfig = ALAKvConfigFactory.create(p);
        alaConfig.setCommonConfig(kvConfig);

        GeocodeKvStore geoService = GeocodeServiceFactory.create(alaConfig);
        GeocodeResponse resp = geoService.get(LatLng.builder().withLongitude(146.2).withLatitude(-27.9).build());
        assert !resp.getLocations().isEmpty();
        Optional<Location> countryLocation =  resp.getLocations().stream().filter(location -> location.getType().equalsIgnoreCase("Political") || location
            .getType().equalsIgnoreCase("EEZ")).findFirst();

        assert countryLocation.get().getCountryName().equals("AU");
    }

    /**
     * Tests the Get operation on {@link KeyValueCache} that wraps a simple KV store backed by a HashMap.
     */
    @Test
    public void testInsideEEZ() throws Exception {

        Properties p = new Properties();
        p.load(new FileInputStream(new File("src/test/resources/pipelines.properties")));
        KvConfig kvConfig = KvConfigFactory.create(p, KvConfigFactory.GEOCODE_PREFIX);
        ALAKvConfig alaConfig = ALAKvConfigFactory.create(p);
        alaConfig.setCommonConfig(kvConfig);

        GeocodeKvStore geoService = GeocodeServiceFactory.create(alaConfig);
        GeocodeResponse resp = geoService.get(LatLng.builder().withLongitude(151.329751).withLatitude(-36.407357).build());
        assert !resp.getLocations().isEmpty();

        Optional<Location> countryLocation =  resp.getLocations().stream().filter(location -> location.getType().equalsIgnoreCase("Political") || location
            .getType().equalsIgnoreCase("EEZ")).findFirst();

        assert countryLocation.get().getCountryName().equals("AU");
    }

    /**
     * Tests the Get operation on {@link KeyValueCache} that wraps a simple KV store backed by a HashMap.
     */
    @Test
    public void testInsideState() throws Exception {

        Properties p = new Properties();
        p.load(new FileInputStream(new File("src/test/resources/pipelines.properties")));
        KvConfig kvConfig = KvConfigFactory.create(p, KvConfigFactory.GEOCODE_PREFIX);
        ALAKvConfig alaConfig = ALAKvConfigFactory.create(p);
        alaConfig.setCommonConfig(kvConfig);

        GeocodeKvStore geoService = GeocodeServiceFactory.create(alaConfig);
        GeocodeResponse resp = geoService.get(LatLng.builder().withLongitude(132.5509603).withLatitude(-19.4914108).build());

        assert !resp.getLocations().isEmpty();
        Optional<Location> stateLocation =  resp.getLocations().stream().filter(location -> location.getType().equalsIgnoreCase("State")).findFirst();

        assert stateLocation.get().getCountryName().equals("Northern Territory");
    }
}
