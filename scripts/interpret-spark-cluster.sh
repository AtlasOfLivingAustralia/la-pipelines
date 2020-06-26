#!/usr/bin/env bash
source set-env.sh

echo "interpret verbatim.arvo file."

if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi

echo $(date)
SECONDS=0
/data/spark/bin/spark-submit \
--name "interpret $1" \
--conf spark.default.parallelism=144 \
--num-executors 16 \
--executor-cores 8 \
--executor-memory 7G \
--driver-memory 1G \
--class au.org.ala.pipelines.beam.ALAVerbatimToInterpretedPipeline \
--master $SPARK_MASTER \
--driver-java-options "-Dlog4j.configuration=file:/efs-mount-point/log4j.properties" \
$PIPELINES_JAR \
--appName="Interpretation for $1" \
--datasetId=$1 \
--attempt=1 \
--interpretationTypes=ALL \
--runner=SparkRunner \
--targetPath=$FS_PATH/$DATA_DIR \
--inputPath=$FS_PATH/$DATA_DIR/$1/1/verbatim.avro \
--metaFileName=interpretation-metrics.yml \
--properties=$FS_PATH/pipelines.properties \
--coreSiteConfig=$HDFS_CONF \
--hdfsSiteConfig=$HDFS_CONF \
--useExtendedRecordId=true \
--skipRegisrtyCalls=true
echo $(date)
duration=$SECONDS
echo "INTERPRET $1 took $(($duration / 60)) minutes and $(($duration % 60)) seconds."

