#!/usr/bin/env bash

echo "Ingest DwCA - Converts DwCA to verbatim.arvo file."
data_dir="/data/biocache-load"
if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi

dwca_dir="$data_dir/$1"

if [[ ! -d  $dwca_dir ]]
then
    echo "$dwca_dir does not exists on your filesystem."
    exit 1
fi
files=`ls $data_dir/$1/`
if [[ $files == $1.zip ]]
then
    echo "There is only one file [$1.zip]  in $data_dir/$1/ . Decompressing ..."
    unzip $data_dir/$1/$1.zip -d $data_dir/$1/
fi
java -cp pipelines/target/pipelines-1.0-SNAPSHOT-shaded.jar org.gbif.pipelines.ingest.pipelines.DwcaToVerbatimPipeline \
  --datasetId=$1 \
  --attempt=1 \
  --runner=SparkRunner \
  --metaFileName=dwca-metrics.yml \
  --targetPath=/data/pipelines-data \
  --inputPath=$dwca_dir