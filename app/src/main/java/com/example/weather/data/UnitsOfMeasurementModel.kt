package com.example.weather.data

import android.os.Parcelable
import androidx.annotation.StringRes
import com.example.weather.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class PressureModel(@StringRes val textResId: Int): Parcelable {
    MmHg(R.string.pressure_mmHg),
    Hpa(R.string.pressure_hPa)
}

@Parcelize
enum class TemperatureModel(@StringRes val textResId: Int): Parcelable {
    Celsius(R.string.temperature_C),
    Fahrenheit(R.string.temperature_F)
}

@Parcelize
enum class WindSpeedModel(@StringRes val textResId: Int): Parcelable {
    Kmh(R.string.wind_kmh),
    Ms(R.string.wind_ms)
}



