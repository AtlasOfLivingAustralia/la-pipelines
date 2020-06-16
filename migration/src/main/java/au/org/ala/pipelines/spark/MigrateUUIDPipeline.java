package au.org.ala.pipelines.spark;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.base.Strings;
import org.apache.avro.Schema;
import org.apache.commons.lang3.StringUtils;
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.LocatedFileStatus;
//import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import scala.Tuple4;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.apache.spark.sql.functions.col;

/**
 * A Spark only pipeline that generates AVRO files for UUIDs based on a CSV export from Cassandra.
 */
@Parameters(separators = "=")
public class MigrateUUIDPipeline implements Serializable {

    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = "--inputPath", description = "Comma-separated list of group names to be run")
    private String inputPath;

    @Parameter(names = "--targetPath", description = "Comma-separated list of group names to be run")
    private String targetPath;

    @Parameter(names = "--hdfsSiteConfig", description = "Debug mode")
    private String hdfsSiteConfig;

    @Parameters(separators = "=")
    private class PipelineArgs {

        @Parameter
        private List<String> parameters = new ArrayList<>();

        @Parameter(names = "--inputPath", description = "Comma-separated list of group names to be run")
        private String inputPath;

        @Parameter(names = "--targetPath", description = "Comma-separated list of group names to be run")
        private String targetPath;

        @Parameter(names = "--hdfsSiteConfig", description = "Debug mode")
        private String hdfsSiteConfig;
    }

    public static void main(String[] args) throws Exception {
        MigrateUUIDPipeline m = new MigrateUUIDPipeline();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(m)
                .build();
        jCommander.parse(args);

        if (m.inputPath == null || m.targetPath == null) {
            jCommander.usage();
            System.exit(1);
        }
        m.run();
    }

    private void run() throws Exception {

        System.out.println("Starting spark job to migrate UUIDs");
        Schema schemaAvro = new Schema.Parser().parse(MigrateUUIDPipeline.class
                .getClassLoader().getResourceAsStream("ala-uuid-record.avsc"));

        System.out.println("Starting spark session");
        SparkSession spark = SparkSession
                .builder()
                .master("local[*]")
                .appName("Migration UUIDs")
                .getOrCreate();

        System.out.println("Load CSV");
        Dataset<Row> dataset = spark.read().csv(inputPath);

        System.out.println("Load UUIDs");
        Dataset<Tuple4<String, String, String, String>> uuidRecords = dataset.filter(new FilterFunction<Row>() {
            @Override
            public boolean call(Row row) throws Exception {
                return StringUtils.isNotEmpty(row.getString(0))
                        && row.getString(0).indexOf("|") > 0
                        && row.getString(0).startsWith("dr");
            }
        }).map(new MapFunction<Row, Tuple4<String, String, String, String>>() {
            @Override
            public Tuple4<String, String, String, String> call(Row row) throws Exception {
                String datasetID = row.getString(0).substring(0, row.getString(0).indexOf("|"));
                return Tuple4.apply(datasetID,
                        datasetID + "_" + row.getString(1),
                        row.getString(0),
                        row.getString(1)
                );
            }
        }, Encoders.tuple(Encoders.STRING(), Encoders.STRING(), Encoders.STRING(), Encoders.STRING()));

        System.out.println("Write AVRO");
        uuidRecords.select(
                col("_1").as("datasetID"),
                col("_2").as("id"),
                col("_3").as("uuid"),
                col("_4").as("uniqueKey")
        ).write()
                .partitionBy("datasetID")
                .format("avro")
                .option("avroSchema", schemaAvro.toString())
                .mode(SaveMode.Overwrite)
                .save(targetPath + "/migration-tmp/avro");

        //move to correct directory structure
        // check if the hdfs-site.xml is provided
        Configuration configuration = new Configuration();
        if (!Strings.isNullOrEmpty(hdfsSiteConfig)) {
            File hdfsSite = new File(hdfsSiteConfig);
            if (hdfsSite.exists() && hdfsSite.isFile()) {
                configuration.addResource(hdfsSite.toURI().toURL());
            }
        }

        //get a list of paths & move to correct directories
        FileSystem fileSystem = FileSystem.get(configuration);
        Path path = new Path(targetPath + "/migration-tmp/avro/");
        RemoteIterator<LocatedFileStatus> iterator = fileSystem.listFiles(path, true);
        List<String> filePaths = new ArrayList<String>();
        while (iterator.hasNext()){

            //datasetID=dr1
            LocatedFileStatus locatedFileStatus = iterator.next();
            Path sourcePath = locatedFileStatus.getPath();
            String fullPath =  sourcePath.toString();

            if (fullPath.lastIndexOf("=") > 0 ) {
                String dataSetID = fullPath.substring(fullPath.lastIndexOf("=") + 1, fullPath.lastIndexOf("/") );

//                System.out.println("Moving file : " + fullPath);
//                System.out.println("Moving to dataSetID : " + dataSetID);

                //move to correct location
                String newPath = targetPath + "/" + dataSetID + "/1/identifiers/ala_uuid/";
//                System.out.println("Moving to directory : " + newPath);
                fileSystem.mkdirs(new Path(newPath));

//                System.out.println("Moving to file : " + newPath + sourcePath.getName());

                Path destination = new Path(newPath + sourcePath.getName());
                boolean successful = fileSystem.rename(sourcePath, destination);
//                System.out.println("##### SUCCESS: " + successful);
            }
        }

        System.out.println("Close session");
        spark.close();
        System.out.println("Closed session. Job finished.");
    }
}

