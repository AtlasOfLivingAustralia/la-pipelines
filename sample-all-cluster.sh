#!/usr/bin/env bash

echo $(date)
SECONDS=0

echo 'Step 1. Spark job to export all lat longs'
/data/spark/bin/spark-submit \
--name "Export All" \
--num-executors 8 \
--executor-cores 8 \
--executor-memory 16G \
--driver-memory 4G \
--class au.org.ala.pipelines.beam.ExportAllLatLongCSVPipeline  \
--master spark://172.30.2.127:7077 \
--driver-java-options "-Dlog4j.configuration=file:/efs-mount-point/log4j.properties" \
/efs-mount-point/pipelines.jar \
--appName="Lat Long export" \
--runner=SparkRunner \
--inputPath=/data/pipelines-data \
--targetPath=/data/pipelines-data

echo 'Step 2. Sample all using sampling.ala.org.au'
java -Xmx8g -Xmx8g -XX:+UseG1GC -cp pipelines/target/pipelines-1.0-SNAPSHOT-shaded.jar au.org.ala.sampling.LayerCrawler

echo 'Step 3. Generate sample cache for all'
java -Xmx8g -Xmx8g -XX:+UseG1GC -cp pipelines/target/pipelines-1.0-SNAPSHOT-shaded.jar au.org.ala.sampling.SamplingCacheBuilder /data/pipelines-sampling/

echo $(date)
duration=$SECONDS
echo "Sampling of all data took $(($duration / 60)) minutes and $(($duration % 60)) seconds."