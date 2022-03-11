package com.example.weather.network

import android.content.Context
import com.example.weather.data.ExpandableDateModel
import com.example.weather.data.LocationsFavoritesTable
import com.example.weather.data.WeatherDescription
import com.example.weather.data.WeatherForecastTable
import com.example.weather.database.WeatherDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class WeatherRepository(private val dataStore: RemoteDataStore, val context: Context) {
    private val instanceDB = WeatherDatabase.create(context)

    @ExperimentalSerializationApi
    suspend fun loadCurrentAndForecastDataByLatLong(lat:String, lon:String, lang: String): List<WeatherDescription>{
        val listWeather = mutableListOf<WeatherDescription>()
        listWeather.add(loadCurrentWeatherByLatLong(lat, lon, lang))
        listWeather.addAll(load5dayForecastByLatLong(lat, lon, lang))
        return listWeather
    }

    @ExperimentalSerializationApi
    suspend fun loadCurrentAndForecastDataByCityId(id: Int, lang: String):List<WeatherDescription>{
        val listWeather = mutableListOf<WeatherDescription>()
        listWeather.add(loadCurrentWeatherByCityId(id, lang))
        listWeather.addAll(load5dayForecastByCityId(id, lang))
        return listWeather
    }

    @ExperimentalSerializationApi
    suspend fun loadCurrentWeatherByLatLong(lat:String, lon:String, lang: String): WeatherDescription {
        return dataStore.getCurrentWeatherByLatLong(lat, lon, lang).let { weather ->
            WeatherDescription(
                temp = weather.main.temp.roundToInt(),
                feels_like = weather.main.feels_like.roundToInt(),
                pressure = weather.main.pressure,
                humidity = weather.main.humidity,
                description = weather.weather.map { image -> image.description }.toString()
                    .drop(1).dropLast(1),
                icon = weather.weather.map { image -> WeatherAPI.BASE_IMAGE_URL + image.icon +
                    WeatherAPI.BASE_IMAGE_URL_END}[0],
                speed = weather.wind.speed.roundToInt(),
                deg = weather.wind.deg,
                name = weather.name,
                country = weather.sys.country,
                cityId = weather.id,
                date = convertUnixTime(weather.dt.toString()),
                time = weather.dt.toString()
            )
        }
    }

    @ExperimentalSerializationApi
    suspend fun loadCurrentWeatherByCityId(id: Int, lang: String): WeatherDescription {
        return dataStore.getCurrentWeatherByCityId(id, lang).let { weather ->
            WeatherDescription(
                temp = weather.main.temp.roundToInt(),
                feels_like = weather.main.feels_like.roundToInt(),
                pressure = weather.main.pressure,
                humidity = weather.main.humidity,
                description = weather.weather.map { image -> image.description }.toString()
                    .drop(1).dropLast(1),
                icon = weather.weather.map { image -> WeatherAPI.BASE_IMAGE_URL + image.icon +
                    WeatherAPI.BASE_IMAGE_URL_END }[0],
                speed = weather.wind.speed.roundToInt(),
                deg = weather.wind.deg,
                name = weather.name,
                country = weather.sys.country,
                cityId = weather.id,
                date = convertUnixTime(weather.dt.toString()),
                time = weather.dt.toString()
            )
        }
    }

    @ExperimentalSerializationApi
    suspend fun load5dayForecastByLatLong(lat:String, lon:String, lang: String):List<WeatherDescription>{
        val listForecast = dataStore.get5DayWeatherForecastByLatLong(lat, lon, lang)
        val city = listForecast.city
        return listForecast.list.map { forecast->
            WeatherDescription(
                temp = forecast.main.temp.roundToInt(),
                feels_like = forecast.main.feels_like.roundToInt(),
                pressure = forecast.main.pressure,
                humidity = forecast.main.humidity,
                description = forecast.weather.map { image-> image.description }.toString().drop(1).dropLast(1),
                icon = forecast.weather.map { image-> WeatherAPI.BASE_IMAGE_URL + image.icon +
                    WeatherAPI.BASE_IMAGE_URL_END }[0],
                speed = forecast.wind.speed.roundToInt(),
                deg = forecast.wind.deg,
                name = city.name,
                country = city.country,
                cityId = city.id,
                date = forecast.dt_txt.take(10),
                time = forecast.dt_txt.takeLast(8).dropLast(3)
            )
        }
    }

    @ExperimentalSerializationApi
    suspend fun load5dayForecastByCityId(id: Int, lang: String):List<WeatherDescription>{
        val listForecast = dataStore.get5DayWeatherForecastByCityId(id, lang)
        val city = listForecast.city
        return listForecast.list.map { forecast->
            WeatherDescription(
                temp = forecast.main.temp.roundToInt(),
                feels_like = forecast.main.feels_like.roundToInt(),
                pressure = forecast.main.pressure,
                humidity = forecast.main.humidity,
                description = forecast.weather.map { image -> image.description }.toString().drop(1).dropLast(1),
                icon = forecast.weather.map { image -> WeatherAPI.BASE_IMAGE_URL + image.icon +
                    WeatherAPI.BASE_IMAGE_URL_END }[0],
                speed = forecast.wind.speed.roundToInt(),
                deg = forecast.wind.deg,
                name = city.name,
                country = city.country,
                cityId = city.id,
                date = forecast.dt_txt.take(10),
                time = forecast.dt_txt.takeLast(8).dropLast(3)
            )
        }
    }

    suspend fun saveWeather5dayForecast(list: List<WeatherDescription>){
        withContext(Dispatchers.IO){
            instanceDB.weatherDao.deleteAllByIdCity(list[0].cityId)
            instanceDB.weatherDao.insertAll(convertWeatherForecastToWeatherDB(list))
        }
    }

    suspend fun getWeatherDataFromDB(id: Int, date: String, time: String): List<WeatherDescription>{
        return withContext(Dispatchers.IO){
            val listWeather = mutableListOf<WeatherDescription>()
            listWeather.addAll(convertWeatherToWeatherScreen(
                instanceDB.weatherDao.getWeatherFromDB(id, date, time))
            )
            listWeather.addAll(convertWeatherToWeatherScreen(instanceDB.weatherDao.getWeatherForecastFromDB(id,date)))
            return@withContext listWeather
        }
    }

    suspend fun get5DayForecastFromDB(cityId: Int): List<ExpandableDateModel> {
        return withContext(Dispatchers.IO){
            convertWeatherForecastToWeatherScreen(instanceDB.weatherDao.getWeather5dayForecast(cityId))
        }
    }

    private fun convertWeatherToWeatherScreen(list: List<WeatherForecastTable>): List<WeatherDescription>{
        return list.map { weather ->
            WeatherDescription(
                weather.temp,
                weather.feels_like,
                weather.pressure,
                weather.humidity,
                weather.description,
                weather.icon,
                weather.speed,
                weather.deg,
                weather.name,
                weather.country,
                weather.cityId,
                weather.time,
                weather.date
            )
        }
    }

    private fun convertWeatherForecastToWeatherDB(list: List<WeatherDescription>): List<WeatherForecastTable>{
        return list.map { weather->
            WeatherForecastTable(
                temp = weather.temp,
                feels_like = weather.feels_like,
                pressure = weather.pressure,
                humidity = weather.humidity,
                description = weather.description,
                icon = weather.icon,
                speed = weather.speed,
                deg = weather.deg,
                name = weather.name,
                country = weather.country,
                cityId = weather.cityId,
                time = weather.time,
                date = weather.date
            )
        }
    }

    private fun convertWeatherForecastToWeatherScreen(list: List<WeatherForecastTable>):List<ExpandableDateModel>{
        val listForecast = mutableListOf<ExpandableDateModel>()
        var date: String? = "12.12.12"
        for(n in list){
            if (date != n.date){
                val dateList = list.filter { it.date == n.date }
                listForecast.add(ExpandableDateModel(ExpandableDateModel.PARENT, n.date,dateList))
                date = n.date
            }
        }
        return listForecast
    }

    suspend fun saveLocationFavorites(location: LocationsFavoritesTable): List<LocationsFavoritesTable>{
        return withContext(Dispatchers.IO){
            instanceDB.weatherDao.insertLocation(location)
            instanceDB.weatherDao.getAllLocations()
        }
    }

    suspend fun getLocationsFavorites(): List<LocationsFavoritesTable>{
        return withContext(Dispatchers.IO){
            instanceDB.weatherDao.getAllLocations()
        }
    }

    suspend fun deleteByIdAndUpdateListLocationsFavorites(id: Int): List<LocationsFavoritesTable>{
        return withContext(Dispatchers.IO){
            instanceDB.weatherDao.deleteByIdLocation(id)
            instanceDB.weatherDao.getAllLocations()
        }
    }

    private fun convertUnixTime(unix: String): String{
        val dateFormat = SimpleDateFormat("E d MMMM, yyyy", Locale.getDefault())
        val date = Date(unix.toLong()*1000)
        return dateFormat.format(date)
    }

}