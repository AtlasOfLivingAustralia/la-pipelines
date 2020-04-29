package au.org.ala.pipelines;

import org.gbif.api.vocabulary.Country;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.kvs.KeyValueStore;
import org.gbif.kvs.geocode.LatLng;
import org.gbif.pipelines.core.interpreters.core.LocationInterpreter;
import org.gbif.pipelines.io.avro.ExtendedRecord;
import org.gbif.pipelines.io.avro.LocationRecord;
import au.org.ala.pipelines.interpreters.ALALocationInterpreter;
import org.gbif.rest.client.geocode.Location;

import org.gbif.pipelines.parsers.parsers.location.GeocodeService;
import org.gbif.rest.client.geocode.GeocodeResponse;
import org.junit.Test;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import static org.junit.Assert.*;


public class AlaLocationInterpreterTest {
    private static final String ID = "777";


    @Test
    public void interpretTest() {

        LocationRecord lr = new LocationRecord();

        Map<String, String> coreMap = new HashMap<>();

        coreMap.put(DwcTerm.stateProvince.qualifiedName(), "ACT");
        coreMap.put(DwcTerm.minimumDepthInMeters.qualifiedName(), "10");
        coreMap.put(DwcTerm.maximumDepthInMeters.qualifiedName(), "200");
        coreMap.put(DwcTerm.continent.qualifiedName(), "Asia");
        coreMap.put(DwcTerm.waterBody.qualifiedName(), "Murray");
        coreMap.put(DwcTerm.minimumElevationInMeters.qualifiedName(), "0");
        coreMap.put(DwcTerm.maximumElevationInMeters.qualifiedName(), "2000");

        coreMap.put(DwcTerm.minimumDistanceAboveSurfaceInMeters.qualifiedName(), "1 test 4 meter");
        coreMap.put(DwcTerm.maximumDistanceAboveSurfaceInMeters.qualifiedName(), "200");
        coreMap.put(DwcTerm.coordinatePrecision.qualifiedName(), "0.5");
        coreMap.put(DwcTerm.coordinateUncertaintyInMeters.qualifiedName(), "1");
        coreMap.put(DwcTerm.geodeticDatum.qualifiedName(), "EPSG:4326");


        ExtendedRecord er = ExtendedRecord.newBuilder().setId(ID).setCoreTerms(coreMap).build();


        LocationInterpreter.interpretStateProvince(er, lr);
        LocationInterpreter.interpretMinimumDepthInMeters(er, lr);
        LocationInterpreter.interpretMaximumDepthInMeters(er, lr);
        //Have to run
        LocationInterpreter.interpretDepth(er, lr);

        LocationInterpreter.interpretContinent(er, lr);
        LocationInterpreter.interpretWaterBody(er, lr);
        LocationInterpreter.interpretMinimumElevationInMeters(er, lr);
        LocationInterpreter.interpretMaximumElevationInMeters(er, lr);
        //Elevation and deption is calucated by min/max depth, elev?
        LocationInterpreter.interpretElevation(er, lr);

        LocationInterpreter.interpretMinimumDistanceAboveSurfaceInMeters(er, lr);
        LocationInterpreter.interpretMaximumDistanceAboveSurfaceInMeters(er, lr);
        LocationInterpreter.interpretCoordinatePrecision(er, lr);
        LocationInterpreter.interpretCoordinateUncertaintyInMeters(er, lr);

        LocationInterpreter.interpretElevation(er,lr);


        //should
        assertEquals(lr.getStateProvince(), "Act");
        assertEquals(lr.getMinimumDepthInMeters(), Double.valueOf(10d));
        assertEquals(lr.getMaximumDepthInMeters(), Double.valueOf(200d));
        //Auto calculated
        assertEquals("Average of Min/Max depth", lr.getDepth(), Double.valueOf(105d));
        assertEquals(lr.getContinent(), "ASIA");
        assertEquals(lr.getWaterBody(), "Murray");
        assertEquals(lr.getMaximumElevationInMeters(), Double.valueOf(2000d));
        assertEquals(lr.getMinimumElevationInMeters(), Double.valueOf(0d));

        assertEquals(lr.getElevation(), Double.valueOf(1000d));
        assertEquals(lr.getMinimumDistanceAboveSurfaceInMeters(), Double.valueOf(14d));
        assertEquals(lr.getMaximumDistanceAboveSurfaceInMeters(), Double.valueOf(200d));
        assertEquals(lr.getCoordinatePrecision(), Double.valueOf(0.5d));
        assertEquals(lr.getCoordinateUncertaintyInMeters(), Double.valueOf(1d));

    }

    @Test
    public void assertionTest() {
        LocationRecord lr = LocationRecord.newBuilder().setId(ID).build();
        Map<String, String> coreMap = new HashMap<>();

        ExtendedRecord er = ExtendedRecord.newBuilder().setId(ID).setCoreTerms(coreMap).build();

        coreMap.put(DwcTerm.coordinatePrecision.qualifiedName(), "100");
        LocationInterpreter.interpretCoordinatePrecision(er, lr);
        assertEquals(lr.getIssues().getIssueList().get(0), "COORDINATE_PRECISION_INVALID");


        coreMap.put(DwcTerm.minimumElevationInMeters.qualifiedName(), " we 0 test 1 inch");
        LocationInterpreter.interpretMinimumElevationInMeters(er,lr);

        coreMap.put(DwcTerm.maximumElevationInMeters.qualifiedName(), " we 1 test 30 meter");
        LocationInterpreter.interpretMaximumElevationInMeters(er,lr);
        LocationInterpreter.interpretElevation(er,lr);

        assertArrayEquals(lr.getIssues().getIssueList().toArray(),new String[]{"COORDINATE_PRECISION_INVALID", "ELEVATION_NOT_METRIC", "ELEVATION_NON_NUMERIC"});


    }
    @Test
    public void assertionElevationTest() {
        LocationRecord lr = LocationRecord.newBuilder().setId(ID).build();
        Map<String, String> coreMap = new HashMap<>();

        ExtendedRecord er = ExtendedRecord.newBuilder().setId(ID).setCoreTerms(coreMap).build();
        coreMap.put(DwcTerm.minimumElevationInMeters.qualifiedName(), "err");
        LocationInterpreter.interpretMinimumElevationInMeters(er,lr);


        coreMap.put(DwcTerm.maximumElevationInMeters.qualifiedName(), " we 1 test 30 meter");
        LocationInterpreter.interpretMaximumElevationInMeters(er,lr);
        LocationInterpreter.interpretElevation(er,lr);

        assertArrayEquals(lr.getIssues().getIssueList().toArray(),new String[]{ "ELEVATION_NON_NUMERIC"});
    }
    @Test
    public void assertionMaximumDistanceAboveSurfaceInMetersTest() {
        LocationRecord lr = LocationRecord.newBuilder().setId(ID).build();
        Map<String, String> coreMap = new HashMap<>();

        ExtendedRecord er = ExtendedRecord.newBuilder().setId(ID).setCoreTerms(coreMap).build();
        coreMap.put(DwcTerm.maximumDistanceAboveSurfaceInMeters.qualifiedName(), "2err0 inch");
        LocationInterpreter.interpretMaximumDistanceAboveSurfaceInMeters(er,lr);

        assertEquals(lr.getMaximumDistanceAboveSurfaceInMeters(), Double.valueOf(0.51d));
    }

    @Test
    public void assertionStateProvinceTest() {
        Location state = new Location();
        state.setCountryName("New South Wales");
        state.setType("State");

        KeyValueTestStoreStub<LatLng, GeocodeResponse> kvStore = new KeyValueTestStoreStub<>();
        kvStore.put(new LatLng(-35d, 149d), new GeocodeResponse(Collections.singletonList(state)));

        GeocodeService service = GeocodeService.create(kvStore);

        LocationRecord lr = LocationRecord.newBuilder().setId(ID).build();
        Map<String, String> coreMap = new HashMap<>();

        ExtendedRecord er = ExtendedRecord.newBuilder().setId(ID).setCoreTerms(coreMap).build();
        coreMap.put(DwcTerm.decimalLatitude.qualifiedName(), "-135d");
        coreMap.put(DwcTerm.decimalLongitude.qualifiedName(), "-149d");
        coreMap.put(DwcTerm.verbatimLongitude.qualifiedName(), "-35d");
        coreMap.put(DwcTerm.verbatimLatitude.qualifiedName(), "149d");

        ALALocationInterpreter.interpretStateProvince(service,er,lr);

        assertEquals(lr.getStateProvince(), "New South Wales");
        assertArrayEquals(lr.getIssues().getIssueList().toArray(),new String[]{ "PRESUMED_SWAPPED_COORDINATE"});

    }

    @Test
    public void assertionMissingLocationTest() {
        Location state = new Location();
        state.setCountryName("New South Wales");
        state.setType("State");

        KeyValueTestStoreStub<LatLng, GeocodeResponse> kvStore = new KeyValueTestStoreStub<>();
        kvStore.put(new LatLng(-35d, 149d), new GeocodeResponse(Collections.singletonList(state)));

        GeocodeService service = GeocodeService.create(kvStore);

        LocationRecord lr = LocationRecord.newBuilder().setId(ID).build();
        Map<String, String> coreMap = new HashMap<>();

        ExtendedRecord er = ExtendedRecord.newBuilder().setId(ID).setCoreTerms(coreMap).build();

        ALALocationInterpreter.interpretStateProvince(service,er,lr);

        assertArrayEquals(lr.getIssues().getIssueList().toArray(),new String[]{ "LOCATION_NOT_SUPPLIED"});

    }

    private class KeyValueTestStoreStub<K, V> implements KeyValueStore<K, V>, Serializable {

        private final Map<K, V> map = new HashMap<>();

        @Override
        public V get(K key) {
            return map.get(key);
        }

        @Override
        public void close() {
            // NOP
        }

        void put(K key, V value) {
            map.put(key, value);
        }
    }


}
