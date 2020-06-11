package com.example.armedconflicts;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/acled/read?terms=accept")
    Call<ApiResponse> getEventById(@Query("data_id") int id);

    @GET("/acled/read?terms=accept&page=1&limit=10")
    Call<ApiResponse> getEventsByCountry(@Query("country") String country);

    @GET("/acled/read?terms=accept")
    Call<ApiResponse> getEventsByRegion(@Query("region") int region, @Query("year") int year, @Query("fatalities") int fatalities, @Query("fatalities_where") String where);

    @GET("/acled/read?terms=accept&event_date_where=>=")
    Call<ApiResponse> getEventsByRegionAndDate(@Query("region") int region, @Query("event_date") String date);

    @GET("/acled/read?terms=accept&event_date_where=>=")
    Call<ApiResponse> getEventsByTypeAndDate(@Query("event_type") String type, @Query("event_date") String date);

    @GET("/acled/read?terms=accept&event_date_where=>")
    Call<ApiResponse> getEventsByDate(@Query("event_date") String date, @Query("page") int page);

    @GET("/acled/read?terms=accept&region=6&region_where=<&event_date_where=>=")
    Call<ApiResponse> getAfricaEventsByDate(@Query("event_date") String date, @Query("page") int page);

    @GET("/acled/read?terms=accept&region=7|11&region_where=BETWEEN&event_date_where=>=")
    Call<ApiResponse> getAsiaEventsByDate(@Query("event_date") String date);
}
