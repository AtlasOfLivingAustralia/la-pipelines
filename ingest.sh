#!/usr/bin/env bash

echo "Fully Ingest DwCA - Converts DwCA to AVRO, interpret, Sample, Index."

if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi

./dwca-avro.sh $1
./interpret-spark-embedded.sh $1
./export-latlng.sh $1
./sample.sh $1
./sample-cache.sh $1
./add-sampling-avro.sh $1
./index.sh $1