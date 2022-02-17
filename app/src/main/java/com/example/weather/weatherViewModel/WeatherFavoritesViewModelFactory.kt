package com.example.weather.weatherViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.network.RemoteDataStore
import com.example.weather.network.WeatherRepository

class WeatherFavoritesViewModelFactory(private val applicationContext: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when(modelClass){
        WeatherFavoritesViewModel::class.java -> WeatherFavoritesViewModel(
        WeatherRepository(dataStore = RemoteDataStore(),context = applicationContext))
        else -> throw IllegalArgumentException("$modelClass is not registered ViewModel")
    } as T
}