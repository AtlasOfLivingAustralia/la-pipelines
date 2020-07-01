#!/usr/bin/env bash
source set-env.sh

if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi

/data/spark/bin/spark-submit \
--name "Export $1" \
--num-executors 8 \
--executor-cores 8 \
--executor-memory 16G \
--driver-memory 4G \
--class au.org.ala.pipelines.beam.ALAInterpretedToLatLongCSVPipeline  \
--master $SPARK_MASTER \
--driver-java-options "-Dlog4j.configuration=file:/efs-mount-point/log4j.properties" \
$PIPELINES_JAR \
--appName="Lat Long export for $1" \
--datasetId=$1 \
--attempt=1 \
--runner=SparkRunner \
--inputPath=$FS_PATH/$DATA_DIR \
--targetPath=$FS_PATH/$DATA_DIR \
--coreSiteConfig=$HDFS_CONF \
--hdfsSiteConfig=$HDFS_CONF
