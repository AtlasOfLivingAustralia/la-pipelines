#!/usr/bin/env bash
source set-env.sh

echo "UUID pipeline - Preserve or add UUIDs."

if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi

java -Xmx8g -Xmx8g -XX:+UseG1GC  -cp $PIPELINES_JAR  au.org.ala.pipelines.beam.ALAUUIDMintingPipeline \
 --appName="UUID minting for $1" \
 --datasetId=$1\
 --attempt=1 \
 --runner=SparkRunner \
 --inputPath=$HDFS_PATH/$DATA_DIR \
 --targetPath=$HDFS_PATH/$DATA_DIR \
 --coreSiteConfig=$HDFS_CONF \
 --hdfsSiteConfig=$HDFS_CONF \
 --metaFileName=uuid-metrics.yml \
 --properties=$HDFS_PATH/pipelines.properties