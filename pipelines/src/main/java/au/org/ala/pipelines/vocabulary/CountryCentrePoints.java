package au.org.ala.pipelines.vocabulary;

import java.io.FileNotFoundException;
import lombok.extern.slf4j.Slf4j;
import java.io.InputStream;

@Slf4j
public class CountryCentrePoints {

  private static CentrePoints cp;

  public static CentrePoints getInstance(String countryFile) throws FileNotFoundException {
    if (cp == null) {
      cp = CentrePoints.getInstance(countryFile);
      log.info("{} contains {} country centres", countryFile, cp.size());
    }
    return cp;
  }

}
