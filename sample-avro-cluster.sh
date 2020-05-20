#!/usr/bin/env bash

if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi

if [[ !  -d "/data/pipelines-data" ]]
then
    echo "/data/pipelines-data does not exists on your filesystem."
    exit 1
fi

if [[ !  -f "/data/pipelines-data/$1/1/verbatim.avro" ]]
then
    echo "/data/pipelines-data/$1/1/verbatim.avro does not exists on your filesystem. Have you ran ingest ?"
    exit 1
fi

echo $(date)
SECONDS=0

/data/spark/bin/spark-submit \
--name "add-sampling $1" \
--conf spark.default.parallelism=192 \
--conf spark.yarn.submit.waitAppCompletion=false \
--num-executors 24 \
--executor-cores 8 \
--executor-memory 7G \
--driver-memory 1G \
--class au.org.ala.pipelines.beam.ALASamplingToAvroPipeline \
--master spark://172.30.1.102:7077 \
--driver-java-options "-Dlog4j.configuration=file:/efs-mount-point/log4j.properties" \
/efs-mount-point/pipelines.jar \
--appName="Add Sampling for $1" \
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

echo $(date)
duration=$SECONDS
echo "Adding sampling to $1 took $(($duration / 60)) minutes and $(($duration % 60)) seconds."