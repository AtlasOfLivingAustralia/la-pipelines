package au.org.ala.pipelines.spark;

import org.apache.avro.Schema;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import scala.Tuple4;

import static org.apache.spark.sql.functions.col;

public class MigrateUUIDPipeline {

    public static void main(String[] args) throws Exception {

        System.out.println("Starting spark job to migrate UUIDs");
        Schema schemaAvro = new Schema.Parser().parse(MigrateUUIDPipeline.class.getClassLoader().getResourceAsStream("ala-uuid-record.avsc"));

        System.out.println("Starting spark session");
        SparkSession spark = SparkSession
                .builder()
                .appName("Migration UUIDs")
                .getOrCreate();

        System.out.println("Load CSV");
        Dataset<Row> dataset = spark.read().csv(
                "hdfs://aws-spark-quoll-1.ala:9000/migration/uuid/occ_uuid.csv");
//                "file:///data/pipelines-data/occ_uuid.csv");

        System.out.println("Load UUIDs");
        Dataset<Tuple4<String,String,String,String>> uuidRecords = dataset.filter(new FilterFunction<Row>() {
            @Override
            public boolean call(Row row) throws Exception {
                return StringUtils.isNotEmpty(row.getString(0))
                        && row.getString(0).indexOf("|") > 0
                        && row.getString(0).startsWith("dr");
            }
        }).map(new MapFunction<Row, Tuple4<String, String,String,String>>() {
            @Override
            public Tuple4<String,String,String,String> call(Row row) throws Exception {
                String datasetID = row.getString(0).substring(0, row.getString(0).indexOf("|"));
                return Tuple4.apply(datasetID,
                        datasetID + "_" + row.getString(1),
                        row.getString(0),
                        row.getString(1)
                );
            }
        },  Encoders.tuple(Encoders.STRING(), Encoders.STRING(), Encoders.STRING(), Encoders.STRING()));

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
                .save("hdfs://aws-spark-quoll-1.ala:9000/migration/avro");
//                .save("/data/pipelines-data/ala-uuid");

        //move to correct directories
        System.out.println("Close session");
        spark.close();
        System.out.println("Closed session. Job finished.");
    }
}