package au.org.ala.pipelines.vocabulary;

public class StateCentrePoints {
    static String stateFile = "./src/main/resources/stateProvinceCentrePoints.txt";

    public static boolean coordinatesMatchCentre(String state, double decimalLatitude,  double decimalLongitude){
        CentrePoints cp = CentrePoints.getInstance(stateFile);
        return cp.coordinatesMatchCentre(state, decimalLatitude, decimalLongitude);
    }

}
