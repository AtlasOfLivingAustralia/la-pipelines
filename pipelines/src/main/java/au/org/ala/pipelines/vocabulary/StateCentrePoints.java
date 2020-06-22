package au.org.ala.pipelines.vocabulary;

import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;

/**
 * Load centres of Australia state from resources
 */
@Slf4j
public class StateCentrePoints {

  static String stateFile = "/stateProvinceCentrePoints.txt";

  private static CentrePoints cp;

  public static CentrePoints getInstance() {
    if (cp == null) {
      InputStream in = StateCentrePoints.class.getResourceAsStream(stateFile);
      cp = CentrePoints.getInstance(in);
      log.info(stateFile + " contains " + cp.size() + " state centres");
    }
    return cp;
  }


}
