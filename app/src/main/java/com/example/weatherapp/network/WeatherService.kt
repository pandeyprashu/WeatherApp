/*
 *
 *  Created Prashu Pandey on 4/14/23, 5:56 PM
 *  Copyright (c) 2023 All rights reserved 2023
 *  Last modified: 4/14/23, 5:56 PM
 *
 */

package com.example.weatherapp.network

import com.example.weatherapp.data.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("2.5/weather")
    fun getWeather(
        @Query("lat")lat:Double,
        @Query("lon")lon:Double,
        @Query("units")units:String?,
        @Query("appid")appid:String?,
    ): Call<WeatherResponse>
}