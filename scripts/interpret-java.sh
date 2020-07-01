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
--targetPath=$FS_PATH/$DATA_DIR \
--inputPath=$FS_PATH/$DATA_DIR/$1/1/verbatim.avro \
--metaFileName=interpretation-metrics.yml \
--properties=$PIPELINES_CONF \
--useExtendedRecordId=true \
--coreSiteConfig=$HDFS_CONF \
--hdfsSiteConfig=$HDFS_CONF

echo $(date)
duration=$SECONDS
echo "Interpretation of $1 took $(($duration / 60)) minutes and $(($duration % 60)) seconds."