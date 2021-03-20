package com.example.spacepictures;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitInterface {
    @GET("apod?api_key=L4juwY6U70NI1errn1qV0OTkPwWMzwPNj2cVHFw4&thumbs=false")
    Call<List<Object>> someResponse(@Query("count") Integer count);
}
