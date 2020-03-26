#!/usr/bin/env bash

echo "Index verbatim.arvo file."

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
java -Xmx8g -XX:+UseG1GC  -Dspark.master=local[*]  -cp pipelines/target/pipelines-1.0-SNAPSHOT-shaded.jar au.org.ala.pipelines.java.ALAInterpretedToSolrIndexPipeline \
--datasetId=$1 \
--attempt=1 \
--inputPath=/data/pipelines-data \
--targetPath=/data/pipelines-data \
--metaFileName=indexing-metrics.yml \
--properties=pipelines.properties \
--includeSampling=true \
--zkHost=localhost:9983 \
--solrCollection=biocache

echo $(date)
duration=$SECONDS
echo "Indexing of $1 took $(($duration / 60)) minutes and $(($duration % 60)) seconds."