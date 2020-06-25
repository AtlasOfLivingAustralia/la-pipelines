#!/usr/bin/env bash
source set-env.sh

#java -cp $PIPELINES_JAR au.org.ala.utils.DumpDatasetSize \
#--hdfsSiteConfig=$HDFS_CONF \
#--inputPath=/$DATA_DIR/ \
#--targetPath=/tmp/dataset-counts.csv

while IFS=, read -r datasetID recordCount
do
    echo "Dataset = $datasetID and count = $recordCount"
    if [ "$recordCount" -gt "50000" ]; then
      if [ "$USE_CLUSTER" == "TRUE" ]; then
        ./index-spark-cluster.sh $datasetID
      else
        ./index-spark-embedded.sh $datasetID
      fi
    else
      ./index-java.sh $datasetID
    fi
done < /tmp/dataset-counts.csv

