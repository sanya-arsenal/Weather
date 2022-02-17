package com.example.weather.weatherViewModel

import androidx.lifecycle.ViewModel
import com.example.weather.data.PressureModel
import com.example.weather.data.TemperatureModel
import com.example.weather.data.WindSpeedModel

class WeatherSettingsViewModel: ViewModel() {

    private var currentTemperature = TemperatureModel.Celsius
    private var currentPressure = PressureModel.MmHg
    private var currentWindSpeed = WindSpeedModel.Ms

    fun setSelectedUnitOfTemperature(temperature: TemperatureModel){ currentTemperature = temperature }
    fun getSelectedUnitOfTemperature(): TemperatureModel { return currentTemperature }

    fun setSelectedUnitOfPressure(pressure: PressureModel){ currentPressure = pressure }
    fun getSelectedUnitOfPressure(): PressureModel { return currentPressure }

    fun setSelectedUnitOfWindSpeed(windSpeed: WindSpeedModel){ currentWindSpeed = windSpeed }
    fun getSelectedUnitOfWindSpeed(): WindSpeedModel { return currentWindSpeed }
}

