package com.example.homoeomirror;


import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @Headers("Content-Type: application/json")
    @POST("groupSetup.php")
    Call<LoginResponse> getOTP(@Query("code") String code,
                              @Query("number") String number);

}
