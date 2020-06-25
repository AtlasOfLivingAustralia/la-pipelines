#!/usr/bin/env bash
source set-env.sh

echo $(date)
START=0

java -cp $PIPELINES_JAR au.org.ala.utils.DumpDatasetSize \
--hdfsSiteConfig=$HDFS_CONF \
--inputPath=/$DATA_DIR/ \
--targetPath=/tmp/dataset-counts.csv

while IFS=, read -r datasetID recordCount
do
    echo "Dataset = $datasetID and count = $recordCount"
    if [ "$recordCount" -gt "50000" ]; then
      ./interpret-spark-cluster.sh $datasetID
#      ./interpret-spark-embedded.sh $datasetID
    else
      ./interpret-java.sh $datasetID
    fi
done < /tmp/dataset-counts.csv

echo $(date)
duration=START
echo "Interpretation of all resources took $(($duration / 60)) minutes and $(($duration % 60)) seconds."
