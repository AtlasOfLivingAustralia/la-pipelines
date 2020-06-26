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
java -Xmx8g -XX:+UseG1GC  -Dspark.master=local[*]  -cp $PIPELINES_JAR au.org.ala.pipelines.beam.ALAVerbatimToInterpretedPipeline \
--datasetId=$1 \
--attempt=1 \
--interpretationTypes=ALL \
--runner=SparkRunner \
--targetPath=$FS_PATH/$DATA_DIR \
--inputPath=$FS_PATH/$DATA_DIR/$1/1/verbatim.avro \
--metaFileName=interpretation-metrics.yml \
--properties=$FS_PATH/pipelines.properties \
--useExtendedRecordId=true \
--coreSiteConfig=$HDFS_CONF \
--hdfsSiteConfig=$HDFS_CONF \
--skipRegisrtyCalls=true

echo $(date)
duration=$SECONDS
echo "Interpretation of $1 took $(($duration / 60)) minutes and $(($duration % 60)) seconds."