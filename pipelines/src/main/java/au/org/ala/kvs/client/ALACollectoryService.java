package au.org.ala.kvs.client;

import retrofit2.http.Path;

import java.io.Closeable;


public interface ALACollectoryService extends Closeable {

    ALACollectoryMetadata lookupDataResource(@Path("dataResourceUid") String dataResourceUid);

    ALACollectionMatch lookupCodes(@Path("institutionCode") String institutionCode, @Path("collectionCode") String collectionCode);
}
