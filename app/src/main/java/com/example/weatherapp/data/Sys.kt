/*
 *
 *  Created Prashu Pandey on 4/14/23, 5:56 PM
 *  Copyright (c) 2023 All rights reserved 2023
 *  Last modified: 4/14/23, 5:56 PM
 *
 */

package com.example.weatherapp.data

import java.io.Serializable
data class Sys (
    val type: Long,
    val id: Long,
    val country: String,
    val sunrise: Long,
    val sunset: Long
) : Serializable