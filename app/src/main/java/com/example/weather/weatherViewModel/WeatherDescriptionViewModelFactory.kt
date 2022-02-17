package com.example.weather.weatherViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.data.Check
import com.example.weather.data.FusedLocationProvider
import com.example.weather.network.RemoteDataStore
import com.example.weather.network.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import java.lang.IllegalArgumentException

@ExperimentalSerializationApi
class WeatherDescriptionViewModelFactory(private val applicationContext: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when(modelClass) {
        WeatherDescriptionViewModel::class.java -> WeatherDescriptionViewModel(
            Check(dispatcher = Dispatchers.Default),
            WeatherRepository(dataStore = RemoteDataStore(),context = applicationContext),
            FusedLocationProvider(context = applicationContext)
        )
        else -> throw IllegalArgumentException("$modelClass is not registered ViewModel")
    } as T
}