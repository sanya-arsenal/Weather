package com.example.weather.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherDescription(
    val temp: Int,
    val feels_like: Int,
    val pressure: Int,
    val humidity: Int,
    val description: String?,
    val icon: String?,
    val speed: Int,
    val deg: Double,
    val name: String?,
    val country: String?,
    val cityId: Int,
    val time: String?,
    val date: String?
): Parcelable