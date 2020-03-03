#!/usr/bin/env bash

echo "Fully Ingest DwCA - Converts DwCA to AVRO, Interpret, Sample, Index."

if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi

./dwca-arvo.sh $1
./interpret.sh $1
./export-latlng.sh $1
./sample.sh $1
./sample-cache.sh $1
./index.sh $1