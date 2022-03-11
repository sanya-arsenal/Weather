package com.example.weather.network

import com.example.weather.data.ResultForecastData
import com.example.weather.data.ResultWeatherData
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    @GET("weather?&units=metric&appid=$KEY_API")
    suspend fun getCurrentWeatherByCityId(
        @Query("id") id: Int,
        @Query("lang") lang: String
    ): ResultWeatherData

    @GET("forecast?&units=metric&appid=$KEY_API")
    suspend fun get5DayWeatherForecastByCityId(
        @Query("id") id: Int,
        @Query("lang") lang: String
    ): ResultForecastData

    @GET("weather?&units=metric&appid=$KEY_API")
    suspend fun getCurrentWeatherByLatLong(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("lang") lang: String
    ): ResultWeatherData

    @GET("forecast?&units=metric&appid=$KEY_API")
    suspend fun get5DayWeatherForecastByLatLong(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("lang") lang: String
    ): ResultForecastData

    companion object{
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        const val BASE_IMAGE_URL = "https://openweathermap.org/img/wn/"
        const val BASE_IMAGE_URL_END = "@2x.png"
        const val KEY_API = "d6a1f0402e725609a47db2496ce5cef2"
    }
}