#!/usr/bin/env bash

source set-env.sh

if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi

echo $(date)
SECONDS=0
java -Xmx8g -XX:+UseG1GC -cp $PIPELINES_JAR au.org.ala.pipelines.java.ALAInterpretedToSolrIndexPipeline \
--datasetId=$1 \
--attempt=1 \
--inputPath=$HDFS_PATH/$DATA_DIR \
--targetPath=$HDFS_PATH/$DATA_DIR \
--metaFileName=indexing-metrics.yml \
--properties=$HDFS_PATH/pipelines.properties \
--includeSampling=true \
--zkHost=$SOLR_ZK_HOST \
--solrCollection=$SOLR_COLLECTION

echo $(date)
duration=$SECONDS
echo "Indexing of $1 took $(($duration / 60)) minutes and $(($duration % 60)) seconds."