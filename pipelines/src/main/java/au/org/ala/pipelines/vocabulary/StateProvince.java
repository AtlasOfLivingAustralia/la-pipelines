package au.org.ala.pipelines.vocabulary;
import au.org.ala.pipelines.util.Stemmer;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class StateProvince {

    private static StateProvince sp;
    private static String filePath="./src/main/resources/stateProvinces.txt";
    private List<String> states = new ArrayList<String>();


    private StateProvince(){

    }

    public static StateProvince getInstance(String filePath){
        if (sp == null){
            try {
                Stemmer stemmer = new Stemmer();
                sp = new StateProvince();
                Files.lines(new File(filePath).getAbsoluteFile().toPath())
                        .map(s -> s.trim())
                        .forEach(l -> {
                            String[] ss = l.split("\t");
                            for(String s : ss){
                                sp.states.add(stemmer.stem(s));
                            }

                        });
                log.info("State / province " + sp.states.size() + " state(s) have been loaded.");
            }catch(Exception e){
                log.error(e.getMessage());
            }
        }
        return sp;
    }

    public static boolean matchTerm(String state){

        sp = StateProvince.getInstance(filePath);

        String stemmed = new Stemmer().stem(state.toLowerCase());

        return sp.states.contains(stemmed);
    }

    public static void main(String[] args){
         System.out.print("Matched:" + StateProvince.matchTerm("ACT"));
    }


}
