package au.org.ala.pipelines.vocabulary;

import java.io.FileNotFoundException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;

/**
 * Load centres of Australia state from resources
 */
@Slf4j
public class StateCentrePoints {
  private static CentrePoints cp;

  public static CentrePoints getInstance(String stateFile) throws FileNotFoundException {
    if (cp == null) {
      cp = CentrePoints.getInstance(stateFile);
      log.info(stateFile + " contains " + cp.size() + " state centres");
    }
    return cp;
  }


}
