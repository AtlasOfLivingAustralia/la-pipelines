package au.org.ala.parser;
import au.org.ala.kvs.LocationInfoConfig;
import au.org.ala.pipelines.vocabulary.CountryCentrePoints;
import au.org.ala.pipelines.vocabulary.CountryMatch;
import au.org.ala.pipelines.vocabulary.StateCentrePoints;
import au.org.ala.pipelines.vocabulary.StateProvince;
import java.io.FileNotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import au.org.ala.kvs.ALAPipelinesConfig;

import java.util.Optional;

public class LocationResourceParserTest {

  private ALAPipelinesConfig alaConfig;

  @Before
  public void setup(){
    LocationInfoConfig liConfig = new LocationInfoConfig("/data/pipelines-data/resources/countryCentrePoints.txt","/data/pipelines-data/resources/stateProvinceCentrePoints.txt","/data/pipelines-data/resources/stateProvinces.txt");
    alaConfig = new ALAPipelinesConfig();
    alaConfig.setLocationInfoConfig(liConfig);
  }

  @Test
  public void CountryCentreTest() throws FileNotFoundException {
    boolean result = CountryCentrePoints.getInstance(alaConfig.getLocationInfoConfig().getCountryCentrePointsFile()).coordinatesMatchCentre("AUSTRALIA", -29.5328,145.491477);
    Assert.assertEquals(true, result);

    boolean result1 = CountryCentrePoints.getInstance(alaConfig.getLocationInfoConfig().getCountryCentrePointsFile()).coordinatesMatchCentre("AUSTRALIA", -29.53281,145.491477);
    Assert.assertEquals(false, result1);
  }

  @Test
  public void countryNameMatchingTest() throws FileNotFoundException {
    Assert.assertEquals(true, CountryMatch.getInstance(alaConfig.getLocationInfoConfig().getCountryCentrePointsFile()).matched("Australia"));
    Assert.assertEquals(false, CountryMatch.getInstance(alaConfig.getLocationInfoConfig().getCountryCentrePointsFile()).matched("Australi"));
  }

  @Test
  public void stateNameMatchingTest() throws FileNotFoundException {
    Assert.assertEquals(Optional.of("Queensland"), StateProvince.getInstance(alaConfig.getLocationInfoConfig().getStateProvinceNamesFile()).matchTerm("QLD"));
    Assert.assertEquals(Optional.of("Victoria"), StateProvince.getInstance(alaConfig.getLocationInfoConfig().getStateProvinceNamesFile()).matchTerm("VIC"));
  }

  @Test
  public void stateCentreMatchingTest() throws FileNotFoundException {
    Assert.assertEquals(false, StateCentrePoints.getInstance(alaConfig.getLocationInfoConfig().getStateProvinceCentrePointsFile()).coordinatesMatchCentre("UNSW", 10.1, 10.1));
    Assert.assertEquals(true, StateCentrePoints.getInstance(alaConfig.getLocationInfoConfig().getStateProvinceCentrePointsFile()).coordinatesMatchCentre("Northern Territory",	-19.4914108,	132.5509603));
    Assert.assertEquals(true, StateCentrePoints.getInstance(alaConfig.getLocationInfoConfig().getStateProvinceCentrePointsFile()).coordinatesMatchCentre("Western+Australia",	-27.6728168,	121.6283098));
  }
}
