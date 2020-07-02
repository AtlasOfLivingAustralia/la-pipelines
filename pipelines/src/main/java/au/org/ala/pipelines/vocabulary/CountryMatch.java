package au.org.ala.pipelines.vocabulary;

import java.util.Optional;

public class CountryMatch {

  private static String countryFile = "/countries.txt";

  public static boolean matched(String countryName) {
    Vocab countryVocab = Vocab.loadVocabFromFile(countryFile);
    return countryVocab.matched(countryName);
  }

  public static Optional<String> match(String countryName) {
    Vocab countryVocab = Vocab.loadVocabFromFile(countryFile);
    return countryVocab.matchTerm(countryName);
  }
}
