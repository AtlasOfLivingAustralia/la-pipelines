#!/usr/bin/env bash
java -cp pipelines/target/pipelines-1.0-SNAPSHOT-shaded.jar au.org.ala.pipelines.beam.ExportAllLatLongCSVPipeline \
 --appName="Lat Long export for datasets" \
 --runner=SparkRunner \
 --inputPath=/data/pipelines-data \
 --targetPath=/data/pipelines-data
