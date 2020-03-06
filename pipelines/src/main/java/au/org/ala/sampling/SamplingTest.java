package au.org.ala.sampling;

import java.util.Map;

public class SamplingTest {


    public static void main(String[] args) throws Exception {

        SamplingCacheFactory.setupFor(args[0]);

        SamplingCache cache = SamplingCacheFactory.getForDataset(args[0]);

        Map<String, String> samples = cache.getSamples(Double.parseDouble(args[1]),Double.parseDouble(args[2]));
        if(samples != null) {
            for (Map.Entry<String, String> entry : samples.entrySet()) {
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }
        } else {
            System.out.println("No match for lat lng");
        }
    }
}
