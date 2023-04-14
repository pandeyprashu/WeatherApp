/*
 *
 *  Created Prashu Pandey on 4/14/23, 5:56 PM
 *  Copyright (c) 2023 All rights reserved 2023
 *  Last modified: 4/14/23, 5:56 PM
 *
 */

package com.example.weatherapp



import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper


class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


       Handler(Looper.getMainLooper()).postDelayed({
           startActivity(Intent(this,MainActivity::class.java))
           finish()
       },3000)




    }





}