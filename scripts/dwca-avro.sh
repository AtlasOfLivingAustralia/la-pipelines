#!/usr/bin/env bash
source set-env.sh

echo "Ingest DwCA - Converts DwCA to verbatim.arvo file."

if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi

dwca_dir="/data/biocache-load/$1/$1.zip"

if [[ ! -f  $dwca_dir ]]
then
    echo "$dwca_dir does not exists on your filesystem."
    exit 1
fi

java -Dspark.local.dir=$SPARK_TMP \
-cp $PIPELINES_JAR org.gbif.pipelines.ingest.pipelines.DwcaToVerbatimPipeline \
  --datasetId=$1 \
  --attempt=1 \
  --runner=SparkRunner \
  --metaFileName=dwca-metrics.yml \
  --targetPath=$FS_PATH/$DATA_DIR \
  --hdfsSiteConfig=$HDFS_CONF \
  --coreSiteConfig=$HDFS_CONF \
  --inputPath=$dwca_dir