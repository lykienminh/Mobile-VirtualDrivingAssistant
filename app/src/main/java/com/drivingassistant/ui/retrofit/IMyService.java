package com.drivingassistant.ui.retrofit;

import java.util.Date;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IMyService {
    @POST("register")
    @FormUrlEncoded
    Observable<String> registerUser (@Field("email") String email,
                                     @Field("name") String name,
                                     @Field("password") String password);
    @POST("login")
    @FormUrlEncoded
    Observable<String> loginUser(@Field("email") String email,
                                 @Field("password") String password);

    @POST("users/{id}/history")
    @FormUrlEncoded
    Observable<String> sendHistory(@Path("id") String user_id,
                                   @Field("speed") String speed,
                                   @Field("latitude") String latitude,
                                   @Field("longitude") String longitude,
                                   @Field("traffic_sign") String traffic_sign);

    @POST("users/{id}/result")
    @FormUrlEncoded
    Observable<String> getHistory(@Path("id") String user_id,
                                  @Field("time_start") String time_start,
                                   @Field("time_end") String time_end);
}
