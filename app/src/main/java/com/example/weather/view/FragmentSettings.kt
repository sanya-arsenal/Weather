package com.example.weather.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.weather.R
import com.example.weather.data.PressureModel
import com.example.weather.data.TemperatureModel
import com.example.weather.data.WindSpeedModel
import com.example.weather.databinding.FragmentSettingsBinding
import com.example.weather.weatherViewModel.WeatherSettingsViewModel
import com.example.weather.weatherViewModel.WeatherSettingsViewModelFactory

class FragmentSettings: Fragment(){

    private val viewModel: WeatherSettingsViewModel by viewModels {
        WeatherSettingsViewModelFactory(requireContext().applicationContext)
    }

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.apply {
            viewModel.setSelectedUnitOfTemperature(
                getParcelable(TEMPERATURE_UNIT_OF_MEASUREMENT)?: TemperatureModel.Celsius
            )
            viewModel.setSelectedUnitOfPressure(
                getParcelable(PRESSURE_UNIT_OF_MEASUREMENT)?: PressureModel.MmHg
            )
            viewModel.setSelectedUnitOfWindSpeed(
                getParcelable(WIND_SPEED_UNIT_OF_MEASUREMENT)?: WindSpeedModel.Ms
            )
        }

        with(binding) {
            spinnerTemperatureSettings.apply {
                adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.item_spinner,
                    R.id.tv_item_settings,
                    resources.getStringArray(R.array.temperature)
                )
                setSelection(
                    getSpinnerPosition(
                        viewModel.getSelectedUnitOfTemperature().textResId,
                        R.array.temperature
                    )
                )
            }

            spinnerPressureSettings.apply {
                adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.item_spinner,
                    R.id.tv_item_settings,
                    resources.getStringArray(R.array.pressure)
                )
                setSelection(
                    getSpinnerPosition(
                        viewModel.getSelectedUnitOfPressure().textResId,
                        R.array.pressure
                    )
                )
            }

            spinnerWindSpeedSettings.apply {
                adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.item_spinner,
                    R.id.tv_item_settings,
                    resources.getStringArray(R.array.windSpeed)
                )
                setSelection(
                    getSpinnerPosition(
                        viewModel.getSelectedUnitOfWindSpeed().textResId,
                        R.array.windSpeed
                    )
                )
            }

            buttonApplySettings.setOnClickListener {
                viewModel.setSelectedUnitOfTemperature(
                    getSpinnerModel(
                        spinnerTemperatureSettings.selectedItem.toString()) as TemperatureModel
                )
                viewModel.setSelectedUnitOfPressure(
                    getSpinnerModel(
                        spinnerPressureSettings.selectedItem.toString()) as PressureModel
                )
                viewModel.setSelectedUnitOfWindSpeed(
                    getSpinnerModel(
                        spinnerWindSpeedSettings.selectedItem.toString()) as WindSpeedModel
                )
                requireActivity().supportFragmentManager
                    .setFragmentResult(
                        REQUEST_KEY,
                        bundleOf(
                            RESULT_TEMPERATURE_KEY to viewModel.getSelectedUnitOfTemperature(),
                            RESULT_PRESSURE_KEY to viewModel.getSelectedUnitOfPressure(),
                            RESULT_WIND_SPEED_KEY to viewModel.getSelectedUnitOfWindSpeed()
                        )
                    )
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun getSpinnerModel(unitOfPressure: String): Enum<*>{
        return when(unitOfPressure){
            getString(PressureModel.MmHg.textResId)-> PressureModel.MmHg
            getString(PressureModel.Hpa.textResId)-> PressureModel.Hpa
            getString(TemperatureModel.Celsius.textResId)-> TemperatureModel.Celsius
            getString(TemperatureModel.Fahrenheit.textResId)-> TemperatureModel.Fahrenheit
            getString(WindSpeedModel.Ms.textResId)-> WindSpeedModel.Ms
            else -> WindSpeedModel.Kmh
        }
    }

    private fun getSpinnerPosition(textResId: Int, arrayId: Int): Int{
        val unitOfPressure = getString(textResId)
        val array = resources.getStringArray(arrayId)
        for (i in array.indices){
            if (array[i] == unitOfPressure) return i
        }
        return 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        const val REQUEST_KEY = "Request key"
        const val RESULT_PRESSURE_KEY = "mm"
        const val RESULT_TEMPERATURE_KEY = "hPa"
        const val RESULT_WIND_SPEED_KEY = "m/s"

        private const val PRESSURE_UNIT_OF_MEASUREMENT = "PRESSURE_UNIT_OF_MEASUREMENT"
        private const val TEMPERATURE_UNIT_OF_MEASUREMENT = "TEMPERATURE_UNIT_OF_MEASUREMENT"
        private const val WIND_SPEED_UNIT_OF_MEASUREMENT = "WIND_SPEED_UNIT_OF_MEASUREMENT"

        fun newInstance(
            temperature: TemperatureModel,
            pressure: PressureModel,
            windSpeed: WindSpeedModel
        ) = FragmentSettings().apply {
            arguments = Bundle().apply {
                putParcelable(TEMPERATURE_UNIT_OF_MEASUREMENT,temperature)
                putParcelable(PRESSURE_UNIT_OF_MEASUREMENT,pressure)
                putParcelable(WIND_SPEED_UNIT_OF_MEASUREMENT,windSpeed)
            }
        }
    }
}