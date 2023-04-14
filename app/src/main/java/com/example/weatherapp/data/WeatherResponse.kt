/*
 *
 *  Created Prashu Pandey on 4/14/23, 5:56 PM
 *  Copyright (c) 2023 All rights reserved 2023
 *  Last modified: 4/14/23, 5:56 PM
 *
 */

package com.example.weatherapp.data

import java.io.Serializable

data class WeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Long,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Long,
    val id: Long,
    val name: String,
    val cod: Long
    ): Serializable














