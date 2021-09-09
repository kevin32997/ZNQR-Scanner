package gov.zambonortedev.znqrscanner.api;


import gov.zambonortedev.znqrscanner.models.ServerResponse;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface helloAPI {

    @Multipart
    @POST("api/check_connection")
    Call<ServerResponse> checkConnection(@Part("key")RequestBody key);
}
