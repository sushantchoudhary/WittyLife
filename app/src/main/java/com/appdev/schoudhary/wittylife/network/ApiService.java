package com.appdev.schoudhary.wittylife.network;


import com.appdev.schoudhary.wittylife.model.CityIndices;
import com.appdev.schoudhary.wittylife.model.CityRecords;
import com.appdev.schoudhary.wittylife.model.ClimateData;
import com.appdev.schoudhary.wittylife.model.CostRanking;
import com.appdev.schoudhary.wittylife.model.CrimeData;
import com.appdev.schoudhary.wittylife.model.HealthCareData;
import com.appdev.schoudhary.wittylife.model.QOLRanking;
import com.appdev.schoudhary.wittylife.model.TrafficRanking;

import java.util.List;

import io.reactivex.Observable;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface ApiService {

    @GET("rankings_by_city_current?section=12")
    Observable<List<QOLRanking>> getQOLRanking(@Query("api_key") String api_key);

    @GET("rankings_by_city_current?section=1")
    Observable<List<CostRanking>> getCostOfLivingRanking(@Query("api_key") String api_key);

    @GET("rankings_by_city_current?section=4")
    Observable<List<TrafficRanking>> getTrafficRanking(@Query("api_key") String api_key);

    @GET("city_crime")
    Single<CrimeData> getDestinationCrimeData(@Query("api_key") String api_key, @Query("query") String destination_name);

    @GET("city_healthcare")
    Single<HealthCareData> getDestinationHealthData(@Query("api_key") String api_key, @Query("query") String destination_name);

    @GET("city_pollution")
    Single<ClimateData> getDestinationClimateData(@Query("api_key") String api_key, @Query("query") String destination_name);

    @GET("cities")
    Observable<CityRecords> getCityRecords(@Query("api_key") String api_key);

    @GET("cities")
    Call<CityRecords> getCities(@Query("api_key") String api_key);

    @GET("indices")
    Single<CityIndices> getCityIndices(@Query("api_key") String api_key, @Query("query") String city_name);

}
