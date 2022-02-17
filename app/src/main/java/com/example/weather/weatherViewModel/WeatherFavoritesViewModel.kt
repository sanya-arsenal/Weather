package com.example.weather.weatherViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.data.LocationsFavoritesTable
import com.example.weather.network.WeatherRepository
import kotlinx.coroutines.launch

class WeatherFavoritesViewModel(private val repository: WeatherRepository): ViewModel() {

    private val _mutableLocationsList = MutableLiveData<List<LocationsFavoritesTable>>(emptyList())
    val locationsList: LiveData<List<LocationsFavoritesTable>> get() = _mutableLocationsList

    fun deleteLocation(id: Int) {
        viewModelScope.launch {
            _mutableLocationsList.value = repository.deleteByIdAndUpdateListLocationsFavorites(id)
        }
    }

     fun insertLocation(location: LocationsFavoritesTable) {
         viewModelScope.launch {
             _mutableLocationsList.value = repository.saveLocationFavorites(location)
         }
     }

    fun getLocations() {
        viewModelScope.launch {
            _mutableLocationsList.value = repository.getLocationsFavorites()
        }
    }

}