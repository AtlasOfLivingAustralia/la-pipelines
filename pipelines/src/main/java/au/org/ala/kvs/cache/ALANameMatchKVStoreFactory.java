package au.org.ala.kvs.cache;

import au.org.ala.kvs.ALAKvConfig;
import au.org.ala.kvs.client.ALANameMatchService;
import au.org.ala.kvs.client.ALANameUsageMatch;
import au.org.ala.kvs.client.ALASpeciesMatchRequest;
import au.org.ala.kvs.client.retrofit.ALANameUsageMatchServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.gbif.kvs.KeyValueStore;
import org.gbif.kvs.cache.KeyValueCache;
import org.gbif.kvs.hbase.Command;
import org.gbif.rest.client.configuration.ClientConfiguration;

import java.io.IOException;

@Slf4j
public class ALANameMatchKVStoreFactory {


  /**
   * Returns ala name matching key value store.
   */
  public static KeyValueStore<ALASpeciesMatchRequest, ALANameUsageMatch> alaNameMatchKVStore(
      ClientConfiguration clientConfiguration, ALAKvConfig kvConfig) throws IOException {

    ALANameUsageMatchServiceClient wsClient = new ALANameUsageMatchServiceClient(
        clientConfiguration);
    Command closeHandler = () -> {
      try {
        wsClient.close();
      } catch (Exception e) {
        logAndThrow(e, "Unable to close");
      }
    };

    return cache2kBackedKVStore(wsClient, closeHandler, kvConfig);
  }

  /**
   * Builds a KV Store backed by the rest client.
   */
  private static KeyValueStore<ALASpeciesMatchRequest, ALANameUsageMatch> cache2kBackedKVStore(
      ALANameMatchService nameMatchService, Command closeHandler, ALAKvConfig kvConfig) {

    KeyValueStore kvs = new KeyValueStore<ALASpeciesMatchRequest, ALANameUsageMatch>() {
      @Override
      public ALANameUsageMatch get(ALASpeciesMatchRequest key) {
        try {
          return nameMatchService.match(key);
        } catch (Exception ex) {
          throw logAndThrow(ex, "Error contacting the species match service");
        }
      }

      @Override
      public void close() throws IOException {
        closeHandler.execute();
      }
    };
    return KeyValueCache
        .cache(kvs, kvConfig.getTaxonomyCacheMaxSize(), ALASpeciesMatchRequest.class,
            ALANameUsageMatch.class);
  }

  /**
   * Wraps an exception into a {@link RuntimeException}.
   *
   * @param throwable to propagate
   * @param message to log and use for the exception wrapper
   * @return a new {@link RuntimeException}
   */
  private static RuntimeException logAndThrow(Throwable throwable, String message) {
    log.error(message, throwable);
    return new RuntimeException(throwable);
  }
}
