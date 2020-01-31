package au.org.ala.kvs.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.util.List;
import java.util.Map;

@JsonDeserialize(builder = ALACollectionMatch.ALACollectionMatchBuilder.class)
@Value
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ALACollectionMatch {

    String collectionUid;
    String collectionName;
    List<String> hubMembership;
    String institutionUid;
    String institutionName;
    Map<String, String> taxonomyCoverageHints;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ALACollectionMatchBuilder {}

}
