package au.org.ala.parser;
import au.org.ala.pipelines.vocabulary.CountryCentrePoints;
import au.org.ala.pipelines.vocabulary.CountryMatch;
import au.org.ala.pipelines.vocabulary.StateCentrePoints;
import org.junit.Assert;
import org.junit.Test;

public class LocationResourceParserTest {

  @Test
  public void CountryCentreTest(){
    boolean result = CountryCentrePoints.getInstance().coordinatesMatchCentre("AUSTRALIA", -29.5328,145.491477);
    Assert.assertEquals(true, result);

    boolean result1 = CountryCentrePoints.getInstance().coordinatesMatchCentre("AUSTRALIA", -29.53281,145.491477);
    Assert.assertEquals(false, result1);
  }

  @Test
  public void countryNameMatchingTest(){
    Assert.assertEquals(true, CountryMatch.matched("Australia"));
    Assert.assertEquals(false, CountryMatch.matched("Australi"));
  }

  @Test
  public void stateNameMatchingTest(){
    Assert.assertEquals(false, StateCentrePoints.getInstance().coordinatesMatchCentre("UNSW", 10.1, 10.1));
    Assert.assertEquals(true, StateCentrePoints.getInstance().coordinatesMatchCentre("Northern Territory",	-19.4914108,	132.5509603));
    Assert.assertEquals(true, StateCentrePoints.getInstance().coordinatesMatchCentre("Western+Australia",	-27.6728168,	121.6283098));
  }

}
