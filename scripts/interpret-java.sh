#!/usr/bin/env bash
source set-env.sh
#!/usr/bin/env bash
echo "interpret verbatim.arvo file."

echo $(date)
SECONDS=0
java -Xmx1g -XX:+UseG1GC  -Dspark.master=local[*]  -cp $PIPELINES_JAR au.org.ala.pipelines.java.ALAVerbatimToInterpretedPipeline \
--datasetId=$1 \
--attempt=1 \
--interpretationTypes=ALL \
--runner=SparkRunner \
--targetPath=$HDFS_PATH/$DATA_DIR \
--inputPath=$HDFS_PATH/$DATA_DIR/$1/1/verbatim.avro \
--metaFileName=interpretation-metrics.yml \
--properties=$HDFS_PATH/pipelines.properties \
--useExtendedRecordId=true \
--coreSiteConfig=$HDFS_CONF \
--hdfsSiteConfig=$HDFS_CONF \
--skipRegisrtyCalls=true

echo $(date)
duration=$SECONDS
echo "Interpretation of $1 took $(($duration / 60)) minutes and $(($duration % 60)) seconds."