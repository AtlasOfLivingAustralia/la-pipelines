package au.org.ala.pipelines.vocabulary;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CountryCentrePoints {

  static String countryFile = "/data/pipelines-data/resources/countryCentrePoints.txt";
  private static CentrePoints cp;

  public static CentrePoints getInstance() {
    if (cp == null) {
      cp = CentrePoints.getInstance(countryFile);
      log.info("{} contains {} country centres", countryFile, cp.size());
    }
    return cp;
  }


}
