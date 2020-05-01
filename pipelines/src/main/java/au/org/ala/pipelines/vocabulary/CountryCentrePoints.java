package au.org.ala.pipelines.vocabulary;

public class CountryCentrePoints {
    static String countryFile = "./src/main/resources/countryCentrePoints.txt";

    public static boolean coordinatesMatchCentre(String countryName, double decimalLatitude,  double decimalLongitude){
        CentrePoints cp = CentrePoints.getInstance(countryFile);
        return cp.coordinatesMatchCentre(countryName, decimalLatitude, decimalLongitude);
    }

}
