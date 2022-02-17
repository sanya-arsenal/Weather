package com.example.weather.data

import androidx.room.*

@Entity(tableName = "WeatherForecastTable")
data class WeatherForecastTable(
        @ColumnInfo(name = "temp")
        val temp: Int,
        @ColumnInfo(name = "feels_like")
        val feels_like: Int,
        @ColumnInfo(name = "pressure")
        val pressure: Int,
        @ColumnInfo(name = "humidity")
        val humidity: Int,
        @ColumnInfo(name = "description")
        val description: String?,
        @ColumnInfo(name = "icon")
        val icon: String?,
        @ColumnInfo(name = "speed")
        val speed: Int,
        @ColumnInfo(name = "deg")
        val deg: Double,
        @ColumnInfo(name = "name")
        val name: String?,
        @ColumnInfo(name = "country")
        val country: String?,
        @ColumnInfo(name = "cityId")
        val cityId: Int,
        @ColumnInfo(name = "time")
        val time: String?,
        @ColumnInfo(name = "date")
        val date:String?
){
        @PrimaryKey(autoGenerate = true)
        var id:Int = 0
}

@Entity(tableName = "LocationsFavoritesTable")
data class LocationsFavoritesTable(
        @PrimaryKey
        @ColumnInfo
        val id: Int,
        @ColumnInfo(name = "city")
        val city: String,
        @ColumnInfo(name = "country")
        val country: String
)