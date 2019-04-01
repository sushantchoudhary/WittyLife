package com.appdev.schoudhary.wittylife.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {

    private static final String BASE_URL = "https://www.numbeo.com/api/";

    private static final String BASE_UNSPLASH_URL = "https://api.unsplash.com/";



    /***
     Get Retrofit Instance
     .addConverterFactory(new NullOnEmptyConverterFactory()) handles null or empty response from network
     */
    private static Retrofit getRetrofitInstance(String baseUrl) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();


        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

    }

    public static ApiService getApiService() {
        return getRetrofitInstance(BASE_URL).create(ApiService.class);
    }

    public static UnsplashApiService getUnsplashApiService() {
        return getRetrofitInstance(BASE_UNSPLASH_URL).create(UnsplashApiService.class);
    }


}
