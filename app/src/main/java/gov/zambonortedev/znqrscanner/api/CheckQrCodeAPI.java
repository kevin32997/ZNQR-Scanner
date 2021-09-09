package gov.zambonortedev.znqrscanner.api;

import gov.zambonortedev.znqrscanner.models.QRResponse;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CheckQrCodeAPI {

    @Multipart
    @POST("api/check_quarantine_pass")
    Call<QRResponse> checkQRCodeQP(
            @Part("key") RequestBody key,
            @Part("id") RequestBody qr_code);

    @Multipart
    @POST("api/check_ration_card")
    Call<QRResponse> checkQRCodeRC(
            @Part("key") RequestBody key,
            @Part("id") RequestBody qr_code);
}
