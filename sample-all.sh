#!/usr/bin/env bash

echo 'Step 1. Spark job to export all lat longs'
java -cp pipelines/target/pipelines-1.0-SNAPSHOT-shaded.jar au.org.ala.pipelines.beam.ExportAllLatLongCSVPipeline \
 --appName="Lat Long export for datasets" \
 --runner=SparkRunner \
 --inputPath=/data/pipelines-data \
 --targetPath=/data/pipelines-data

echo 'Step 2. Sample all using sampling.ala.org.au'
java -Xmx8g -Xmx8g -XX:+UseG1GC -cp pipelines/target/pipelines-1.0-SNAPSHOT-shaded.jar au.org.ala.sampling.LayerCrawler

echo 'Step 3. Generate sample cache for all'
java -Xmx8g -Xmx8g -XX:+UseG1GC -cp pipelines/target/pipelines-1.0-SNAPSHOT-shaded.jar au.org.ala.sampling.SamplingCacheBuilder /tmp/