#!/usr/bin/env bash

java -Xmx8g -Xmx8g -XX:+UseG1GC  -cp pipelines/target/pipelines-1.0-SNAPSHOT-shaded.jar au.org.ala.pipelines.beam.ALAInterpretedToSolrIndexPipeline \
 --datasetId=dr1411 \
 --attempt=1 \
 --runner=SparkRunner \
 --inputPath=/data/pipelines-data \
 --targetPath=/data/pipelines-data \
 --metaFileName=indexing-metrics.txt \
 --properties=pipelines.properties \
 --zkHost=localhost:9983
