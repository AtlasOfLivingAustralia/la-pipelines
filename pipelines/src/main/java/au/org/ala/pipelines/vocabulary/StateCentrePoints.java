package au.org.ala.pipelines.vocabulary;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StateCentrePoints {

  static String stateFile = "/data/pipelines-data/resources/stateProvinceCentrePoints.txt";

  private static CentrePoints cp;

  public static CentrePoints getInstance() {
    if (cp == null) {
      cp = CentrePoints.getInstance(stateFile);
      log.info(stateFile + " contains " + cp.size() + " state centres");
    }
    return cp;
  }


}
