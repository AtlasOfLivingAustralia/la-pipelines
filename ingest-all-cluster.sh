#!/bin/bash

echo "#### DWCA-AVRO #####"
SECONDS=0
for file in /data/biocache-load/*
do
    if [[ -d $file ]]; then
        datasetID=$(basename $file)
        echo "[DWCA-AVRO] Starting dwca avro conversion for $datasetID....."
        ./dwca-avro.sh $datasetID
        echo "[DWCA-AVRO] Finished dwca avro conversion for $datasetID."
    fi
done
duration=$SECONDS
echo "#### DWCA-AVRO - DWCA load of all took $(($duration / 60)) minutes and $(($duration % 60)) seconds."


echo "#### INTERPRETATION #####"
SECONDS=0
for file in /data/pipelines-data/*
do
    if [[ -d $file ]]; then
        datasetID=$(basename $file)
        echo "[Interpretation] Starting interpretation for $datasetID....."
        groovy interpret.groovy $datasetID
        echo "[Interpretation] Finished interpretation for $datasetID."
    fi
done
duration=$SECONDS
echo "#### INTERPRETATION - Interpretation of all took $(($duration / 60)) minutes and $(($duration % 60)) seconds."


echo "#### LAT LONG #####"
SECONDS=0
for file in /data/pipelines-data/*
do
    if [[ -d $file ]]; then
        datasetID=$(basename $file)
        echo "[LatLong] Starting lat long export for $datasetID....."
        groovy export-latlng.groovy $datasetID
        echo "[LatLong] Finished lat long export for $datasetID."
    fi
done
duration=$SECONDS
echo "#### LATLONG - Lat Long export of all took $(($duration / 60)) minutes and $(($duration % 60)) seconds."


echo "#### SAMPLING #####"
SECONDS=0
for file in /data/pipelines-data/*
do
    if [[ -d $file ]]; then
        datasetID=$(basename $file)
        echo "[Sampling] Starting sampling for $datasetID....."
        ./sample.sh $datasetID
        echo "[Sampling] Finished sampling for $datasetID."
    fi
done
duration=$SECONDS
echo "#### SAMPLING - Sampling of all took $(($duration / 60)) minutes and $(($duration % 60)) seconds."


echo "#### SAMPLING CACHE #####"
SECONDS=0
for file in /data/pipelines-data/*
do
    if [[ -d $file ]]; then
        datasetID=$(basename $file)
        echo "[Sampling-Cache] Starting sample cache for $datasetID....."
        ./sample-cache.sh $datasetID
        echo "[Sampling-Cache] Finished sample cache for $datasetID."
    fi
done
duration=$SECONDS
echo "#### SAMPLING CACHE - Sample cache builds of all took $(($duration / 60)) minutes and $(($duration % 60)) seconds."

echo "#### UUID MAPPING #####"
SECONDS=0
for file in /data/pipelines-data/*
do
    if [[ -d $file ]]; then
        datasetID=$(basename $file)
        echo "[Uuid-Mapping] Starting uuid mapping for $datasetID....."
        ./uuid-spark-cluster.sh  $1
        echo "[Uuid-Mapping] Finished uuid mapping for $datasetID."
    fi
done
duration=$SECONDS
echo "#### UUID-MAPPING - Uuid mapping builds of all took $(($duration / 60)) minutes and $(($duration % 60)) seconds."

echo "#### INDEXING #####"
SECONDS=0
for file in /data/pipelines-data/*
do
  if [[ -d $file ]]; then
    datasetID=$(basename $file)
    echo "[Indexing] Starting indexing for $datasetID....."
    groovy index.groovy $datasetID
    echo "[Indexing] Finished indexing for $datasetID."
   fi
done
duration=$SECONDS
echo "### INDEXING - Indexing of all took $(($duration / 60)) minutes and $(($duration % 60)) seconds."


echo "#### FINISHED #####"

