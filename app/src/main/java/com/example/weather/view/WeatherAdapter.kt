package com.example.weather.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.data.TemperatureModel
import com.example.weather.data.WeatherDescription
import com.example.weather.databinding.ItemForecastBinding

class WeatherAdapter(
    private val list: List<WeatherDescription>,
    private val temperatureUnit: TemperatureModel
    ): RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        return WeatherViewHolder(
            ItemForecastBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        with(holder.binding){
            tvTimes.text = list[position].time
            ivWeatherss.let { Glide.with(holder.itemView).load(list[position].icon).into(it) }
            tvTemperature.text = convertUnitsOfMeasurements(holder.itemView.context,list[position].temp, temperatureUnit)
            tvDescriptionMain.text = list[position].description
        }
    }

    override fun getItemCount(): Int = list.size

    inner class WeatherViewHolder(val binding: ItemForecastBinding): RecyclerView.ViewHolder(binding.root)

    private fun convertUnitsOfMeasurements(
        context: Context,
        value: Int,
        temperatureUnit: TemperatureModel
    ): String{
        return when(temperatureUnit){
            TemperatureModel.Fahrenheit -> context.getString(R.string.temperature_info_F, value)
            TemperatureModel.Celsius -> context.getString(R.string.temperature_info_C, value)
        }
    }
}