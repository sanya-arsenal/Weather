package com.example.weather.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.data.LocationsFavoritesTable
import com.example.weather.databinding.*

class FavoritesAdapter(
    private val list: List<LocationsFavoritesTable>,
    private val currentLocation: LocationsFavoritesTable,
    private val locationClick: (LocationsFavoritesTable) -> Unit,
    private val deleteClick: (Int) -> Unit,
    private val addLocation: (LocationsFavoritesTable) -> Unit
    ): RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when(position) {
            0 -> VIEW_TYPE_CURRENT_LOCATION
            1 -> VIEW_TYPE_CURRENT_LOCATION_NAME
            2 -> VIEW_TYPE_FAVORITES_LOCATIONS
            else -> VIEW_TYPE_FAVORITES_LOCATIONS_NAME
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        return when(viewType){
            VIEW_TYPE_CURRENT_LOCATION -> CurrentLocationHolder(
                ItemCurrentLocationBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            VIEW_TYPE_CURRENT_LOCATION_NAME -> CurrentLocationNameHolder(
                ItemCurrentLocationNameBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            VIEW_TYPE_FAVORITES_LOCATIONS -> FavoritesLocationsHolder(
                ItemFavoritesLocationsBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> FavoritesLocationsNameHolder(
                ItemFavoritesNameBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false

                )
            )
        }
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        when(holder){
            is CurrentLocationHolder ->{}
            is CurrentLocationNameHolder ->{
                with(holder.binding){
                    tvNameLocation.text = holder.itemView.context.getString(R.string.city_name_country,
                        currentLocation.city,
                        currentLocation.country
                    )
                    ivBaseFavorites.setOnClickListener {
                        addLocation(currentLocation)
                        ivBaseFavorites.isVisible = false
                        ivFavorites.isVisible = true
                    }
                    ivFavorites.setOnClickListener {
                        deleteClick(currentLocation.id)
                        ivBaseFavorites.isVisible = true
                        ivFavorites.isVisible = false
                    }
                }
            }
            is FavoritesLocationsHolder ->{}
            is FavoritesLocationsNameHolder ->{
                with(holder.binding){
                    tvLocationTitle.text = holder.itemView.context.getString(
                        R.string.city_name_country,
                        list[position-3].city,
                        list[position-3].country
                    )
                    tvLocationTitle.setOnClickListener{
                        locationClick(list[position-3])
                    }
                    ivLocationDelete.setOnClickListener {
                        deleteClick(list[position-3].id)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size+3
    }

    abstract class FavoritesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    inner class CurrentLocationHolder(binding: ItemCurrentLocationBinding): FavoritesViewHolder(binding.root)
    inner class CurrentLocationNameHolder(val binding: ItemCurrentLocationNameBinding): FavoritesViewHolder(binding.root)
    inner class FavoritesLocationsHolder(binding: ItemFavoritesLocationsBinding): FavoritesViewHolder(binding.root)
    inner class FavoritesLocationsNameHolder(val binding: ItemFavoritesNameBinding): FavoritesViewHolder(binding.root)

    companion object{
        private const val VIEW_TYPE_CURRENT_LOCATION = 0
        private const val VIEW_TYPE_CURRENT_LOCATION_NAME = 1
        private const val VIEW_TYPE_FAVORITES_LOCATIONS = 2
        private const val VIEW_TYPE_FAVORITES_LOCATIONS_NAME = 3
    }

}