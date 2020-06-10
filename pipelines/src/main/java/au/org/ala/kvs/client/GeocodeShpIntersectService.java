package au.org.ala.kvs.client;

import au.org.ala.layers.intersect.SimpleShapeFile;
import org.gbif.rest.client.geocode.GeocodeService;
import org.gbif.rest.client.geocode.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is a port of the functionality in geocode to using ALA's layer-store
 * (https://github.com/AtlasOfLivingAustralia/layers-store) SimpleShapeFile for intersections.
 *
 * @see SimpleShapeFile
 */
public class GeocodeShpIntersectService implements GeocodeService {

  private SimpleShapeFile countries = null;
  private SimpleShapeFile eez = null;
  private SimpleShapeFile states = null;
  private static final GeocodeShpIntersectService instance = new GeocodeShpIntersectService();

  private GeocodeShpIntersectService() {
    //initialise references to SHP files....
    synchronized (this) {
      countries = new SimpleShapeFile("/data/pipelines-shp/political", "ISO_A2");
      eez = new SimpleShapeFile("/data/pipelines-shp/eez", "ISO2");
      //states = new SimpleShapeFile("/data/pipelines-shp/ibra7_regions", "REG_NAME_6");
      states = new SimpleShapeFile("/data/pipelines-shp/cw_state_poly", "FEATURE");

    }
  }

  public static GeocodeShpIntersectService getInstance() {
    return instance;
  }

  @Override
  public Collection<Location> reverse(Double latitude, Double longitude) {
    List<Location> locations = new ArrayList<Location>();
    String state = states.intersect(longitude, latitude);
    if (state != null) {
      Location l = new Location();
      l.setType("State");
      l.setSource("http://www.naturalearthdata.com");
      l.setCountryName(state);
      l.setIsoCountryCode2Digit(state);
      locations.add(l);
    }
    String value = countries.intersect(longitude, latitude);
    if (value != null) {
      Location l = new Location();
      l.setType("Political");
      l.setSource("http://www.naturalearthdata.com");
      l.setCountryName(value);
      l.setIsoCountryCode2Digit(value);
      locations.add(l);
    } else {
      String eezValue = eez.intersect(longitude, latitude);
      if (eezValue != null) {
        Location l = new Location();
        l.setType("EEZ");
        l.setSource("http://vliz.be/vmdcdata/marbound/");
        l.setCountryName(eezValue);
        l.setIsoCountryCode2Digit(eezValue);
        locations.add(l);
      }
    }
    return locations;
  }

  @Override
  public void close() throws IOException {
  }
}
