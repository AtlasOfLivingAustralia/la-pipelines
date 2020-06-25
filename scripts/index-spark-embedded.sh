#!/usr/bin/env bash
source set-env.sh

if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi

java -Xmx8g -Xmx8g -XX:+UseG1GC -cp $PIPELINES_JAR au.org.ala.pipelines.beam.ALAInterpretedToSolrIndexPipeline \
 --appName="SOLR indexing for $1" \
 --datasetId=$1 \
 --attempt=1 \
 --runner=SparkRunner \
 --inputPath=$HDFS_PATH/$DATA_DIR \
 --targetPath=$HDFS_PATH/$DATA_DIR \
 --coreSiteConfig=$HDFS_CONF \
 --hdfsSiteConfig=$HDFS_CONF \
 --metaFileName=indexing-metrics.yml \
 --properties=pipelines.properties \
 --zkHost=$SOLR_ZK_HOST \
 --solrCollection=biocache  \
 --includeSampling=true

curl -X GET "http://localhost:8983/solr/admin/collections?action=RELOAD&name=biocache"
