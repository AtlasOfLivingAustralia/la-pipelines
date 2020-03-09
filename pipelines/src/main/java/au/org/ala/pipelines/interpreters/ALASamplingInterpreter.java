package au.org.ala.pipelines.interpreters;

import java.util.Map;
import java.util.function.BiConsumer;

import au.org.ala.sampling.SamplingCache;
import org.gbif.pipelines.io.avro.AustraliaSpatialRecord;
import org.gbif.pipelines.io.avro.LocationRecord;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/** Interprets the location of a {@link AustraliaSpatialRecord}. */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ALASamplingInterpreter {

    public static BiConsumer<LocationRecord, AustraliaSpatialRecord> interpret(SamplingCache samplingCache) {
        return (lr, asr) -> {
            if (samplingCache != null) {
                if (lr.getDecimalLongitude() != null && lr.getDecimalLatitude() != null) {
                    try {
                        Map<String, String> resp = samplingCache.getSamples(lr.getDecimalLatitude(), lr.getDecimalLongitude());
                        if (resp != null){
                            if (resp != null && !resp.isEmpty()) {
                                asr.setItems(resp);
                            }
                        }
                    } catch (Exception e){
                        throw new RuntimeException("Unable to retrieve sampling for point "
                                + lr.getDecimalLatitude()
                                + ","
                                + lr.getDecimalLongitude() + "; " + e.getMessage(), e);
                    }
                }
            }
        };
    }
}