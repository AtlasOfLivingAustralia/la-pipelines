#!/usr/bin/env bash
source set-env.sh

java -cp $PIPELINES_JAR au.org.ala.utils.DumpDatasetSize \
--hdfsSiteConfig=$HDFS_CONF \
--inputPath=/$DATA_DIR/ \
--targetPath=/tmp/dataset-counts.csv

while IFS=, read -r datasetID recordCount
do
    echo "Dataset = $datasetID and count = $recordCount"
    if [ "$recordCount" -gt "50000" ]; then
      if [ "$USE_CLUSTER" == "TRUE" ]; then
        ./interpret-spark-cluster.sh $datasetID
      else
        ./interpret-spark-embedded.sh $datasetID
      fi
    else
      ./interpret-java.sh $datasetID
    fi
done < /tmp/dataset-counts.csv

