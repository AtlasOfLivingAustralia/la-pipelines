#!/usr/bin/env bash

if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi

SECONDS=0
./dwca-avro.sh $1
./interpret-spark-cluster.sh $1
./export-latlng-cluster.sh $1
./sample.sh $1
./sample-avro-cluster.sh $1
./uuid-spark-cluster.sh  $1
./index-spark-cluster.sh $1

duration=$SECONDS
echo "Full ingest took $(($duration / 60)) minutes and $(($duration % 60)) seconds."

curl -X GET "http://aws-solrcloud-quoll-1.ala:8983/solr/admin/collections?action=RELOAD&name=biocache"

