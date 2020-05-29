#!/usr/bin/env bash
rootDir=/data/biocache-load
# !!!!!! need to run this command on one vm then run the script !!!!
# rm -rf $rootDir/dr*.locked
echo "#### DWCA-AVRO #####"
SECONDS=0
tempDirs=()
for file in $rootDir/dr*; do
  if [[ -d $file ]] && [[ $file =~ ${rootDir}/dr[0-9]+$ ]]; then
    datasetID=$(basename $file)
    if mkdir "$file.lockdir"; then
      tempDirs+=("$file.lockdir")
      # you now have the exclusive lock
      echo "[DWCA-AVRO] Starting dwca avro conversion for $datasetID....."
      ./dwca-avro.sh $datasetID
      echo "[DWCA-AVRO] Finished dwca avro conversion for $datasetID."
      mkdir "$file.processed"
    else
      echo "[DWCA-AVRO] Skipping dwca avro conversion for $datasetID....."
    fi
  fi
done
duration=$SECONDS
echo "#### DWCA-AVRO - DWCA load of all took $(($duration / 60)) minutes and $(($duration % 60)) seconds."