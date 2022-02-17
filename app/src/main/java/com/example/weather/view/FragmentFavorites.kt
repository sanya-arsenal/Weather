package com.example.weather.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.weather.data.LocationsFavoritesTable
import com.example.weather.databinding.FragmentFavoritesBinding
import com.example.weather.weatherViewModel.WeatherFavoritesViewModel
import com.example.weather.weatherViewModel.WeatherFavoritesViewModelFactory

class FragmentFavorites: Fragment() {

    private val viewModel: WeatherFavoritesViewModel by viewModels {
        WeatherFavoritesViewModelFactory(requireContext())
    }

    private var _binding: FragmentFavoritesBinding? = null
    private val binding  get() = _binding!!

    private lateinit var currentLocation: LocationsFavoritesTable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.apply {
            currentLocation = LocationsFavoritesTable(
                getInt(CITY_ID),
                getString(CITY_NAME).toString(),
                getString(CITY_COUNTRY).toString()
            )
        }
        viewModel.getLocations()
        viewModel.locationsList.observe(this.viewLifecycleOwner, this::updateAdapter)
    }

    private fun doClick(city: LocationsFavoritesTable) {
        requireActivity().supportFragmentManager
            .setFragmentResult(
                REQUEST_KEY,
                bundleOf(RESULT_CITY to city.id)
            )
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun updateAdapter(locations: List<LocationsFavoritesTable>) {
        with(binding) {
                rvFavorites.adapter = FavoritesAdapter(
                locations,
                currentLocation,
                this@FragmentFavorites::doClick,
                viewModel::deleteLocation,
                viewModel::insertLocation
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        const val REQUEST_KEY = "FAVORITES"
        const val RESULT_CITY = "CITY"
        private const val CITY_ID = "CITY_ID"
        private const val CITY_NAME = "CITY_NAME"
        private const val CITY_COUNTRY = "CITY_COUNTRY"
        fun newInstance(id: Int, city: String?, country: String?) = FragmentFavorites().apply {
            arguments = Bundle().apply {
                putInt(CITY_ID, id)
                putString(CITY_NAME, city)
                putString(CITY_COUNTRY, country)
            }
        }
    }
}