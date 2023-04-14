/*
 *
 *  Created Prashu Pandey on 4/14/23, 5:56 PM
 *  Copyright (c) 2023 All rights reserved 2023
 *  Last modified: 4/14/23, 5:56 PM
 *
 */

package com.example.weatherapp.data

import java.io.Serializable
data class Weather (
    val id: Long,
    val main: String,
    val description: String,
    val icon: String
) : Serializable