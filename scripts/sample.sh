#!/usr/bin/env bash
source set-env.sh

if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi

java -Xmx8g -Xmx8g -XX:+UseG1GC -cp $PIPELINES_JAR au.org.ala.sampling.LayerCrawler \
 --datasetId=$1 \
 --config=../configs/la-pipelines.yaml,../configs/la-pipelines-local.yaml
