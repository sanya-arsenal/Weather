package com.example.weather.network

import com.example.weather.data.*
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

class RemoteDataStore {

    private val json = Json { ignoreUnknownKeys = true }

    private class WeatherHeaderInterceptors : Interceptor{
        override fun intercept(chain: Interceptor.Chain): Response {
            val originRequest = chain.request()
            val originHttpUrl = originRequest.url

            val newRequest = originRequest.newBuilder()
                    .url(originHttpUrl)
                    .addHeader(API_KEY_HEADER, WeatherAPI.KEY_API)
                    .build()

            return chain.proceed(newRequest)
        }
    }

    private val client = OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(WeatherHeaderInterceptors())
            .build()

    @ExperimentalSerializationApi
    private val retrofit1 = Retrofit.Builder()
        .client(client)
        .baseUrl(WeatherAPI.BASE_URL)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @ExperimentalSerializationApi
    val weatherAPI: WeatherAPI = retrofit1.create(WeatherAPI::class.java)

    @ExperimentalSerializationApi
    suspend fun getCurrentWeatherByLatLong(lat:String, lon:String, lang: String): ResultWeather {
        return weatherAPI.getCurrentWeatherByLatLong(lat,lon,lang)
    }

    @ExperimentalSerializationApi
    suspend fun get5DayWeatherForecastByLatLong(lat:String, lon:String, lang: String): ResultForecastData{
        return weatherAPI.get5DayWeatherForecastByLatLong(lat,lon, lang)
    }

    @ExperimentalSerializationApi
    suspend fun getCurrentWeatherByCityId(id: Int, lang: String): ResultWeather{
        return weatherAPI.getCurrentWeatherByCityId(id, lang)
    }

    @ExperimentalSerializationApi
    suspend fun get5DayWeatherForecastByCityId(id: Int, lang: String): ResultForecastData{
        return weatherAPI.get5DayWeatherForecastByCityId(id, lang)
    }

    companion object {
        const val API_KEY_HEADER = "api-key"
    }
}