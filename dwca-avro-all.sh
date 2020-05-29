#!/usr/bin/env bash
rootDir=/data/biocache-load
echo "#### DWCA-AVRO #####"
SECONDS=0
lockDirs=()
for file in $rootDir/dr*; do
  if [[ -d $file ]] && [[ $file =~ ${rootDir}/dr[0-9]+$ ]]; then
    datasetID=$(basename $file)
    if mkdir "$file.lockdir"; then
      lockDirs+=("$file.lockdir")
      # you now have the exclusive lock
      echo "[DWCA-AVRO] Starting dwca avro conversion for $datasetID....."
      ./dwca-avro.sh $datasetID
      echo "[DWCA-AVRO] Finished dwca avro conversion for $datasetID."
    else
      echo "[DWCA-AVRO] Skipping dwca avro conversion for $datasetID....."
    fi
  fi
done
duration=$SECONDS
echo "#### DWCA-AVRO - DWCA load of all took $(($duration / 60)) minutes and $(($duration % 60)) seconds."
totalDrs=`ls -1q $rootDir/dr*/dr*.zip | wc -l`
processedDrs=`ls -1q $rootDir/dr*.lockdir | wc -l`

echo "#### DWCA-AVRO - Wait till all the DRs get processed successfully."
while [[ "$totalDrs" != "$processedDrs" ]]; do
  sleep 1s
done

for lockDir in "${lockDirs[@]}"; do
  rmdir $lockDir
done
