#!/usr/bin/env bash


java -Xmx8g -Xmx8g -XX:+UseG1GC  -cp pipelines/target/pipelines-1.0-SNAPSHOT-shaded.jar au.org.ala.pipelines.beam.AvroSolrTest \
 --appName="SOLR indexing for $1" \
 --datasetId=$1\
 --attempt=1 \
 --runner=SparkRunner \
 --inputPath=/data/pipelines-data \
 --targetPath=/data/pipelines-data \
 --metaFileName=indexing-metrics.yml \
 --properties=pipelines.properties \
 --zkHost=localhost:9983 \
 --solrCollection=biocache  \
 --includeSampling=true

