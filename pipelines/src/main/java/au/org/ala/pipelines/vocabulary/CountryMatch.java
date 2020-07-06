package au.org.ala.pipelines.vocabulary;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

public class CountryMatch {

  private static Vocab cm;

  public static Vocab getInstance(String countryFile) throws FileNotFoundException{
    if (cm == null) {
      cm = Vocab.loadVocabFromFile(countryFile);
    }
    return cm;
  }

  public static boolean matched(String countryName) {
    return cm.matched(countryName);
  }

  public static Optional<String> match(String countryName) {
    return cm.matchTerm(countryName);
  }
}
