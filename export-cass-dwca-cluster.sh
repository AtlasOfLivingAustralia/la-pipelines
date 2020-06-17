#!/usr/bin/env bash

dwca_dir="/data/biocache-load"

/data/spark/bin/spark-submit \
--name "Export Cassandra to Dwca" \
--num-executors 8 \
--executor-cores 8 \
--executor-memory 16G \
--driver-memory 4G \
--class au.org.ala.pipelines.beam.ExportCassToDwcaPipeline  \
--master spark://172.30.1.102:7077 \
--driver-java-options "-Dlog4j.configuration=file:/efs-mount-point/log4j.properties" \
/efs-mount-point/pipelines.jar \
--appName="Export Cassandra to Dwca" \
--attempt=1 \
--runner=SparkRunner \
--cassandraHosts=aws-cass-cluster-1b.ala,aws-cass-cluster-2b.ala,aws-cass-cluster-3b.ala,aws-cass-cluster-4b.ala \
--table=qid
