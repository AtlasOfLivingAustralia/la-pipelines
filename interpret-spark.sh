#!/usr/bin/env bash

if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi

/data/spark/bin/spark-submit \
--name "Interpret $1" \
--num-executors 8 \
--executor-cores 8 \
--executor-memory 16G \
--driver-memory 4G \
--class au.org.ala.pipelines.beam.ALAVerbatimToInterpretedPipeline \
--master spark://172.30.2.127:7077 \
--driver-java-options "-Dlog4j.configuration=file:/efs-mount-point/log4j.properties" \
/efs-mount-point/pipelines.jar \
--appName="Interpretation for $1" \
--datasetId=$1 \
--attempt=1 \
--interpretationTypes=ALL \
--runner=SparkRunner \
--targetPath=/data/pipelines-data \
--inputPath=/data/pipelines-data/$1/1/verbatim.avro \
--metaFileName=interpretation-metrics.yml \
--properties=/efs-mount-point/pipelines.properties \
--useExtendedRecordId=true \
--skipRegisrtyCalls=true