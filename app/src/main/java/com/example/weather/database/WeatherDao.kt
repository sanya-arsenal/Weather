package com.example.weather.database

import androidx.room.*
import com.example.weather.data.LocationsFavoritesTable
import com.example.weather.data.WeatherForecastTable

@Dao
interface WeatherDao {
    @Query("SELECT * FROM WeatherForecastTable WHERE cityId = :id")
    fun getWeather5dayForecast(id: Int):List<WeatherForecastTable>

    @Query("SELECT * FROM WeatherForecastTable WHERE cityId = :id AND date =:date AND time >= :time")
    fun getWeatherFromDB(id: Int, date: String, time: String): List<WeatherForecastTable>

    @Query("SELECT * FROM WeatherForecastTable WHERE cityId = :id AND date >:date")
    fun getWeatherForecastFromDB(id: Int, date: String): List<WeatherForecastTable>

    @Insert
    fun insertAll(list: List<WeatherForecastTable>)

    @Query("DELETE FROM WeatherForecastTable WHERE cityId = :id" )
    fun deleteAllByIdCity(id: Int)

    @Query("SELECT * FROM LocationsFavoritesTable ORDER BY id ASC")
    fun getAllLocations(): List<LocationsFavoritesTable>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: LocationsFavoritesTable)

    @Query("DELETE FROM LocationsFavoritesTable WHERE id = :id ")
    fun deleteByIdLocation(id: Int)
}