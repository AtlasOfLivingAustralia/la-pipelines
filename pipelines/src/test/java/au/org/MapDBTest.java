package au.org;

import au.org.ala.kvs.client.ALANameUsageMatch;
import au.org.ala.kvs.client.ALASpeciesMatchRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gbif.kvs.cache.KeyValueCache;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.mapdb.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

public class MapDBTest {

    /**
     * Tests the Get operation on {@link KeyValueCache} that wraps a simple KV store backed by a HashMap.
     */
    @Test
    public void getCacheTest() throws Exception {

        DB db = DBMaker.fileDB("/tmp/species-requests").make();
        ConcurrentMap<ALASpeciesMatchRequest, ALANameUsageMatch> map = db.hashMap("species-requests")
                .valueSerializer(
                    new Serializer<ALANameUsageMatch>() {
                        ObjectMapper objectMapper = new ObjectMapper();
                        @Override
                        public void serialize(@NotNull DataOutput2 dataOutput2, @NotNull ALANameUsageMatch alaNameUsageMatch) throws IOException {
                            objectMapper.writeValue((DataOutput) dataOutput2, alaNameUsageMatch);
                        }
                        @Override
                        public ALANameUsageMatch deserialize(@NotNull DataInput2 dataInput2, int i) throws IOException {
                            return objectMapper.readValue((DataInput) dataInput2, ALANameUsageMatch.class);
                        }
                    }
        ).keySerializer(new Serializer<ALASpeciesMatchRequest>() {
                    ObjectMapper objectMapper = new ObjectMapper();
                    @Override
                    public void serialize(@NotNull DataOutput2 dataOutput2, @NotNull ALASpeciesMatchRequest alaSpeciesMatchRequest) throws IOException {
                        objectMapper.writeValue((DataOutput) dataOutput2, alaSpeciesMatchRequest);
                    }

                    @Override
                    public ALASpeciesMatchRequest deserialize(@NotNull DataInput2 dataInput2, int i) throws IOException {
                        return objectMapper.readValue((DataInput) dataInput2, ALASpeciesMatchRequest.class);
                    }
                }).createOrOpen();

        ALASpeciesMatchRequest speciesMatch = ALASpeciesMatchRequest.builder().scientificName("Acacia").build();
        ALANameUsageMatch match = ALANameUsageMatch.builder().taxonConceptID("urn:acacia").build();

        map.put(speciesMatch, match);



        ALASpeciesMatchRequest speciesMatch2 = ALASpeciesMatchRequest.builder().scientificName("Acacia").build();
        ALANameUsageMatch match2  = map.get(speciesMatch2);


        System.out.println("The match:" + match2.toString());

        db.close();
    }
}
