#!/usr/bin/env bash
java -Xmx8g -Xmx8g -XX:+UseG1GC -cp pipelines/target/pipelines-1.0-SNAPSHOT-shaded.jar au.org.ala.pipelines.beam.MigrateUUIDPipeline \
  --datasetId=ALL \
  --attempt=1 \
  --runner=SparkRunner \
  --metaFileName=uuid-metrics.yml \
  --targetPath=/data/pipelines-data \
  --inputPath=/data/pipelines-data/occ_uuid.csv