package au.org.ala.pipelines.vocabulary;

import au.org.ala.pipelines.util.Stemmer;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class StateProvince {

  private static String file = "/stateProvinces.txt";

  public static boolean matched(String countryName) {
    Vocab countryVocab = Vocab.loadVocabFromFile(file);
    return countryVocab.matched(countryName);
  }

  public static Optional<String> matchTerm(String countryName) {
    Vocab countryVocab = Vocab.loadVocabFromFile(file);
    return countryVocab.matchTerm(countryName);
  }
}
