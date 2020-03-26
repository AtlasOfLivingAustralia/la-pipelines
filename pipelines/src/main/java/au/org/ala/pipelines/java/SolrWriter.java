package au.org.ala.pipelines.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpClientUtil;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.NamedList;

import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class SolrWriter<T> {

    private String zkHost;
    private String collection;
    private boolean useSyncMode;
    private Function<T, SolrInputDocument> indexRequestFn;
    private ExecutorService executor;
    private Collection<T> records;
    private int solrMaxBatchSize;

    @SneakyThrows
    public void write() {

        ModifiableSolrParams params = new ModifiableSolrParams();
        CloseableHttpClient httpClient =  HttpClientUtil.createClient(params);

        try (CloudSolrClient client = new CloudSolrClient(zkHost, httpClient)) {

            client.setDefaultCollection(collection);
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            Queue<UpdateRequest> requests = new LinkedList<>();
            requests.add(new UpdateRequest());

            Consumer<T> addIndexRequestFn = br -> Optional.ofNullable(requests.peek())
                    .ifPresent(req -> req.add(indexRequestFn.apply(br)));

            Consumer<UpdateRequest> clientIndexFn = updateRequest -> {
                try {
                    NamedList<Object> updateResponse = client.request(updateRequest);
//                    if (updateResponse.getStatus() != 200 && updateResponse.getStatus() !=201 ) {
//                        log.error("Solr indexing failed with error code: " + updateResponse.getStatus());
//                        throw new RuntimeException("Solr indexing failed with error code: " + updateResponse.getStatus());
//                    }
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                    throw new RuntimeException(ex.getMessage(), ex);
                }
            };

            Runnable pushIntoSolrFn = () -> Optional.ofNullable(requests.poll())
                    .filter(req -> req.getDocuments() != null && req.getDocuments().size() > 0)
                    .ifPresent(req -> {
                        if (useSyncMode) {
                            clientIndexFn.accept(req);
                        } else {
                            futures.add(CompletableFuture.runAsync(() -> clientIndexFn.accept(req), executor));
                        }
                    });

            // Push requests into ES
            for (T t : records) {
                UpdateRequest peek = requests.peek();
                if (peek != null
                        &&
                        (   peek.getDocuments() == null ||
                            peek.getDocuments().size() < solrMaxBatchSize - 1
                        )) {
                    addIndexRequestFn.accept(t);
                } else {
                    addIndexRequestFn.accept(t);
                    pushIntoSolrFn.run();
                    requests.add(new UpdateRequest());
                }
            }

            // Final push
            pushIntoSolrFn.run();

            // Wait for all futures
            if (!useSyncMode) {
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
            }
        }
    }
}