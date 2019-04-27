package com.appdev.schoudhary.wittylife.network;

import com.appdev.schoudhary.wittylife.model.DestinationImg;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UnsplashApiService {

    @GET("search/photos?page=1&per_page=1&orientation=landscape")
    Observable<DestinationImg> getDestination(@Query("client_id") String client_id, @Query("query") String query);
}
