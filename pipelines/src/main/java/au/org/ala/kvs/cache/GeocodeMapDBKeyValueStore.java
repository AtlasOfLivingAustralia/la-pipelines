package au.org.ala.kvs.cache;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.gbif.kvs.KeyValueStore;
import org.gbif.kvs.geocode.LatLng;
import org.gbif.rest.client.configuration.ClientConfiguration;
import org.gbif.rest.client.geocode.GeocodeResponse;
import org.gbif.rest.client.geocode.retrofit.GeocodeServiceSyncClient;

import org.jetbrains.annotations.NotNull;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GeocodeMapDBKeyValueStore implements KeyValueStore<LatLng, GeocodeResponse> {

  private final GeocodeServiceSyncClient service;
  private final DB db;
  private final HTreeMap<LatLng, GeocodeResponse> cache;

  private GeocodeMapDBKeyValueStore(ClientConfiguration config) {
    this.service = new GeocodeServiceSyncClient(config);

    this.db = DBMaker
        .fileDB("/tmp/geocoderesponse")
        .closeOnJvmShutdown()
        .fileMmapEnableIfSupported()
        .make();

    this.cache = db.hashMap("geocoderesponse")
        .keySerializer(new Serializer<LatLng>() {
          @Override
          public void serialize(@NotNull DataOutput2 dataOutput2, @NotNull LatLng latLng) throws IOException {
            String toString = latLng.getLatitude() + "&&&" + latLng.getLongitude();
            dataOutput2.writeChars(toString);
          }

          @Override
          public LatLng deserialize(@NotNull DataInput2 dataInput2, int i) throws IOException {
            String[] parts = dataInput2.readLine().split("&&&");
            return LatLng.builder()
                .withLatitude(Double.parseDouble(parts[0]))
                .withLongitude(Double.parseDouble(parts[1]))
                .build();
          }
        }).valueSerializer(new Serializer<GeocodeResponse>() {
          ObjectMapper objectMapper = new ObjectMapper();

          @Override
          public void serialize(@NotNull DataOutput2 dataOutput2, @NotNull GeocodeResponse resp) throws IOException {
            objectMapper.writeValue((DataOutput) dataOutput2, resp);
          }

          @Override
          public GeocodeResponse deserialize(@NotNull DataInput2 dataInput2, int i) throws IOException {
            return objectMapper.readValue((DataInput) dataInput2, GeocodeResponse.class);
          }
        })
        .createOrOpen();
  }

  public static GeocodeMapDBKeyValueStore create(ClientConfiguration config) {
    return new GeocodeMapDBKeyValueStore(config);
  }

  @Override
  public GeocodeResponse get(LatLng key) {
    return cache.computeIfAbsent(key,
        latLng -> new GeocodeResponse(service.reverse(latLng.getLatitude(), latLng.getLongitude())));
  }

  @Override
  public void close() throws IOException {
    cache.close();
    db.close();
    service.close();
  }
}
