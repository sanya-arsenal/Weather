package com.example.weather.weatherViewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.weather.R
import com.example.weather.data.PressureModel
import com.example.weather.data.TemperatureModel
import com.example.weather.data.WindSpeedModel
import com.example.weather.data.Check
import com.example.weather.data.FusedLocationProvider
import com.example.weather.data.GetUserLocationResult
import com.example.weather.data.WeatherDescription
import com.example.weather.network.WeatherRepository
import com.example.weather.view.FragmentWeatherDescription
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalSerializationApi
class WeatherDescriptionViewModel(
    private val check: Check,
    private val repository:WeatherRepository,
    private val locationProvider: FusedLocationProvider
    ): ViewModel(){

    private var currentTemperature = TemperatureModel.Celsius
    private var currentPressure = PressureModel.MmHg
    private var currentWindSpeed = WindSpeedModel.Ms
    private var currentLocationId = FragmentWeatherDescription.MINSK

    private val _dataWeatherState = MutableLiveData<ViewModelDataState>()
    val dataWeatherState: LiveData<ViewModelDataState> get() = _dataWeatherState

    private val _locationState = MutableLiveData<ViewModelLocationState>()
    val locationState: LiveData<ViewModelLocationState> get() = _locationState

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Coroutine exception, scope active:${viewModelScope.isActive}", throwable)

        val errorTextId = when (throwable) {
            is IOException, is HttpException -> R.string.results_internet_connection_error_text
            is SerializationException -> R.string.results_parsing_error_text
            else -> R.string.results_unexpected_error_text
        }
        _dataWeatherState.value = ViewModelDataState.Error(errorTextId)
        getDataFromDB()
    }

    fun getCurrentLocationId(): Int{ return currentLocationId }
    fun setCurrentLocationId(id: Int){ currentLocationId = id }

    fun setSelectedUnitOfTemperature(temperature: TemperatureModel){ currentTemperature = temperature }
    fun getSelectedUnitOfTemperature(): TemperatureModel { return currentTemperature }

    fun setSelectedUnitOfPressure(value: PressureModel) { currentPressure = value }
    fun getSelectedUnitOfPressure(): PressureModel { return currentPressure }

    fun setSelectedUnitOfWindSpeed(windSpeed: WindSpeedModel){ currentWindSpeed = windSpeed }
    fun getSelectedUnitOfWindSpeed(): WindSpeedModel { return currentWindSpeed }

    @ExperimentalSerializationApi
    fun loadingDataWeathers(){
        _locationState.value = ViewModelLocationState.Loading
        viewModelScope.launch(exceptionHandler) {
            when(val location = locationProvider.getUserLocation()){
                is GetUserLocationResult.Success -> {
                    loadDataByLatLong(location.latitude, location.longitude)
                }
                is GetUserLocationResult.Failed.LocationProviderIsDisabled ->{
                    _locationState.value = ViewModelLocationState.LocationProviderDisabled
                }
                is GetUserLocationResult.Failed.LocationManagerNotAvailable ->{
                    _locationState.value = ViewModelLocationState.Error(R.string.location_manager_not_available)
                }
                is GetUserLocationResult.Failed.OtherFailure ->{
                    _locationState.value = ViewModelLocationState.Error(R.string.error_getting_location)
                }
            }
        }
    }

    @ExperimentalSerializationApi
    suspend fun loadDataByLatLong(lat:String, lon:String){
        _dataWeatherState.value = ViewModelDataState.Loading
        var lang = Locale.getDefault().language
        if(lang != "ru") lang = "en"
        val list = repository.loadCurrentAndForecastDataByLatLong(lat,lon, lang)
        val newState = when(check.checkedLoadData(list)){
            Check.VerifyResult.Success ->  ViewModelDataState.Success(list.subList(0,10))
            is Check.VerifyResult.Error -> ViewModelDataState.Error(R.string.loading_error)
        }
        _dataWeatherState.value = newState
        currentLocationId = list[0].cityId
        repository.saveWeather5dayForecast(list.subList(1,list.size))
    }

    @ExperimentalSerializationApi
    fun loadDataByCity(id: Int){
        var lang = Locale.getDefault().language
        if (lang != "ru") lang = "en"
        viewModelScope.launch(exceptionHandler) {
            _dataWeatherState.value = ViewModelDataState.Loading
            val list = repository.loadCurrentAndForecastDataByCityId(id, lang)
            val newState = when(check.checkedLoadData(list)){
                is Check.VerifyResult.Success -> ViewModelDataState.Success(list.subList(0,10))
                is Check.VerifyResult.Error -> ViewModelDataState.Error(R.string.loading_error)
            }
            _dataWeatherState.value = newState
            repository.saveWeather5dayForecast(list.subList(1,list.size))
        }
    }

    @ExperimentalSerializationApi
    fun getDataFromDB(){
        viewModelScope.launch(exceptionHandler) {
            _dataWeatherState.value = ViewModelDataState.Loading
            val list = repository.getWeatherDataFromDB(currentLocationId,getCurrentDate(),getCurrentTime())
            val newState = when(check.checkedLoadData(list)){
                is Check.VerifyResult.Success -> ViewModelDataState.Success(list.subList(0,10))
                is Check.VerifyResult.Error -> ViewModelDataState.Error(R.string.loading_error)
            }
            _dataWeatherState.value = newState
        }
    }

    private fun getCurrentDate(): String{
        val date = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun getCurrentTime(): String{
        val date = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(date)
    }

    sealed class ViewModelDataState{
        object Loading : ViewModelDataState()
        data class Success(val list: List<WeatherDescription>) : ViewModelDataState()
        data class Error(val errorId: Int) : ViewModelDataState()
    }

    sealed class ViewModelLocationState{
        object Loading: ViewModelLocationState()
        object LocationProviderDisabled: ViewModelLocationState()
        data class Error(val errorId: Int): ViewModelLocationState()
    }

}