package au.org.ala.pipelines.vocabulary;

import java.io.FileInputStream;
import lombok.extern.slf4j.Slf4j;

import org.gbif.kvs.geocode.LatLng;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * CentrePoints is used by coutryCentres and stateCentres, so it can be a singleton Singleton should
 * be implememend in a conconcrete class like, countryCentres / StateCentres <p> Simulate
 * CentrePoints.scala Compare with predefined state centres in file Format: New South
 * Wales	-31.2532183	146.921099	-28.1561921	153.903718	-37.5052772	140.9992122 Rounded decimal of
 * predefined state centres based on precision of the given coordinates
 *
 * @author Bai187
 */
@Slf4j
public class CentrePoints {

  private Map<String, LatLng> statesCentre = new HashMap();
  private Map<String, BBox> statesBBox = new HashMap();

  private static CentrePoints cp;

  private CentrePoints() {

  }

  public static CentrePoints getInstance(String filePath) {
    try {
      InputStream is = new FileInputStream(new File(filePath));
      getInstance(is);
    } catch (Exception e) {
      log.error(e.getMessage());
    }

    return cp;
  }

  public static CentrePoints getInstance(InputStream is) {
    cp = new CentrePoints();
    try {
      new BufferedReader(new InputStreamReader(is)).lines()
          .map(s -> s.trim())
          .filter(l -> l.split("\t").length == 7)
          .forEach(l -> {
            String[] ss = l.split("\t");
            String state = ss[0].toLowerCase();
            LatLng centre = new LatLng(Double.parseDouble(ss[1]), Double.parseDouble(ss[2]));
            BBox bbox = new BBox(Double.parseDouble(ss[3]), Double.parseDouble(ss[4]),
                Double.parseDouble(ss[5]), Double.parseDouble(ss[6]));
            cp.statesCentre.put(state, centre);
            cp.statesBBox.put(state, bbox);
          });
    } catch (Exception e) {
      log.error(e.getMessage());
    }

    return cp;
  }

  /**
   * Precision of coordinate is determined by the given lat and lng for exmaple, given lat 14.39,
   * will only compare to the second decimal
   */
  public boolean coordinatesMatchCentre(String location, double decimalLatitude,
      double decimalLongitude) {

    LatLng supposedCentre = statesCentre.get(location.toLowerCase());
    if (supposedCentre != null) {
      int latDecPlaces = noOfDecimalPlace(decimalLatitude);
      int longDecPlaces = noOfDecimalPlace(decimalLongitude);

      //approximate the centre points appropriately
      double approximatedLat = round(supposedCentre.getLatitude(), latDecPlaces);
      double approximatedLong = round(supposedCentre.getLongitude(), longDecPlaces);

      //compare approximated centre point with supplied coordinates
      log.debug(decimalLatitude + " " + decimalLongitude + " VS " + approximatedLat + " "
          + approximatedLong);
      return approximatedLat == decimalLatitude && approximatedLong == decimalLongitude;
    } else {
      log.error("{} is not found in records", location);
      return false;
    }
  }

  /**
   * @return size of centres
   */
  public int size() {
    return statesCentre.size();
  }

  private double round(double number, int decimalPlaces) {
    if (decimalPlaces > 0) {
      int x = 1;
      for (int i = 0; i < decimalPlaces; i++) {
        x = x * 10;
      }
      return ((double) (Math.round(number * x))) / x;
    } else {
      return Math.round(number);
    }
  }

  private int noOfDecimalPlace(double number) {
    String numberString = String.valueOf(number);
    int decimalPointLoc = numberString.indexOf(".");
    if (decimalPointLoc < 0) {
      return 0;
    } else {
      return numberString.substring(decimalPointLoc + 1).length();
    }
  }


  public static void main(String[] args) {
    CentrePoints cps = CentrePoints
        .getInstance("pipelines/src/main/resources/stateProvinceCentrePoints.txt");
    boolean result = cps.coordinatesMatchCentre("New South Wales", -31.25, 146.921099);
    System.out.print("Matched:" + result);
  }
}

class BBox {

  private double xmin = Double.POSITIVE_INFINITY;
  private double xmax = Double.NEGATIVE_INFINITY;
  private double ymin = Double.POSITIVE_INFINITY;
  private double ymax = Double.NEGATIVE_INFINITY;


  public BBox(LatLng a, LatLng b) {
    add(a);
    add(b);
  }

  public BBox(BBox copy) {
    this.xmin = copy.xmin;
    this.xmax = copy.xmax;
    this.ymin = copy.ymin;
    this.ymax = copy.ymax;
  }

  public BBox(double a_x, double a_y, double b_x, double b_y) {
    xmin = Math.min(a_x, b_x);
    xmax = Math.max(a_x, b_x);
    ymin = Math.min(a_y, b_y);
    ymax = Math.max(a_y, b_y);
    sanity();
  }

  private void sanity() {
    if (xmin < -180.0) {
      xmin = -180.0;
    }
    if (xmax > 180.0) {
      xmax = 180.0;
    }
    if (ymin < -90.0) {
      ymin = -90.0;
    }
    if (ymax > 90.0) {
      ymax = 90.0;
    }
  }

  public void add(LatLng c) {
    add(c.getLongitude(), c.getLatitude());
  }

  /**
   * Extends this bbox to include the point (x, y)
   */
  public void add(double x, double y) {
    xmin = Math.min(xmin, x);
    xmax = Math.max(xmax, x);
    ymin = Math.min(ymin, y);
    ymax = Math.max(ymax, y);
    sanity();
  }

  public void add(BBox box) {
    add(box.getTopLeft());
    add(box.getBottomRight());
  }

  public double height() {
    return ymax - ymin;
  }

  public double width() {
    return xmax - xmin;
  }

  /**
   * Tests, weather the bbox b lies completely inside this bbox.
   */
  public boolean bounds(BBox b) {
    if (!(xmin <= b.xmin) ||
        !(xmax >= b.xmax) ||
        !(ymin <= b.ymin) ||
        !(ymax >= b.ymax)) {
      return false;
    }
    return true;
  }

  /**
   * Tests, weather the Point c lies within the bbox.
   */
  public boolean bounds(LatLng c) {
    if ((xmin <= c.getLongitude()) &&
        (xmax >= c.getLongitude()) &&
        (ymin <= c.getLatitude()) &&
        (ymax >= c.getLatitude())) {
      return true;
    }
    return false;
  }

  /**
   * Tests, weather two BBoxes intersect as an area. I.e. weather there exists a point that lies in
   * both of them.
   */
  public boolean intersects(BBox b) {
    if (xmin > b.xmax) {
      return false;
    }
    if (xmax < b.xmin) {
      return false;
    }
    if (ymin > b.ymax) {
      return false;
    }
    if (ymax < b.ymin) {
      return false;
    }
    return true;
  }

  /**
   * Returns a list of all 4 corners of the bbox rectangle.
   */
  public List<LatLng> points() {
    LatLng p1 = new LatLng(ymin, xmin);
    LatLng p2 = new LatLng(ymin, xmax);
    LatLng p3 = new LatLng(ymax, xmin);
    LatLng p4 = new LatLng(ymax, xmax);
    List<LatLng> ret = new ArrayList<LatLng>(4);
    ret.add(p1);
    ret.add(p2);
    ret.add(p3);
    ret.add(p4);
    return ret;
  }

  public LatLng getTopLeft() {
    return new LatLng(ymax, xmin);
  }

  public LatLng getBottomRight() {
    return new LatLng(ymin, xmax);
  }

  @Override
  public int hashCode() {
    return (int) (ymin * xmin);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof BBox) {
      BBox b = (BBox) o;
      return b.xmax == xmax && b.ymax == ymax && b.xmin == xmin && b.ymin == ymin;
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return "[ x: " + xmin + " -> " + xmax +
        ", y: " + ymin + " -> " + ymax + " ]";
  }
}
