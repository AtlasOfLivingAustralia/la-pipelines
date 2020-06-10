package au.org.ala.kvs.cache;

import au.org.ala.kvs.client.GeocodeShpIntersectService;
import org.gbif.kvs.KeyValueStore;
import org.gbif.kvs.geocode.LatLng;
import org.gbif.rest.client.geocode.GeocodeResponse;
import org.gbif.rest.client.geocode.GeocodeService;

import java.io.IOException;

public class GeocodeCache2kKeyValueStore implements KeyValueStore<LatLng, GeocodeResponse> {

  private final GeocodeService service;

  private GeocodeCache2kKeyValueStore() {
    this.service = GeocodeShpIntersectService.getInstance();
  }

  public static GeocodeCache2kKeyValueStore create() {
    return new GeocodeCache2kKeyValueStore();
  }

  @Override
  public void close() throws IOException {
  }

  @Override
  public GeocodeResponse get(LatLng latLng) {
    return new GeocodeResponse(service.reverse(latLng.getLatitude(), latLng.getLongitude()));
  }
}
