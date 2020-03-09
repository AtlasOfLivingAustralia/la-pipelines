#!/usr/bin/env bash

if [ $# -eq 0 ]
  then
    echo "Please supply a data resource UID"
    exit 1
fi

./dwca-arvo.sh $1
groovy interpret.groovy $1
groovy export-latlng.groovy $1
./sample.sh $1
./sample-cache.sh $1
groovy index.groovy $1

curl -X GET "http://aws-quoll-1.ala:8983/solr/admin/collections?action=RELOAD&name=biocache"