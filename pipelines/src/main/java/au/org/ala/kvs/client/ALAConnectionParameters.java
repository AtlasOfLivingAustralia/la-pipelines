package au.org.ala.kvs.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@JsonDeserialize(builder = ALAConnectionParameters.ALAConnectionParametersBuilder.class)
@Value
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ALAConnectionParameters {

    String protocol;
    String url;
    List<String> termsForUniqueKey;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ALAConnectionParametersBuilder {}
}
