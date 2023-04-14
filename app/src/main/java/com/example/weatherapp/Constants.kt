/*
 *
 *  Created Prashu Pandey on 4/14/23, 5:56 PM
 *  Copyright (c) 2023 All rights reserved 2023
 *  Last modified: 4/14/23, 5:56 PM
 *
 */

package com.example.weatherapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object Constants {

    const val API_KEY="YOUR_API_KEY"
    const val BASE_URL="https://api.openweathermap.org/data/"
    const val METRIC_UNIT="metric"
    const val PREFERENCE_NAME="WeatherApp"
    const val WEATHER_RESPONSE="Weather_Response"

    fun isNetworkAvailable(context: Context):Boolean{
        val connectivityManager=
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as
                    ConnectivityManager


            val network=connectivityManager.activeNetwork?:return false
            val activeNetwork=connectivityManager.getNetworkCapabilities(network)?:return false

            return when{
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->return true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->return true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ->return true
                else -> false
            }

        }

}


