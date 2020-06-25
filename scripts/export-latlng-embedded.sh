#!/usr/bin/env bash
source set-env.sh

if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi

dwca_dir="/data/pipelines-data/$1"

java -cp $PIPELINES_JAR au.org.ala.pipelines.beam.ALAInterpretedToLatLongCSVPipeline \
 --appName="Lat Long export for $1" \
 --datasetId=$1 \
 --attempt=1 \
 --runner=SparkRunner \
 --inputPath=$HDFS_PATH/$DATA_DIR \
 --targetPath=$HDFS_PATH/$DATA_DIR \
 --coreSiteConfig=$HDFS_CONF \
 --hdfsSiteConfig=$HDFS_CONF

