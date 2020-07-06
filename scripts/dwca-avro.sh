#!/usr/bin/env bash
source set-env.sh

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

java -Dspark.local.dir=$SPARK_TMP \
-cp $PIPELINES_JAR au.org.ala.pipelines.beam.DwcaToVerbatimPipeline \
  --datasetId=$1 \
  --config=../pipelines/src/main/resources/la-pipelines.yaml
