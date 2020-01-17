package au.org.ala.kvs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class ALAKvConfig {

    String taxonomyBasePath;
    String spatialBasePath;
    String listsBasePath;
    long timeout;

    public static class ALAKvConfigBuilder {}
}
