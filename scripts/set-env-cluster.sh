#!/usr/bin/env bash

export PIPELINES_JAR="/efs-mount-point/pipelines-hdfs.jar"

export HDFS_CONF="/efs-mount-point/hdfs-site.xml"

export HDFS_PATH="hdfs://aws-spark-quoll-1.ala:9000"

export SPARK_TMP="/data/spark-tmp"

export SOLR_ZK_HOST="aws-zoo-quoll-1.ala:2181,aws-zoo-quoll-2.ala:2181,aws-zoo-quoll-3.ala:2181,aws-zoo-quoll-4.ala:2181,aws-zoo-quoll-5.ala:2181"

export SOLR_COLLECTION="biocache"

export SPARK_MASTER="spark://aws-spark-quoll-1.ala:7077"

export DATA_DIR="pipelines-data"

export USE_CLUSTER="TRUE"