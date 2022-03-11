package com.example.weather.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResultForecastData (
        @SerialName("list")
        val list: List<ListResults>,
        @SerialName("city")
        val city: City,
)

@Serializable
data class ResultWeatherData (
        @SerialName("weather")
        val weather: List<Weather>,
        @SerialName("main")
        val main: Main,
        @SerialName("wind")
        val wind: Wind,
        @SerialName("dt")
        val dt: Long,
        @SerialName("sys")
        val sys: Sys,
        @SerialName("id")
        val id: Int,
        @SerialName("name")
        val name: String
)

@Serializable
data class ListResults(
        @SerialName("main")
        val main: Main,
        @SerialName("weather")
        val weather: List<Weather>,
        @SerialName("wind")
        val wind: Wind,
        @SerialName("dt_txt")
        val dt_txt: String
)

@Serializable
data class Main(
        @SerialName("temp")
        val temp: Double,
        @SerialName("feels_like")
        val feels_like: Double,
        @SerialName("pressure")
        val pressure: Int,
        @SerialName("humidity")
        val humidity: Int
)

@Serializable
data class Weather(
        @SerialName("description")
        val description: String,
        @SerialName("icon")
        val icon: String
)

@Serializable
data class Wind(
        @SerialName("speed")
        val speed: Double,
        @SerialName("deg")
        val deg: Double
)

@Serializable
data class City(
        @SerialName("id")
        val id:Int,
        @SerialName("name")
        val name: String,
        @SerialName("country")
        val country: String
)

@Serializable
data class Sys(
        @SerialName("country")
        val country: String
)