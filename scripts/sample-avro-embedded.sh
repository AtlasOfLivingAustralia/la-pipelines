#!/usr/bin/env bash
source set-env.sh

echo "Add sampling to dataset"

if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi

java -Xmx8g -Xmx8g -XX:+UseG1GC  -cp $PIPELINES_JAR au.org.ala.pipelines.beam.ALASamplingToAvroPipeline \
 --appName="SamplingToAvro indexing for $1" \
 --datasetId=$1\
 --attempt=1 \
 --runner=SparkRunner \
 --inputPath=$HDFS_PATH/pipelines-data \
 --targetPath=$HDFS_PATH/pipelines-data \
 --coreSiteConfig=$HDFS_CONF \
 --hdfsSiteConfig=$HDFS_CONF \
 --metaFileName=indexing-metrics.yml \
 --properties=$HDFS_PATH/pipelines.properties