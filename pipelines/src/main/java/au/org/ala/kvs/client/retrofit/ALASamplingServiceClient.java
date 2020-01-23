package au.org.ala.kvs.client.retrofit;

import au.org.ala.kvs.client.ALASamplingRequest;
import au.org.ala.kvs.client.ALASamplingService;
import okhttp3.OkHttpClient;
import org.gbif.rest.client.configuration.ClientConfiguration;
import org.gbif.rest.client.retrofit.RetrofitClientFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.gbif.rest.client.retrofit.SyncCall.syncCall;

public class ALASamplingServiceClient implements ALASamplingService {

    //Wrapped service
    private final ALASamplingRetrofitService alaSamplingService;

    private final OkHttpClient okHttpClient;

    /**
     * Creates an instance using the provided configuration settings.
     * @param clientConfiguration Rest client configuration
     */
    public ALASamplingServiceClient(ClientConfiguration clientConfiguration) {
        okHttpClient = RetrofitClientFactory.createClient(clientConfiguration);
        alaSamplingService = RetrofitClientFactory.createRetrofitClient(okHttpClient,
                clientConfiguration.getBaseApiUrl(),
                ALASamplingRetrofitService.class);
    }

    @Override
    public Map<String,String> sample(ALASamplingRequest request) {
        return syncCall(alaSamplingService.sample(request.getLatitude(), request.getLongitude()));
    }

    @Override
    public void close() throws IOException {
        if (Objects.nonNull(okHttpClient) && Objects.nonNull(okHttpClient.cache())) {
            File cacheDirectory = okHttpClient.cache().directory();
            if (cacheDirectory.exists()) {
                try (Stream<File> files = Files.walk(cacheDirectory.toPath())
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)) {
                    files.forEach(File::delete);
                }
            }
        }
    }

}
