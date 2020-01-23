package au.org.ala.kvs.client;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@JsonDeserialize(builder = ALASamplingRequest.ALASamplingRequestBuilder.class)
@Value
@Builder
@ToString
public class ALASamplingRequest {

    private final Double latitude;
    private final Double longitude;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ALASamplingRequestBuilder {}
}
