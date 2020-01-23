package au.org.ala.pipelines.interpreters;

import java.util.Map;
import java.util.function.BiConsumer;

import au.org.ala.kvs.client.ALASamplingRequest;
import org.gbif.kvs.KeyValueStore;
import org.gbif.pipelines.io.avro.AustraliaSpatialRecord;
import org.gbif.pipelines.io.avro.LocationRecord;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/** Interprets the location of a {@link AustraliaSpatialRecord}. */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ALASamplingInterpreter {

    public static BiConsumer<LocationRecord, AustraliaSpatialRecord> interpret(KeyValueStore<ALASamplingRequest, Map<String, String>> kvStore) {
        return (lr, asr) -> {
            if (kvStore != null) {

                if (lr.getDecimalLongitude() != null && lr.getDecimalLatitude() != null) {
                    ALASamplingRequest request = ALASamplingRequest.builder()
                            .latitude(lr.getDecimalLatitude())
                            .longitude(lr.getDecimalLongitude()).build();
                    Map<String, String> resp = kvStore.get(request);
                    if(resp != null){
                        if (resp !=null && !resp.isEmpty()) {
                            asr.setItems(resp);
                        }
                    }
                }
            }
        };
    }
}