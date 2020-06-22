package au.org.ala.kvs.client;

import au.org.ala.kvs.ALAKvConfig;
import au.org.ala.kvs.GeocodeShpIntersectConfig;
import au.org.ala.layers.intersect.SimpleShapeFile;
import java.nio.file.Files;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.gbif.rest.client.geocode.GeocodeService;
import org.gbif.rest.client.geocode.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.io.File;

/**
 * This is a port of the functionality in geocode to using ALA's layer-store
 * (https://github.com/AtlasOfLivingAustralia/layers-store) SimpleShapeFile for intersections.
 *
 * @see SimpleShapeFile
 */
@Slf4j
public class GeocodeShpIntersectService implements GeocodeService {

  private SimpleShapeFile countries = null;
  private SimpleShapeFile eez = null;
  private SimpleShapeFile states = null;
  private static GeocodeShpIntersectService instance;



  private GeocodeShpIntersectService(GeocodeShpIntersectConfig config) {
    //initialise references to SHP files....
    synchronized (this) {
      checkResouceFiles(config);
      countries = new SimpleShapeFile(config.getCountry_shp_file(), config.getCountry_name_field());
      eez = new SimpleShapeFile(config.getEez_shp_file(), config.getEez_country_name_field());
      states = new SimpleShapeFile(config.getState_shp_file(), config.getState_name_field());
    }
  }

  private void checkResouceFiles(GeocodeShpIntersectConfig config){
    String error = "";
    if(! new File(config.getCountry_shp_file()+".dbf").exists()) {
      error = String.format("FATAL: SHP file of Country: %s does not exist! Check property file defined in --properties argument!",config.getCountry_shp_file() + ".dbf");
    }
    if(! new File(config.getEez_shp_file()+".dbf").exists()){
      error = String.format("FATAL: SHP file of EEZ: %s does not exist! Check property file defined in --properties argument!",config.getEez_shp_file() + ".dbf");
    }
    if(! new File(config.getState_shp_file()+".dbf").exists()){
      error = String.format("FATAL: SHP file of State: %s does not exist! Check property file defined in --properties argument!",config.getState_shp_file() + ".dbf");
    }

    if(!Strings.isNullOrEmpty(error)){
      error = Strings.LINE_SEPARATOR + Strings.repeat('*',128) + Strings.LINE_SEPARATOR + error +Strings.LINE_SEPARATOR ;
      error += Strings.LINE_SEPARATOR + "The following properties are compulsory for location interpretation:";
      error += Strings.LINE_SEPARATOR + "Those properties need to be defined in a property file given by -- properties argument.";
      error += Strings.LINE_SEPARATOR;
      error += Strings.LINE_SEPARATOR +"\t" + String.format("%-32s%-48s%-32s","country_shp_file","SHP file for country searching.", "Example: //data//pipelines-shp//political (DO NOT INCLUDE extension)");
      error += Strings.LINE_SEPARATOR +"\t" + String.format("%-32s%-48s","country_name_field","SHP field of country name");
      error += Strings.LINE_SEPARATOR +"\t" + String.format("%-32s%-48s%-32s","eez_shp_file","SHP file for country searching.", "Example: /data/pipelines-shp/eez (DO NOT INCLUDE extension)");
      error += Strings.LINE_SEPARATOR +"\t" + String.format("%-32s%-48s","eez_country_name_field","SHP field of country name");
      error += Strings.LINE_SEPARATOR +"\t" + String.format("%-32s%-48s%-32s","state_shp_file","SHP file for state searching.", "Example: /data/pipelines-shp/cw_state_pol (DO NOT INCLUDE extension)");
      error += Strings.LINE_SEPARATOR +"\t" + String.format("%-32s%-48s","state_name_field","SHP field of state name");
      error +=  Strings.LINE_SEPARATOR + Strings.repeat('*',128);
      log.error( error);
      throw new RuntimeException(error);
    }

  }

  public static GeocodeShpIntersectService getInstance(GeocodeShpIntersectConfig config) {
    if(instance == null){
      instance = new GeocodeShpIntersectService(config);
    }
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
