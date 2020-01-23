#!/usr/bin/env bash

echo "Ingest DwCA - Converts DwCA to verbatim.arvo file."

if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi

dwca_dir="/data/biocache-load/$1"

if [[ ! -d  $dwca_dir ]]
then
    echo "$dwca_dir does not exists on your filesystem."
    exit 1
fi

java -cp pipelines/target/pipelines-1.0-SNAPSHOT-shaded.jar org.gbif.pipelines.ingest.pipelines.DwcaToVerbatimPipeline \
  --datasetId=$1 \
  --attempt=1 \
  --runner=SparkRunner \
  --targetPath=/data/pipelines-data \
  --inputPath=$dwca_dir