package au.org.ala.kvs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

@Value
@Builder
@AllArgsConstructor
public class ALAKvConfig implements Serializable {

    String taxonomyBasePath;
    String spatialBasePath;
    String listsBasePath;
    long timeout;

    public static class ALAKvConfigBuilder {}
}
