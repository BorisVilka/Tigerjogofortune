package com.fortkto.gametigerone;


import retrofit2.Call;
import retrofit2.http.GET;

public interface API {

    @GET("/vVwdbwHx")
    Call<Answer> getAns();

    @GET("/vVwdbwHx?setting=ok")
    Call<Answer2> getAns2();

}