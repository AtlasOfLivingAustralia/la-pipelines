#!/usr/bin/env bash
source set-env.sh
java -Xmx8g -Xmx8g  -XX:+UseG1GC -cp $MIGRATION_JAR au.org.ala.pipelines.spark.MigrateUUIDPipeline \
  --inputPath=$DATA_PATH/migration/occ_uuid.csv \
  --targetPath=$DATA_PATH/pipelines-data \
  --hdfsSiteConfig=$HDFS_CONF
