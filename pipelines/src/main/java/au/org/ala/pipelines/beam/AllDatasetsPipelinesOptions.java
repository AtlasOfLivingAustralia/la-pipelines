package au.org.ala.pipelines.beam;

import org.apache.beam.sdk.io.hdfs.HadoopFileSystemOptions;
import org.apache.beam.sdk.options.*;
import org.apache.hadoop.conf.Configuration;
import org.gbif.pipelines.ingest.options.BasePipelineOptions;

import java.util.List;
import java.util.Optional;

public interface AllDatasetsPipelinesOptions  extends PipelineOptions {

    @Description("Path of the input file.")
    String getInputPath();

    void setInputPath(String var1);

    @Description("Target path where the outputs of the pipeline will be written to. Required.")
    @Validation.Required
    @Default.InstanceFactory(org.gbif.pipelines.ingest.options.BasePipelineOptions.DefaultDirectoryFactory.class)
    String getTargetPath();

    void setTargetPath(String var1);

    @Description("Target metadata file name where the outputs of the pipeline metrics results will be written to.")
    String getMetaFileName();

    void setMetaFileName(String var1);

    @Description("If set to true it writes the outputs of every step of the pipeline")
    @Default.Boolean(false)
    boolean getWriteOutput();

    void setWriteOutput(boolean var1);

    @Description("Avro compression type")
    @org.apache.beam.sdk.options.Default.String("snappy")
    String getAvroCompressionType();

    void setAvroCompressionType(String var1);

    @Description("Avro sync interval time")
    @org.apache.beam.sdk.options.Default.Integer(2097152)
    int getAvroSyncInterval();

    void setAvroSyncInterval(int var1);

    @Description("The threshold for java based pipelines, switches between sync and async execution")
    @org.apache.beam.sdk.options.Default.Integer(1000)
    int getSyncThreshold();

    void setSyncThreshold(int var1);

    public static class DefaultDirectoryFactory implements DefaultValueFactory<String> {
        public DefaultDirectoryFactory() {
        }

        static Optional<String> getDefaultFs(PipelineOptions options) {
            List<Configuration> configs = ((HadoopFileSystemOptions)options.as(HadoopFileSystemOptions.class)).getHdfsConfiguration();
            return Optional.ofNullable(configs).filter((x) -> {
                return !x.isEmpty();
            }).map((c) -> {
                return ((Configuration)configs.get(0)).get("fs.defaultFS");
            });
        }

        public String create(PipelineOptions options) {
            return (String)getDefaultFs(options).orElse("hdfs://");
        }
    }
}
