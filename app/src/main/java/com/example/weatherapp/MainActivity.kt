/*
 *
 *  Created Prashu Pandey on 4/14/23, 5:56 PM
 *  Copyright (c) 2023 All rights reserved 2023
 *  Last modified: 4/14/23, 5:56 PM
 *
 */

package com.example.weatherapp


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.network.WeatherService
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mProgressDialog: Dialog?=null
    private lateinit var binding:ActivityMainBinding
    private lateinit var mSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mFusedLocationClient=LocationServices.getFusedLocationProviderClient(this)
        mSharedPreferences=getSharedPreferences(Constants.PREFERENCE_NAME,Context.MODE_PRIVATE)
        setupUI()
        if (!isLocationEnabled()) {
            Toast.makeText(
                this,
                "PLEASE TURN ON GPS", Toast.LENGTH_SHORT).show()

            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {
            Dexter.withActivity(this).withPermissions(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                            requestLocationData()
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        Toast.makeText(
                            this@MainActivity,
                            "You have denied location permission, Kindly allow it",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    showRationalDialogForPermission()
                }

            }).onSameThread()
                .check()
        }


    }

    private fun getLocationWeatherDetails(latitude:Double,longitude:Double){
        if(Constants.isNetworkAvailable(this)){
            val retrofit:Retrofit=Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service:WeatherService=retrofit
                .create<WeatherService>(WeatherService::class.java)

            val listCall: Call<WeatherResponse> = service.getWeather(
                latitude,longitude,Constants.METRIC_UNIT,Constants.API_KEY
            )

            showCustomProgressDialog()

            listCall.enqueue(object : Callback<WeatherResponse>{
                @SuppressLint("CommitPrefEdits")
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if(response.isSuccessful){
                        hideProgressDialog()
                        val weatherList: WeatherResponse? =response.body()


                        val weatherResponseJsonString= Gson().toJson(weatherList)

                        val editor=mSharedPreferences.edit()
                        editor.putString(Constants.WEATHER_RESPONSE,weatherResponseJsonString)
                        editor.apply()

                        setupUI()

                    }else{
                        val rc=response.code()
                        when(rc){
                            400->{
                                Log.e("Error 400","Bad Connection")
                            }
                            404->{
                                Log.e("Error 404","Not Found")
                            }else->{
                                Log.e("Error","Generic Error")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    hideProgressDialog()
                    Log.e("Error",t.toString())
                }

            })

        }else{
            Toast.makeText(this,
                "Turn on Internet Connection",
                Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData(){
        val mLocationRequest=LocationRequest()
        mLocationRequest.priority=LocationRequest.PRIORITY_HIGH_ACCURACY
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,mLocationCallback,
        Looper.myLooper())
    }

    private val mLocationCallback=object:LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location?=locationResult.lastLocation
            val latitude=mLastLocation?.latitude
            val longitude=mLastLocation?.longitude
            if (latitude != null) {
                if (longitude != null) {
                    getLocationWeatherDetails(latitude,longitude)
                }
            }

        }
    }

    private fun showRationalDialogForPermission() {
        AlertDialog.Builder(this)
            .setMessage("It looks like you have turned off permission required for functionality")
            .setPositiveButton("Go to Settings") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun isLocationEnabled(): Boolean {

        //provide access to the system location services
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }

    private fun showCustomProgressDialog(){
        mProgressDialog=Dialog(this)
        mProgressDialog!!.setContentView(R.layout.dialog_custom)
        mProgressDialog!!.show()
    }

    private fun hideProgressDialog(){
        if(mProgressDialog!=null){
            mProgressDialog!!.dismiss()
        }
    }
    @SuppressLint("SetTextI18n")
    private fun setupUI(){
        val weatherResponse=mSharedPreferences.getString(Constants.WEATHER_RESPONSE,"")
        if(!weatherResponse.isNullOrEmpty()){
            val gson=Gson()
           val weatherList=gson.fromJson(weatherResponse,WeatherResponse::class.java)
            for (z in weatherList.weather.indices) {
                Log.i("NAMEEEEEEEE", weatherList.weather[z].main)

                binding.tvMain.text = weatherList.weather[z].main
                binding.tvMainDescription.text = weatherList.weather[z].description
                binding.tvTemp.text = weatherList.main.temp.toString() + getUnit(application.resources.configuration.locales.toString())

                binding.tvHumidity.text = weatherList.main.humidity.toString() + "%"
                binding.tvMin.text = weatherList.main.tempMin.toString() + " min"
                binding.tvMax.text = weatherList.main.tempMax.toString() + " max"
                binding.tvSpeed.text = weatherList.wind.speed.toString()
                binding.tvName.text = weatherList.name
                binding.tvCountry.text = weatherList.sys.country
                binding.tvSunriseTime.text = unixTime(weatherList.sys.sunrise.toLong())
                binding.tvSunsetTime.text = unixTime(weatherList.sys.sunset.toLong())

                // Here we update the main icon
                when (weatherList.weather[z].icon) {
                    "01d" -> binding.ivMain.setImageResource(R.drawable.sunny)
                    "02d" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "03d" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "04d" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "04n" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "10d" -> binding.ivMain.setImageResource(R.drawable.rain)
                    "11d" -> binding.ivMain.setImageResource(R.drawable.storm)
                    "13d" -> binding.ivMain.setImageResource(R.drawable.snowflake)
                    "01n" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "02n" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "03n" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "10n" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "11n" -> binding.ivMain.setImageResource(R.drawable.rain)
                    "13n" -> binding.ivMain.setImageResource(R.drawable.snowflake)
                }
            }
        }

    }

    private fun getUnit(value:String):String?{
        var value="°C"
        if("US" == value || "LR"==value|| "MM"==value){
            value="°F"
        }
        return value
    }

    private fun unixTime(time:Long):String?{
        val date= Date(time*1000L)
        val sdf=SimpleDateFormat("hh:mm",Locale.UK)
        sdf.timeZone= TimeZone.getDefault()
        return sdf.format(date)

    }


}