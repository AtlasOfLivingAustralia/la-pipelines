#!/usr/bin/env bash

echo "Add sampling to dataset"

if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi

java -Xmx8g -Xmx8g -XX:+UseG1GC  -cp pipelines/target/pipelines-1.0-SNAPSHOT-shaded.jar au.org.ala.pipelines.beam.ALASamplingToAvroPipeline \
 --appName="SamplingToAvro indexing for $1" \
 --datasetId=$1\
 --attempt=1 \
 --runner=SparkRunner \
 --inputPath=/data/pipelines-data \
 --targetPath=/data/pipelines-data \
 --metaFileName=indexing-metrics.yml \
 --properties=pipelines.properties