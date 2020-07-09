package au.org.ala.kvs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class LocationInfoConfig implements Serializable {
  private String countryCentrePointsFile;
  private String stateProvinceCentrePointsFile;
  private String stateProvinceNamesFile;
}
