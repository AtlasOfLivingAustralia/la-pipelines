package au.org.ala.kvs.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@JsonDeserialize(builder = ALACollectoryMetadata.ALACollectoryMetadataBuilder.class)
@Value
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ALACollectoryMetadata {

    String name;
    String acronym;
    String uid;
    String licenseType;
    String licenseVersion;
    ALAConnectionParameters connectionParameters;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ALACollectoryMetadataBuilder {}
}
