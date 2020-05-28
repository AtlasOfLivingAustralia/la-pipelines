echo "#### DWCA-AVRO #####"
SECONDS=0
for file in /data/biocache-load/*
do
    if [[ -d $file ]]; then
        datasetID=$(basename $file)
        if mkdir "$file.lockdir"; then
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
rmdir "*.lockdir"
