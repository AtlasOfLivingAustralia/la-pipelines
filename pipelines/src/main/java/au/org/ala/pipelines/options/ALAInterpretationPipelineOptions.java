package au.org.ala.pipelines.options;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.gbif.pipelines.ingest.options.InterpretationPipelineOptions;

public interface ALAInterpretationPipelineOptions extends InterpretationPipelineOptions {

    @Description("ALA name matching service endpoint")
    @Default.String("http://localhost:9179")
    String getNameMatchingServiceUrl();

    void setNameMatchingServiceUrl(String nameMatchingServiceUrl);



}
