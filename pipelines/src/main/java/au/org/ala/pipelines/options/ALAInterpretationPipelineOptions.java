package au.org.ala.pipelines.options;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.gbif.pipelines.ingest.options.InterpretationPipelineOptions;

public interface ALAInterpretationPipelineOptions extends InterpretationPipelineOptions {

    @Description("Use map DB for caches")
    @Default.Boolean(false)
    boolean isMapDBCacheEnabled();

    void setMapDBCacheEnabled(boolean var1);
}
