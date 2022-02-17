package com.example.weather.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weather.data.LocationsFavoritesTable
import com.example.weather.data.WeatherForecastTable

@Database(entities = [WeatherForecastTable::class, LocationsFavoritesTable::class],version = 4,exportSchema = false)
abstract class WeatherDatabase: RoomDatabase() {
    abstract val weatherDao: WeatherDao

    companion object{
        fun create(context: Context) = Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weatherDB"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}