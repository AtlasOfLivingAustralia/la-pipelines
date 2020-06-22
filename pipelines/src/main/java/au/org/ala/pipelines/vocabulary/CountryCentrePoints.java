package au.org.ala.pipelines.vocabulary;

import lombok.extern.slf4j.Slf4j;
import java.io.InputStream;

@Slf4j
public class CountryCentrePoints {

  static String countryFile = "/countryCentrePoints.txt";
  private static CentrePoints cp;

  public static CentrePoints getInstance() {
    if (cp == null) {
      InputStream in = CountryCentrePoints.class.getResourceAsStream(countryFile);
      cp = CentrePoints.getInstance(in);
      log.info("{} contains {} country centres", countryFile, cp.size());
    }
    return cp;
  }

}
