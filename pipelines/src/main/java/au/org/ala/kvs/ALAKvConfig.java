package au.org.ala.kvs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

@Value
@Builder
@AllArgsConstructor
public class ALAKvConfig implements Serializable {

    String geocodeBasePath;
    String taxonomyBasePath;
    String spatialBasePath;
    String collectoryBasePath;
    String listsBasePath;
    long timeout;

    public static class ALAKvConfigBuilder {}
}
