#!/usr/bin/env bash
cd solr/conf

echo 'Deleting existing collection'
curl -X GET "http://localhost:8985/solr/admin/collections?action=DELETE&name=avro-test"

echo 'Creating  collection'
curl -X GET "http://localhost:8985/solr/admin/collections?action=CREATE&name=avro-test&numShards=32&maxShardsPerNode=4&replicationFactor=1&collection.configName=biocache"
cd ../..

echo 'Done'



