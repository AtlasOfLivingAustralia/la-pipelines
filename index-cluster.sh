#!/usr/bin/env bash

if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi


echo $(date)
SECONDS=0

/data/spark/bin/spark-submit \
--num-executors 8 \
--executor-cores 8 \
--executor-memory 16G \
--driver-memory 4G \
--class au.org.ala.pipelines.beam.ALAInterpretedToSolrIndexPipeline  \
--master spark://172.30.2.127:7077 \
--driver-java-options "-Dlog4j.configuration=file:/efs-mount-point/log4j.properties" \
/efs-mount-point/pipelines.jar \
--appName="SOLR indexing for $1" \
--datasetId=$1 \
--attempt=1 \
--runner=SparkRunner \
--inputPath=/data/pipelines-data \
--targetPath=/data/pipelines-data \
--metaFileName=indexing-metrics.yml \
--properties=pipelines.properties \
--properties= \
--zkHost=aws-quoll-zoo-1.ala:2181,aws-quoll-zoo-2.ala:2181,aws-quoll-zoo-3.ala:2181,aws-quoll-zoo-4.ala:2181,aws-quoll-zoo-5.ala:2181 \
--solrCollection=biocache

echo $(date)
duration=$SECONDS
echo "Indexing of $1 took $(($duration / 60)) minutes and $(($duration % 60)) seconds."