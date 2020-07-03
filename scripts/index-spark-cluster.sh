#!/usr/bin/env bash

source set-env.sh

if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi

echo $(date)
SECONDS=0

/data/spark/bin/spark-submit \
--name "SOLR indexing for $1" \
--conf spark.default.parallelism=192 \
--num-executors 24 \
--executor-cores 8 \
--executor-memory 7G \
--driver-memory 4G \
--class au.org.ala.pipelines.beam.ALAInterpretedToSolrIndexPipeline  \
--master $SPARK_MASTER \
--driver-java-options "-Dlog4j.configuration=file:/efs-mount-point/log4j.properties" \
$PIPELINES_JAR \
--appName="SOLR indexing for $1" \
--datasetId=$1 \
--attempt=1 \
--runner=SparkRunner \
--inputPath=$FS_PATH/$DATA_DIR \
--targetPath=$FS_PATH/$DATA_DIR \
--coreSiteConfig=$HDFS_CONF \
--hdfsSiteConfig=$HDFS_CONF \
--metaFileName=indexing-metrics.yml \
--properties=$PIPELINES_CONF \
--includeSampling=true \
--zkHost=$SOLR_ZK_HOST \
--solrCollection=$SOLR_COLLECTION

echo $(date)
duration=$SECONDS
echo "Indexing of $1 took $(($duration / 60)) minutes and $(($duration % 60)) seconds."