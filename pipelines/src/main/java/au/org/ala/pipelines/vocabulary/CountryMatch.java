package au.org.ala.pipelines.vocabulary;

public class CountryMatch {
    private static String countryFile = "/data/pipelines-data/resources/countries.txt";
    private static CountryMatch cm;


    public static boolean matched(String countryName){
        Vocab countryVocab = Vocab.loadVocabFromFile(countryFile);
        return countryVocab.matched(countryName);
    }



}
