#!/usr/bin/env bash
#rm -Rf /data/pipelines-data/**/1/identifiers
java -Xmx24g -Xmx24g -XX:+UseG1GC -cp pipelines/target/pipelines-1.0-SNAPSHOT-shaded.jar au.org.ala.pipelines.beam.MigrateUUIDPipeline \
  --datasetId=ALL \
  --attempt=1 \
  --runner=DirectRunner \
  --metaFileName=uuid-metrics.yml \
  --targetPath=/data/pipelines-data \
  --inputPath=/efs-mount-point/occ_uuid.csv
