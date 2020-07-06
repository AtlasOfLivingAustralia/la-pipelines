package au.org.ala.pipelines.vocabulary;

import au.org.ala.pipelines.util.Stemmer;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.google.common.base.Strings;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class StateProvince {

  private static Vocab stateVocab;


  public static Vocab getInstance(String stateVocabFile) throws FileNotFoundException {
    if (stateVocab == null) {
        stateVocab  = Vocab.loadVocabFromFile(stateVocabFile);
    }
    return stateVocab;
  }

  public static boolean matched(String countryName) {
    return stateVocab.matched(countryName);
  }

  public static Optional<String> matchTerm(String countryName) {
    return stateVocab.matchTerm(countryName);
  }

}
