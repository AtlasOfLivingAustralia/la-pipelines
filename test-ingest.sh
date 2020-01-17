#!/usr/bin/env bash

java -cp pipelines/target/pipelines-1.0-SNAPSHOT-shaded.jar org.gbif.pipelines.ingest.pipelines.DwcaToVerbatimPipeline \
  --datasetId=dr1411 \
  --attempt=1 \
  --runner=SparkRunner \
  --targetPath=/data/pipelines-data \
  --inputPath=/data/biocache-load/dr1411