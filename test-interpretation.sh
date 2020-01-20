#!/usr/bin/env bash

java -Xmx8g -Xmx8g -XX:+UseG1GC  -cp pipelines/target/pipelines-1.0-SNAPSHOT-shaded.jar au.org.ala.pipelines.beam.ALAVerbatimToInterpretedPipeline \
--datasetId=dr1411 \
--attempt=1 \
--interpretationTypes=ALL \
--runner=SparkRunner \
--targetPath=/data/pipelines-data \
--inputPath=/data/pipelines-data/dr1411/1/verbatim.avro \
--metaFileName=interpretation-metrics.txt \
--properties=pipelines.properties \
--useExtendedRecordId=true \
--skipRegisrtyCalls=true
