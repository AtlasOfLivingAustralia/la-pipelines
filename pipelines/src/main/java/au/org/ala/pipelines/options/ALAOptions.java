package au.org.ala.pipelines.options;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;

public interface ALAOptions {

    @Description("Pipelines cache directory")
    @Default.String("/data/pipelines-cache")
    String getPipelinesCacheDirectory();
    void setPipelinesCacheDirectory(String pipelinesCacheDirectory);
}
