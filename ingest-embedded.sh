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
./sample-avro-embedded.sh $1
./index-spark-embedded.sh $1