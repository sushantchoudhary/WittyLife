package com.appdev.schoudhary.wittylife.network;


import com.appdev.schoudhary.wittylife.model.CostRanking;
import com.appdev.schoudhary.wittylife.model.QOLRanking;

import java.util.List;

import io.reactivex.Observable;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface ApiService {

    @GET("rankings_by_city_current?section=12")
    Observable<List<QOLRanking>> getQOLRanking(@Query("api_key") String api_key);

    @GET("rankings_by_city_current?section=1")
    Call<CostRanking> getCostOfLivingRanking(@Query("api_key") String api_key);

    @GET("rankings_by_city_current?section=4")
    Call<CostRanking> getTrafficRanking(@Query("api_key") String api_key);
//
//    @GET("top_rated")
//    Observable<MovieStore> getRxTopRatedMovies(@Query("api_key") String api_key);
//
//    @GET("{movie_id}/videos")
//    Call<MovieVideos> getMovieVideo(@Path("movie_id") int movie_id, @Query("api_key") String api_key);
//
//    @GET("{movie_id}/reviews")
//    Call<MovieReviews> getMovieReview(@Path("movie_id") int movie_id, @Query("api_key") String api_key);



}
