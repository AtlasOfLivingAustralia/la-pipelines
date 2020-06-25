#!/usr/bin/env bash

#PIPELINES_JAR=/efs-mount-point/pipelines.jar
export PIPELINES_JAR="$HOME/dev/la-pipelines/pipelines/target/pipelines-1.0-SNAPSHOT-shaded.jar"

#HDFS_CONF=/efs-mount-point/hdfs-site.xml
export HDFS_CONF="/usr/local/Cellar/hadoop/3.2.1_1/libexec/etc/hadoop/hdfs-site.xml"

#HDFS_PATH=hdfs://aws-spark-quoll-1.ala:9000
export HDFS_PATH="hdfs://localhost:8020"

export SPARK_TMP="/data/spark-tmp"

#export SOLR_ZK_HOST="localhost:9983"
export SOLR_ZK_HOST="localhost:9983"

export SPARK_MASTER="spark://aws-spark-quoll-1.ala:7077"

export DATA_DIR="pipelines-data"