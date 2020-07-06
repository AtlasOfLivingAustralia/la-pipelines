package au.org.ala.kvs;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LocationInfoConfig {
  private String countryNamesFile;
  private String countryCentrePointsFile;
  private String stateProvinceCentrePointsFile;
  private String stateProvinceNamesFile;
}
