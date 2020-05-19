#!/usr/bin/env bash

if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi

java -Xmx8g -Xmx8g -XX:+UseG1GC  -cp pipelines/target/pipelines-1.0-SNAPSHOT-shaded.jar au.org.ala.pipelines.beam.ALAInterpretedToSolrIndexPipeline \
 --appName="SOLR indexing for $1" \
 --datasetId=$1 \
 --attempt=1 \
 --runner=SparkRunner \
 --inputPath=/data/pipelines-data \
 --targetPath=/data/pipelines-data \
 --metaFileName=indexing-metrics.txt \
 --properties=pipelines.properties \
 --zkHost=localhost:9983 \
 --solrCollection=biocache  \
 --includeSampling=true

curl -X GET "http://localhost:8983/solr/admin/collections?action=RELOAD&name=biocache"
