#!/usr/bin/env bash

if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi

dwca_dir="/data/pipelines-data/$1"

/data/spark/bin/spark-submit \
--name "Export $1" \
--num-executors 8 \
--executor-cores 8 \
--executor-memory 16G \
--driver-memory 4G \
--class au.org.ala.pipelines.beam.ALAInterpretedToLatLongCSVPipeline  \
--master spark://172.30.2.127:7077 \
--driver-java-options "-Dlog4j.configuration=file:/efs-mount-point/log4j.properties" \
/efs-mount-point/pipelines.jar \
--appName="Lat Long export for $1" \
--datasetId=$1 \
--attempt=1 \
--runner=SparkRunner \
--inputPath=/data/pipelines-data \
--targetPath=/data/pipelines-data
