package au.org.ala.kvs.client.retrofit;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.Map;

public interface ALASamplingRetrofitService {

    @POST("/intersect/{latitude}/{longitude}")
    Call<Map<String,String>> sample(@Path("latitude") Double latitude, @Path("longitude") Double longitude);
}
