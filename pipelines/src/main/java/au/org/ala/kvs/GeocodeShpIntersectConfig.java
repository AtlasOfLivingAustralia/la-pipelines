package au.org.ala.kvs;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Store configuation of SHP files including fields for ALA Country/State interpretation
 */
@AllArgsConstructor
@Data
public class GeocodeShpIntersectConfig implements Serializable {
  String country_shp_file;
  String country_name_field;
  String eez_shp_file;
  String eez_country_name_field;
  String state_shp_file;
  String state_name_field;

    /*GeocodeShpIntersectConfig( String country_shp_file,
        String country_name_field,
        String eez_shp_file,
        String eez_country_name_field,
        String state_shp_file,
        String state_name_field){

      if(Strings.isNullOrEmpty(country_shp_file)||Strings.isNullOrEmpty(country_name_field)||Strings.isNullOrEmpty(eez_shp_file)||Strings.isNullOrEmpty(eez_country_name_field)||Strings.isNullOrEmpty(state_shp_file)||Strings.isNullOrEmpty(state_name_field))
        throw new RuntimeException("You need to provide full information of THREE Shp files for interpreting Country and State ");
      else{
        this.country_shp_file = country_shp_file;
        this.country_name_field=country_name_field;
        this.eez_shp_file=eez_shp_file;
        this.eez_country_name_field=eez_country_name_field;
        this.state_shp_file=state_shp_file;
        this.state_name_field=state_name_field;

      }
    }*/
}