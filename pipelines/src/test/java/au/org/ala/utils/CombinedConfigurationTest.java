package au.org.ala.utils;
import org.junit.BeforeClass;
import org.junit.Test;

import org.hamcrest.Matchers;

import java.io.FileNotFoundException;
import java.util.LinkedHashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CombinedConfigurationTest {
  private static CombinedYamlConfiguration testConf;

  @BeforeClass
  public static void loadConf() throws FileNotFoundException {
    testConf = new CombinedYamlConfiguration("src/main/resources/la-pipelines.yaml");
  }

  @Test
  public void getUnknowValueReturnsEmptyList() throws FileNotFoundException {
    assertThat(((LinkedHashMap<String, Object>) testConf.subSet("general2")).size(), equalTo(0));
  }
  
  @Test
  public void weCanJoinSeveralConfigsAndConvertToArgs() {
    String[] args = testConf.toArgs("general", "interpret.default", "interpret.spark-embedded");
    // it should be --args=value arrays
    assertThat(args.length, greaterThan(0));
    LinkedHashMap<String, Object> argsInMap = new LinkedHashMap<>();
    for (String arg : args) {
      assertThat(arg.substring(0, 2), equalTo("--"));
      String[] splitted = arg.substring(2).split("=");
      assertThat(splitted.length, equalTo(2));
      argsInMap.put(splitted[0], splitted[1]);
    }
    assertThat(argsInMap.get("interpretationTypes"), equalTo("ALL"));
    assertThat(argsInMap.get("runner"), equalTo("SparkRunner"));
    assertThat(argsInMap.get("attempt"), equalTo("1"));
    assertThat(argsInMap.get("missingVar"), equalTo(null));
    assertThat(argsInMap.get("missing.dot.var"), equalTo(null));
  }

  @Test
  public void weCanJoinSeveralConfigs() {
    LinkedHashMap<String, Object> embedConf = testConf.subSet("general", "interpret.default", "interpret.spark-embedded");
    assertThat(embedConf.get("interpretationTypes"), equalTo("ALL"));
    assertThat(embedConf.get("runner"), equalTo("SparkRunner"));
    assertThat(embedConf.get("attempt"), equalTo(1));
    assertThat(embedConf.get("missingVar"), equalTo(null));
    assertThat(embedConf.get("missing.dot.var"), equalTo(null));
    assertThat(embedConf.get("geocode.api.url"), equalTo("http://127.0.0.1:4444/geocode/%"));
    assertThat(embedConf.get("geocode.api.url"), not("http://just-testing-matchers"));
  }

  @Test
  public void rootVars() {
    assertThat(testConf.subSet("test-delete").getClass(), equalTo(LinkedHashMap.class));
    assertThat(testConf.subSet().get("test-delete"), equalTo(1));
    assertThat(testConf.get("test-delete"), equalTo(1));
  }

  @Test
  public void dotVars() {
    assertThat(testConf.get("index.spark-embedded").getClass() , equalTo(LinkedHashMap.class));
    assertThat(testConf.get("index.spark-embedded.includeSampling"), equalTo(true));
    assertThat(testConf.get("index.spark-embedded.solrCollection"), equalTo("biocache"));
  }
}