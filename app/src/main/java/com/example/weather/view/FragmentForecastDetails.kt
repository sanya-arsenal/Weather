package com.example.weather.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.weather.R
import com.example.weather.data.ExpandableDateModel
import com.example.weather.data.PressureModel
import com.example.weather.data.TemperatureModel
import com.example.weather.data.WindSpeedModel
import com.example.weather.databinding.FragmentWeatherForecastBinding
import com.example.weather.weatherViewModel.WeatherForecastViewModel
import com.example.weather.weatherViewModel.WeatherForecastViewModelFactory
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class FragmentForecastDetails: Fragment() {

    private val viewModel: WeatherForecastViewModel by viewModels {
        WeatherForecastViewModelFactory(applicationContext = requireContext().applicationContext)
    }

    private var _binding: FragmentWeatherForecastBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherForecastBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getInt(CITY_NAME_ID)?.let { viewModel.loadingDataWeatherForecast(it) }
        viewModel.dataWeatherForecast.observe(this.viewLifecycleOwner, this::setState)
    }

    private fun setState(state: WeatherForecastViewModel.ViewModelDataStateForecast) = when(state){
        is WeatherForecastViewModel.ViewModelDataStateForecast.Loading -> showProgress()
        is WeatherForecastViewModel.ViewModelDataStateForecast.Success -> showWeather(state.list)
        is WeatherForecastViewModel.ViewModelDataStateForecast.Error ->showError(state.error)
    }

    private fun showWeather(listDate: List<ExpandableDateModel>){
        with(binding) {
            tvCityWeather.text = getString(R.string.city_name_country, listDate[0].list[0].name, listDate[0].list[0].country)
            rvForecast.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            rvForecast.adapter = ForecastDetailsAdapter(
                listDate as MutableList<ExpandableDateModel>,
                arguments?.getParcelable(TEMPERATURE_UNIT_OF_MEASUREMENT)?: TemperatureModel.Celsius,
                arguments?.getParcelable(PRESSURE_UNIT_OF_MEASUREMENT)?: PressureModel.MmHg,
                arguments?.getParcelable(WIND_SPEED_UNIT_OF_MEASUREMENT)?: WindSpeedModel.Ms
            )
            prProgressBarForecast.isVisible = false
            rvForecast.isVisible = true
        }
    }

    private fun showProgress(){
        with(binding) {
            rvForecast.isVisible = false
            prProgressBarForecast.isVisible = true
        }
    }

    private fun showError(error: String) {
        with(binding) {
            rvForecast.isVisible = false
            prProgressBarForecast.isVisible = false
            Toast.makeText(context,error, Toast.LENGTH_SHORT).show()
        }
    }

    companion object{
        private const val CITY_NAME_ID = "CITY_NAME_ID"
        private const val PRESSURE_UNIT_OF_MEASUREMENT = "PRESSURE_UNIT_OF_MEASUREMENT"
        private const val TEMPERATURE_UNIT_OF_MEASUREMENT = "TEMPERATURE_UNIT_OF_MEASUREMENT"
        private const val WIND_SPEED_UNIT_OF_MEASUREMENT = "WIND_SPEED_UNIT_OF_MEASUREMENT"

        fun newInstance(
            cityId: Int,
            temperature: TemperatureModel,
            pressure: PressureModel,
            windSpeed: WindSpeedModel
        ) = FragmentForecastDetails().apply {
             arguments = Bundle().apply {
                 putInt(CITY_NAME_ID, cityId)
                 putParcelable(TEMPERATURE_UNIT_OF_MEASUREMENT, temperature)
                 putParcelable(PRESSURE_UNIT_OF_MEASUREMENT, pressure)
                 putParcelable(WIND_SPEED_UNIT_OF_MEASUREMENT, windSpeed)
            }
        }
    }
}