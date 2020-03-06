#!/usr/bin/env bash

echo "interpret verbatim.arvo file."

if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi

if [[ ! -d "/data/pipelines-data" ]]
then
    echo "/data/pipelines-data does not exists on your filesystem."
    exit 1
fi

if [[ ! -f "/data/pipelines-data/$1/1/verbatim.avro" ]]
then
    echo "/data/pipelines-data/$1/1/verbatim.avro does not exists on your filesystem. Have you ran ingest ?"
    exit 1
fi

echo $(date)
SECONDS=0
java -Xmx2g -XX:+UseG1GC  -Dspark.master=local[*]  -cp pipelines/target/pipelines-1.0-SNAPSHOT-shaded.jar au.org.ala.pipelines.java.ALAVerbatimToInterpretedPipeline \
--datasetId=$1 \
--attempt=1 \
--interpretationTypes=ALL \
--targetPath=/data/pipelines-data \
--inputPath=/data/pipelines-data/$1/1/verbatim.avro \
--metaFileName=interpretation-metrics.yml \
--properties=pipelines.properties \
--useExtendedRecordId=true \
--skipRegisrtyCalls=true

echo $(date)
duration=$SECONDS
echo "Interpretation of $1 took $(($duration / 60)) minutes and $(($duration % 60)) seconds."