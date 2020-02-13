package au.org.ala.kvs;

import au.org.ala.layers.intersect.SimpleShapeFile;
import org.gbif.rest.client.geocode.GeocodeService;
import org.gbif.rest.client.geocode.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This is a port of the functionality in geocode to using ALA's layer store SimpleShapeFile
 * for intersections.
 */
public class GeocodeShpIntersectService implements GeocodeService {

    SimpleShapeFile countries = null;
    SimpleShapeFile eez = null;
    private static final GeocodeShpIntersectService instance = new GeocodeShpIntersectService();

    private GeocodeShpIntersectService(){
        //initialise references to SHP files....
        synchronized (this) {
            countries = new SimpleShapeFile("/data/pipelines-shp/ne_10m_admin_0_countries/ne_10m_admin_0_countries", "ISO_A2");
            eez = new SimpleShapeFile("/data/pipelines-shp/World_EEZ_v10_20180221/eez_v10", "Territory1");
        }
    }

    public static GeocodeShpIntersectService getInstance(){
        return instance;
    }

    @Override
    public Collection<Location> reverse(Double latitude, Double longitude) {
        String value = countries.intersect(longitude, latitude);
        if (value != null) {
            List<Location> locations = new ArrayList<Location>();
            Location l = new Location();
            l.setType("Political");
            l.setSource("http://www.naturalearthdata.com");
            l.setCountryName(value);
            l.setIsoCountryCode2Digit(value); //FIXME
            locations.add(l);
            return locations;
        }
//        else {
//            String eezValue = eez.intersect(longitude, latitude);
//            if (eezValue != null) {
//                List<Location> locations = new ArrayList<Location>();
//                Location l = new Location();
//                l.setCountryName(eezValue);
//                l.setType("EEZ");
//                l.setSource( "http://vliz.be/vmdcdata/marbound/");
//                l.setIsoCountryCode2Digit(value); //FIXME
//                locations.add(l);
//                return locations;
//            }
//        }
//        System.out.println("[NOTHING] LL " + latitude + "," +  longitude);
        return Collections.emptyList();
    }

    @Override
    public void close() throws IOException { }
}
