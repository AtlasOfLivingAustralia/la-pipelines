#!/usr/bin/env bash

#PIPELINES_JAR=/efs-mount-point/pipelines.jar
export PIPELINES_JAR="$PWD/../pipelines/target/pipelines-1.0-SNAPSHOT-shaded.jar"

export PIPELINES_CONF="$PWD/pipelines.yaml"

#HDFS_CONF=/efs-mount-point/hdfs-site.xml
export HDFS_CONF=""

#FS_PATH=hdfs://aws-spark-quoll-1.ala:9000
export FS_PATH="/data"

export SPARK_TMP="/data/spark-tmp"

#export SOLR_ZK_HOST="localhost:9983"
export SOLR_ZK_HOST="localhost:9983"

export SOLR_COLLECTION="biocache"

export SPARK_MASTER="NOT-IN-USE"

export DATA_DIR="pipelines-data"

export USE_CLUSTER="FALSE"