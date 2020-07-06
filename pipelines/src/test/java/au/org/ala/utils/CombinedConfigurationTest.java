package au.org.ala.utils;

import org.jetbrains.annotations.NotNull;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.LinkedHashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CombinedConfigurationTest {
  private static CombinedYamlConfiguration testConf;

  @BeforeClass
  public static void loadConf() throws FileNotFoundException {
    testConf =
        new CombinedYamlConfiguration(
            new String[] {
              "--someArg=1",
              "--runner=other",
              "--datasetId=dr893",
              "--config=src/main/resources/la-pipelines.yaml,src/main/resources/la-pipelines.yaml"
            });
  }

  @Test
  public void getUnknownValueReturnsEmptyList() throws FileNotFoundException {
    assertThat(
        new CombinedYamlConfiguration(
                new String[] {"--config=src/main/resources/la-pipelines.yaml"})
            .subSet("general2")
            .size(),
        equalTo(0));
  }

  @Test
  public void weCanJoinSeveralConfigsAndConvertToArgs() {
    String[] args = testConf.toArgs("general", "interpret.default", "interpret.spark-embedded");
    // it should be --args=value arrays
    assertThat(args.length, greaterThan(0));
    LinkedHashMap<String, Object> argsInMap = argsToMap(args);
    assertThat(argsInMap.get("interpretationTypes"), equalTo("ALL"));
    assertThat(argsInMap.get("runner"), equalTo("other")); // as main args has preference
    assertThat(argsInMap.get("attempt"), equalTo("1"));
    assertThat(argsInMap.get("missingVar"), equalTo(null));
    assertThat(argsInMap.get("missing.dot.var"), equalTo(null));
  }

  @NotNull
  private LinkedHashMap<String, Object> argsToMap(String[] args) {
    LinkedHashMap<String, Object> argsInMap = new LinkedHashMap<>();
    for (String arg : args) {
      assertThat(arg.substring(0, 2), equalTo("--"));
      String[] splitted = arg.substring(2).split("=", 2);
      assertThat(splitted.length, equalTo(2));
      argsInMap.put(splitted[0], splitted[1]);
    }
    return argsInMap;
  }

  @Test
  public void weCanJoinSeveralConfigsAndConvertToArgsWithParams() {
    String[] args = testConf.toArgs("general", "interpret.default", "interpret.spark-cluster");
    LinkedHashMap<String, Object> argsInMap = argsToMap(args);
    assertThat(argsInMap.get("name"), equalTo("interpret dr893"));
    assertThat(argsInMap.get("appName"), equalTo("Interpretation for dr893"));
    assertThat(argsInMap.get("inputPath"), equalTo("/data/pipelines-data/dr893/1/verbatim.avro"));
  }

  @Test
  public void weCanJoinSeveralConfigs() {
    LinkedHashMap<String, Object> embedConf =
        testConf.subSet("general", "services", "interpret.default", "interpret.spark-embedded");
    assertThat(embedConf.get("interpretationTypes"), equalTo("ALL"));
    assertThat(embedConf.get("runner"), equalTo("other")); // as main args has preference
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
    assertThat(testConf.get("index.spark-embedded").getClass(), equalTo(LinkedHashMap.class));
    assertThat(testConf.get("index.spark-embedded.includeSampling"), equalTo(true));
    assertThat(testConf.get("index.spark-embedded.solrCollection"), equalTo("biocache"));
  }

  @Test
  public void testEmptyVarNotNull() {
    assertThat(testConf.get("general.hdfsSiteConfig"), equalTo(""));
  }
}
