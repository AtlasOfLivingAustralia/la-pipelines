package au.org.ala.sampling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.FileUtils;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * A utility to crawl the ALA layers. Requires an input csv containing lat, lng (no header)
 * and an output directory.
 *
 * TODO This needs improvement to make it a more useable commandline tool with options.
 */
@Slf4j
public class LayerCrawler {

    // TODO: make this configurable
    private static final String BASE_URL = "https://sampling.ala.org.au/";
    // TODO: make this configurable
    private static final String SELECTED_ELS  = "el674,el874,el774,el715,el1020,el713,el893,el598,el1006,el996,el814,el881,el1081,el705,el725,el1038,el728,el882,el889,el843,el726,el747,el793,el891,el737,el894,el1011,el720,el887,el708,el899,el681,el718,el766,el788,el810,el890,el830,el673,el898,el663,el668,el730,el669,el1019,el729,el1023,el721,el865,el879,el683,el680,el867,el892,el740,el816,el711,el845,el1003,el1078,el1079,el862,el591,el860,el866,el886,el995,el819,el772,el1013,el1073,el836,el838,el1007,el1040,el586,el704,el878,el1021,el1037,el1077,el670,el827,el1017,el1055,el876,el682,el746,el760,el787,el844,el1010,el2119,el1012,el719,el870,el672,el789,el948,el1036";
    // TODO: make this configurable
    public static final int BATCH_SIZE = 25000;
    // TODO: make this configurable
    public static final int BATCH_STATUS_SLEEP_TIME = 1000;
    // TODO: make this configurable
    public static final int DOWNLOAD_RETRIES = 5;
    public static final String UNKNOWN_STATUS = "unknown";
    public static final String FINISHED_STATUS = "finished";
    public static final String ERROR_STATUS = "error";

    SamplingService service;

    private static Retrofit retrofit =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .validateEagerly(true)
                    .build();

    public static void main(String[] args) throws Exception  {


        String baseDir = "/data/pipelines-data/";
        String samplingAllDir = "/data/pipelines-sampling/";

        if (args.length == 1) {

            String dataSetID = args[0];

            Instant batchStart = Instant.now();

            //list file in directory
            LayerCrawler lc = new LayerCrawler();

            //delete existing sampling output
            File samplingDir = new File(baseDir + dataSetID + "/1/sampling");
            if (samplingDir.exists()){
                FileUtils.forceDelete(samplingDir);
            }

            //(re)create sampling output directories
            File samples = new File(baseDir + dataSetID + "/1/sampling/downloads");
            FileUtils.forceMkdir(samples);

            File latLngExportDir = new File(baseDir + dataSetID + "/1/latlng");
            if (!latLngExportDir.exists()){
                throw new RuntimeException("LatLng export unavailable. Has LatLng export pipeline been ran ? " + latLngExportDir.getAbsolutePath());
            }

            Stream<File> latLngFiles = Stream.of(latLngExportDir.listFiles());

            String layerList = lc.getRequiredLayers();

            for (Iterator<File> i = latLngFiles.iterator(); i.hasNext(); ) {
                File inputFile = i.next();
                lc.crawl(layerList, inputFile, samples);
            }
            Instant batchFinish = Instant.now();

            log.info("Finished sampling for {}. Time taken {} minutes", dataSetID, Duration.between(batchStart, batchFinish).toMinutes());
        }

        if (args.length == 0) {

            Instant batchStart = Instant.now();
            //list file in directory
            LayerCrawler lc = new LayerCrawler();

            File samples = new File(samplingAllDir + "sampling/downloads");
            FileUtils.forceMkdir(samples);

            Stream<File> latLngFiles = Stream.of(
                new File(samplingAllDir + "latlng/").listFiles()
            );

            String layerList = lc.getRequiredLayers();

            for (Iterator<File> i = latLngFiles.iterator(); i.hasNext(); ) {
                File inputFile = i.next();
                lc.crawl(layerList, inputFile, samples);
            }
            Instant batchFinish = Instant.now();

            log.info("Finished sampling for complete lat lng export. Time taken {} minutes", Duration.between(batchStart, batchFinish).toMinutes());
        }
    }

    public LayerCrawler(){
        log.info("Initialising crawler....");
        this.service = retrofit.create(SamplingService.class);
        log.info("Initialised.");
    }

    public String getRequiredLayers() throws Exception {

        log.info("Retrieving layer list from sampling service");
        List<String> requiredEls = Arrays.asList(SELECTED_ELS.split(","));
        String layers = service.getLayers().execute().body().stream()
                .filter(l -> l.getEnabled())
                .map(l -> String.valueOf(l.getId()))

//                .filter(s -> s.startsWith("cl") || requiredEls.contains(s))
                .collect(Collectors.joining(","));

        log.info("Required layer count {}",  layers.split(",").length);

        return layers;
    }


    public void crawl(String layers, File inputFile, File outputDirectory) throws Exception {

        // partition the coordinates into batches of N to submit
        log.info("Partitioning coordinates from file {}", inputFile.getAbsolutePath());

        Stream<String> coordinateStream = Files.lines(inputFile.toPath());
        Collection<List<String>> partitioned = partition(coordinateStream, BATCH_SIZE);

        for (List<String> partition : partitioned) {

            log.info("Partition size (no of coordinates) : {}", partition.size());
            String coords = String.join(",", partition);

            Instant batchStart = Instant.now();

            // Submit a job to generate a join
            Response<SamplingService.Batch> submit = service.submitIntersectBatch(layers, coords).execute();
            String batchId = submit.body().getBatchId();

            String state = UNKNOWN_STATUS;
            while (!state.equalsIgnoreCase(FINISHED_STATUS)  && !state.equalsIgnoreCase(ERROR_STATUS)) {
                Response<SamplingService.BatchStatus> status = service.getBatchStatus(batchId).execute();
                SamplingService.BatchStatus batchStatus = status.body();
                state = batchStatus.getStatus();

                Instant batchCurrentTime = Instant.now();

                log.info("batch ID {} - status: {} - time elapses {} seconds", batchId, state, Duration.between(batchStart, batchCurrentTime).getSeconds());

                if (!state.equals(FINISHED_STATUS)) {
                    Thread.sleep(BATCH_STATUS_SLEEP_TIME);
                } else {
                    log.info("Downloading sampling batch {}", batchId);

                    downloadFile(outputDirectory, batchId, batchStatus);

                    try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(new File(outputDirectory, batchId + ".zip")))) {
                        ZipEntry entry = zipInputStream.getNextEntry();
                        while (entry != null) {
                            log.info("Unzipping {}", entry.getName());
                            java.nio.file.Path filePath = new File(outputDirectory, batchId + ".csv").toPath();
                            if (!entry.isDirectory()) {
                                unzipFiles(zipInputStream, filePath);
                            } else {
                                Files.createDirectories(filePath);
                            }

                            zipInputStream.closeEntry();
                            entry = zipInputStream.getNextEntry();
                        }
                    }

                    log.info("Sampling done for file {}", inputFile.getAbsolutePath());
                }
            }

            if (state.equals(ERROR_STATUS)){
                log.error("Unable to download batch ID {}", batchId);
                throw new RuntimeException("Unable to complete sampling for dataset. Check the status of sampling service for more details");
            }
        }
    }

    /**
     * Download the batch file with a retries mechanism.
     *
     * @param outputDirectory
     * @param batchId
     * @param batchStatus
     * @return
     * @throws IOException
     */
    private boolean downloadFile(File outputDirectory, String batchId, SamplingService.BatchStatus batchStatus) throws IOException {

        for (int i = 0; i < DOWNLOAD_RETRIES; i++) {

            try {
                try (
                        ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(batchStatus.getDownloadUrl()).openStream());
                        FileOutputStream fileOutputStream = new FileOutputStream(new File(outputDirectory, batchId + ".zip"));
                        FileChannel fileChannel = fileOutputStream.getChannel()
                ) {
                    fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                    return true;
                }

            } catch (IOException e) {
                log.info("Download for batch {} failed, retrying attempt {} of 5", batchId, i);
                continue;
            }
        }
        return false;
    }

    /**
     * Simple client to the ALA sampling service.
     */
    private interface SamplingService {

        /**
         * Return an inventory of layers in the ALA spatial portal
         */
        @GET("sampling-service/fields")
        Call<List<Layer>> getLayers();

        /**
         * Return an inventory of layers in the ALA spatial portal
         */
        @GET("sampling-service/intersect/batch/{id}")
        Call<BatchStatus> getBatchStatus(@Path("id") String id);

        /**
         * Trigger a job to run the intersection and return the job key to poll.
         * @param layerIds The layers of interest in comma separated form
         * @param coordinatePairs The coordinates in lat,lng,lat,lng... format
         * @return The batch submited
         */
        @FormUrlEncoded
        @POST("sampling-service/intersect/batch")
        Call<Batch> submitIntersectBatch(@Field("fids") String layerIds,
                                         @Field("points") String coordinatePairs);

        @JsonIgnoreProperties(ignoreUnknown = true)
        class Layer {

            private String id;
            private Boolean enabled;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }


            public Boolean getEnabled() {
                return enabled;
            }

            public void setEnabled(Boolean enabled) {
                this.enabled = enabled;
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        class Batch {
            private String batchId;

            public String getBatchId() {
                return batchId;
            }

            public void setBatchId(String batchId) {
                this.batchId = batchId;
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        class BatchStatus {
            private String status;
            private String downloadUrl;

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getDownloadUrl() {
                return downloadUrl;
            }

            public void setDownloadUrl(String downloadUrl) {
                this.downloadUrl = downloadUrl;
            }
        }
    }

    /**
     * Util to partition a stream into fixed size windows.
     * See https://e.printstacktrace.blog/divide-a-list-to-lists-of-n-size-in-Java-8/
     */
    private static <T> Collection<List<T>> partition(Stream<T> stream, int size) {
        final AtomicInteger counter = new AtomicInteger(0);
        return stream
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / size))
                .values();
    }

    /**
     * Unzip the file to the path.
     */
    public static void unzipFiles(final ZipInputStream zipInputStream, final java.nio.file.Path unzipFilePath) throws IOException {

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(unzipFilePath.toAbsolutePath().toString()))) {
            byte[] bytesIn = new byte[1024];
            int read = 0;
            while ((read = zipInputStream.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }
}