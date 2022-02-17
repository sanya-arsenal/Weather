package com.example.weather.weatherViewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.data.ExpandableDateModel
import com.example.weather.data.Check
import com.example.weather.network.WeatherRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

class WeatherForecastViewModel(private val check: Check, private val repository: WeatherRepository): ViewModel() {

    private val mutableDataWeatherForecast = MutableLiveData<ViewModelDataStateForecast>()
    val dataWeatherForecast: LiveData<ViewModelDataStateForecast> get() = mutableDataWeatherForecast

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Coroutine exception, scope active:${viewModelScope.isActive}", throwable)
    }

    @ExperimentalSerializationApi
    fun loadingDataWeatherForecast(cityId: Int){
        viewModelScope.launch(exceptionHandler) {
            mutableDataWeatherForecast.value = ViewModelDataStateForecast.Loading
            val list = repository.get5DayForecastFromDB(cityId)
            val newState = when(check.checkedDataForDB(list)){
                is Check.VerifyResult.Success -> ViewModelDataStateForecast.Success(list)
                is Check.VerifyResult.Error -> ViewModelDataStateForecast.Error("Error DB")
            }
            mutableDataWeatherForecast.value = newState
        }
    }

    sealed class ViewModelDataStateForecast{
        object Loading : ViewModelDataStateForecast()
        data class Success(val list: List<ExpandableDateModel>) : ViewModelDataStateForecast()
        data class Error(val error: String) : ViewModelDataStateForecast()
    }
}