#!/usr/bin/env bash
#java -Xmx8g -Xmx8g  -XX:+UseG1GC -cp migration/target/migration-1.0-SNAPSHOT-shaded.jar au.org.ala.pipelines.spark.MigrateUUIDPipeline \
#  --inputPath=hdfs://localhost:8020/migration/occ_uuid.csv \
#  --targetPath=hdfs://localhost:8020/pipelines-data \
#  --hdfsSiteConfig=/usr/local/Cellar/hadoop/3.2.1_1/libexec/etc/hadoop/hdfs-site.xml


java -Xmx8g -Xmx8g  -XX:+UseG1GC -cp migration/target/migration-1.0-SNAPSHOT-shaded.jar au.org.ala.pipelines.spark.MigrateUUIDPipeline \
  --inputPath=/data/occ_uuid-dr893.csv \
  --targetPath=/data/pipelines-data