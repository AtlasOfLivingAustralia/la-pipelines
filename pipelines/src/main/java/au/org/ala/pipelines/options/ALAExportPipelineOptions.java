package au.org.ala.pipelines.options;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.gbif.pipelines.ingest.options.InterpretationPipelineOptions;

/** Main pipeline options necessary for SOLR index for Living atlases */
public interface ALAExportPipelineOptions extends PipelineOptions, InterpretationPipelineOptions {

    @Description("Cassandra cluster hosts, comma separated list")
    @Default.String("localhost")
    String getCassandraHosts();
    void setCassandraHosts(String cassandraHosts);

    @Description("Export folder")
    @Default.String("/data/biocache-load")
    String getExportFolder();
    void setExportFolder(String exportFolder);

    @Description("Cassandra port")
    @Default.Integer(9042)
    Integer getPort();
    void setPort(Integer port);

    @Description("Cassandra keyspace/database")
    @Default.String("occ")
    String getDatabase();
    void setDatabase(String database);

    @Description("Cassandra columnFamily/table")
    @Default.String("occ")
    String getTable();
    void setTable(String Table);
}