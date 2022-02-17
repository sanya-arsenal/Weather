package com.example.weather.view

import android.Manifest
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.data.PressureModel
import com.example.weather.data.TemperatureModel
import com.example.weather.data.WindSpeedModel
import com.example.weather.data.WeatherDescription
import com.example.weather.databinding.FragmentWeatherDescriptionBinding
import com.example.weather.weatherViewModel.WeatherDescriptionViewModel
import com.example.weather.weatherViewModel.WeatherDescriptionViewModelFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.math.roundToInt

@ExperimentalSerializationApi
class FragmentWeatherDescription: Fragment() {
    private val viewModel: WeatherDescriptionViewModel by viewModels {
        WeatherDescriptionViewModelFactory(applicationContext = requireContext().applicationContext)
    }

    private var requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted:Boolean->
            if (isGranted) {
                onLocationPermissionGranted()
            }
            else{
                onLocationPermissionNotGranted()
            }
        }

    private var isRationaleShown = false

    private lateinit var listDataOfWeather: List<WeatherDescription>

    private var _binding: FragmentWeatherDescriptionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restorePreferencesData()

        requireActivity().supportFragmentManager
            .setFragmentResultListener(FragmentSettings.REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
                viewModel.setSelectedUnitOfTemperature(
                    bundle.getParcelable(FragmentSettings.RESULT_TEMPERATURE_KEY)?: TemperatureModel.Celsius
                )
                viewModel.setSelectedUnitOfPressure(
                    bundle.getParcelable(FragmentSettings.RESULT_PRESSURE_KEY)?: PressureModel.MmHg
                )
                viewModel.setSelectedUnitOfWindSpeed(
                    bundle.getParcelable(FragmentSettings.RESULT_WIND_SPEED_KEY)?: WindSpeedModel.Ms
                )
                showWeather(listDataOfWeather)
            }

        requireActivity().supportFragmentManager
            .setFragmentResultListener(FragmentFavorites.REQUEST_KEY, viewLifecycleOwner){ _,bundle->
                viewModel.loadDataByCity(bundle.getInt(FragmentFavorites.RESULT_CITY))
            }

        with(binding){
            swSwipeRefreshLayout.setOnRefreshListener {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED ){
                    viewModel.loadDataByCity(viewModel.getCurrentLocationId())
                    swSwipeRefreshLayout.isRefreshing = false
                }else{
                    onGetPermissionLocation()
                    swSwipeRefreshLayout.isRefreshing = false
                }
            }
        }

        requestLocationPermission()
        viewModel.dataWeatherState.observe(this.viewLifecycleOwner, this::setState)
        viewModel.locationState.observe(this.viewLifecycleOwner, this::events)
    }

    private fun events(event: WeatherDescriptionViewModel.ViewModelLocationState) = when(event) {
        is WeatherDescriptionViewModel.ViewModelLocationState.Error ->{
            Toast.makeText(context, getString(event.errorId), Toast.LENGTH_SHORT).show()
            viewModel.loadDataByCity(viewModel.getCurrentLocationId())
        }
        is WeatherDescriptionViewModel.ViewModelLocationState.LocationProviderDisabled ->{
            showLocationProviderSettingsDialog()
        }
        is WeatherDescriptionViewModel.ViewModelLocationState.Loading ->{
            showProgress()
        }
    }

    private fun setState(state: WeatherDescriptionViewModel.ViewModelDataState) = when(state){
        is WeatherDescriptionViewModel.ViewModelDataState.Loading -> showProgress()
        is WeatherDescriptionViewModel.ViewModelDataState.Success -> showWeather(state.list)
        is WeatherDescriptionViewModel.ViewModelDataState.Error -> showError(state.errorId)
    }

    private fun showWeather(list: List<WeatherDescription>) {
        listDataOfWeather = list
        with(binding){
            prProgressBarState.visibility = View.INVISIBLE
            clWeather.visibility = View.VISIBLE
            ivSettings.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.frame_content, FragmentSettings.newInstance(
                        viewModel.getSelectedUnitOfTemperature(),
                        viewModel.getSelectedUnitOfPressure(),
                        viewModel.getSelectedUnitOfWindSpeed()
                    ))
                    .addToBackStack("FragmentSettings")
                    .commit()
            }
            ivFavoritesLocations.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.frame_content,FragmentFavorites.newInstance(
                        list[0].cityId, list[0].name, list[0].country)
                    )
                    .addToBackStack("FragmentFavorites")
                    .commit()
            }
            ivWeather.let {  Glide.with(requireContext()).load(list[0].icon).into(it) }
            tvDegrees.text = convertUnitsOfMeasurements(list[0].temp, viewModel.getSelectedUnitOfTemperature())
            tvFeelsLike.text = getString(
                R.string.temperature_feels_like,
                convertUnitsOfMeasurements(list[0].feels_like, viewModel.getSelectedUnitOfTemperature())
            )
            tvWeatherDescription.text = list[0].description?.uppercase()
            tvHumidity.text = getString(R.string.humidity_info, list[0].humidity )
            tvPressure.text = convertUnitsOfMeasurements(list[0].pressure, viewModel.getSelectedUnitOfPressure())
            tvWindSpeed.text = convertUnitsOfMeasurements(list[0].speed, viewModel.getSelectedUnitOfWindSpeed())
            tvWindDirection.text = getString(convertDegreesToWindDirection(list[0].deg.toInt()))
            tvDate.text = list[0].date
            tvCityWeather.text = getString(R.string.city_name_country, list[0].name, list[0].country)
            tvDetails.setOnClickListener{ doOnClick(list[0].cityId) }
            rvForecastDetails.adapter = WeatherAdapter(
                list.subList(1,list.size),
                viewModel.getSelectedUnitOfTemperature()
            )
        }
    }

    private fun doOnClick(cityId: Int){
        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.frame_content, FragmentForecastDetails.newInstance(
                cityId,
                viewModel.getSelectedUnitOfTemperature(),
                viewModel.getSelectedUnitOfPressure(),
                viewModel.getSelectedUnitOfWindSpeed())
            )
            .addToBackStack("FragmentForecastDetails")
            .commit()
    }

    private fun showProgress() {
        with(binding){
            clWeather.visibility = View.INVISIBLE
            prProgressBarState.visibility = View.VISIBLE
        }
    }

    private fun showError(errorId: Int) {
        with(binding) {
            clWeather.visibility = View.INVISIBLE
            prProgressBarState.visibility = View.INVISIBLE
            Toast.makeText(context,getString(errorId),Toast.LENGTH_SHORT).show()
        }
    }

    @ExperimentalSerializationApi
    private fun onGetPermissionLocation(){
        when{
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED -> onLocationPermissionGranted()
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)->
                showLocationPermissionExplanationDialog()
            isRationaleShown -> showLocationPermissionDeniedDialog()
        }
    }

    private fun requestLocationPermission(){
        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun onLocationPermissionGranted() {
        viewModel.loadingDataWeathers()
    }

    private fun onLocationPermissionNotGranted() {
        onGetPermissionLocation()
    }

    private fun showLocationPermissionExplanationDialog() {
        context?.let {
            AlertDialog.Builder(it)
                .setMessage(R.string.explanation)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    isRationaleShown = true
                    requestLocationPermission()
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                    viewModel.loadDataByCity(viewModel.getCurrentLocationId())
                }
                .show()
        }
    }

    private fun showLocationPermissionDeniedDialog() {
        context?.let {
            AlertDialog.Builder(it)
                .setMessage(R.string.permission_denied)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + it.packageName)
                        )
                    )
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                    viewModel.loadDataByCity(viewModel.getCurrentLocationId())
                }
                .show()
        }
    }

    private fun showLocationProviderSettingsDialog() {
        context?.let {
            AlertDialog.Builder(it)
                .setMessage(R.string.location_provider_disable)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                    viewModel.loadDataByCity(viewModel.getCurrentLocationId())
                }
                .show()
        }
    }

    private fun restorePreferencesData(){
        isRationaleShown = activity?.getPreferences(MODE_PRIVATE)?.getBoolean(RATIONALE_KEY, false)?:false
        viewModel.setCurrentLocationId(
            activity?.getPreferences(MODE_PRIVATE)?.getInt(LOCATION_ID, MINSK )!!
        )
        viewModel.setSelectedUnitOfTemperature(
            convertStringToUnitsModel(
                activity?.getPreferences(MODE_PRIVATE)?.getString(
                    TEMPERATURE_KEY, TemperatureModel.Celsius.toString()) ?: TemperatureModel.Celsius.toString()
            ) as TemperatureModel
        )
        viewModel.setSelectedUnitOfPressure(
            convertStringToUnitsModel(
                activity?.getPreferences(MODE_PRIVATE)?.getString(
                    PRESSURE_KEY, PressureModel.MmHg.toString()) ?: PressureModel.MmHg.toString()
            ) as PressureModel
        )
        viewModel.setSelectedUnitOfWindSpeed(
            convertStringToUnitsModel(
                activity?.getPreferences(MODE_PRIVATE)?.getString(
                    WIND_SPEED_KEY, WindSpeedModel.Ms.toString()) ?: WindSpeedModel.Ms.toString()
            ) as WindSpeedModel
        )
    }

    private fun savePreferencesData(){
        activity?.getPreferences(MODE_PRIVATE)?.edit()?.putBoolean(RATIONALE_KEY, isRationaleShown)
            ?.putInt(LOCATION_ID, viewModel.getCurrentLocationId())
            ?.putString(TEMPERATURE_KEY, viewModel.getSelectedUnitOfTemperature().toString())
            ?.putString(PRESSURE_KEY, viewModel.getSelectedUnitOfPressure().toString())
            ?.putString(WIND_SPEED_KEY, viewModel.getSelectedUnitOfWindSpeed().toString())
            ?.apply()
    }

    private fun convertStringToUnitsModel(model: String): Enum<*>{
        return when(model){
            TemperatureModel.Celsius.toString() -> TemperatureModel.Celsius
            TemperatureModel.Fahrenheit.toString() -> TemperatureModel.Fahrenheit
            PressureModel.Hpa.toString() -> PressureModel.Hpa
            PressureModel.MmHg.toString() -> PressureModel.MmHg
            WindSpeedModel.Ms.toString() -> WindSpeedModel.Ms
            else -> WindSpeedModel.Kmh
        }
    }

    private fun convertUnitsOfMeasurements(value: Int, model: Enum<*>): String{
        return when(model){
            TemperatureModel.Celsius -> getString(R.string.temperature_info_C, value)
            TemperatureModel.Fahrenheit -> getString(R.string.temperature_info_F, (value*1.8+32).roundToInt())
            PressureModel.Hpa -> getString(R.string.pressure_info_hPa, value)
            PressureModel.MmHg -> getString(R.string.pressure_info_mmHg,value*100/133.3.roundToInt())
            WindSpeedModel.Ms -> getString(R.string.wind_info_ms, value)
            else -> getString(R.string.wind_info_kmh, (value.toDouble()/1000*3600).roundToInt())
        }
    }

    private fun convertDegreesToWindDirection(degrees: Int): Int{
        return when(degrees){
            in 0..90 -> R.string.windDirection_Type_SV
            in 91..180 -> R.string.windDirection_Type_YUV
            in 181..270 -> R.string.windDirection_Type_YUZ
            else -> {R.string.windDirection_Type_SZ}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        savePreferencesData()
        requestPermission.unregister()
        _binding = null
    }

    companion object{
        private const val RATIONALE_KEY = "RATIONALE_KEY"
        private const val LOCATION_ID = "LOCATION_ID"
        private const val TEMPERATURE_KEY = "TEMPERATURE_KEY"
        private const val PRESSURE_KEY = "PRESSURE_KEY"
        private const val WIND_SPEED_KEY = "WIND_SPEED_KEY"
        private const val MINSK = 625144

    }

}