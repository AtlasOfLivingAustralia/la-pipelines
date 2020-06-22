#!/usr/bin/env bash
/data/spark/bin/spark-submit \
--name "Migrate UUIDs" \
--conf spark.default.parallelism=192 \
--num-executors 24 \
--executor-cores 8 \
--executor-memory 7G \
--driver-memory 1G \
--class au.org.ala.pipelines.spark.MigrateUUIDPipeline \
--master spark://aws-spark-quoll-1.ala:7077 \
--driver-java-options "-Dlog4j.configuration=file:/efs-mount-point/log4j.properties" \
/efs-mount-point/migration.jar \
--inputPath=hdfs://aws-spark-quoll-1.ala:9000/migration/occ_uuid.csv \
--targetPath=hdfs://aws-spark-quoll-1.ala:9000/pipelines-data \
--hdfsSiteConfig=/efs-mount-point/hdfs-site.xml