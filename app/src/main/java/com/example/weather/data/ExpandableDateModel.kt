package com.example.weather.data

class ExpandableDateModel{
    val type: Int
    var date: String? = null
    lateinit var list: List<WeatherForecastTable>
    lateinit var weather: WeatherForecastTable
    var isExpanded: Boolean
    constructor(type: Int,
                date: String?,
                list: List<WeatherForecastTable>,
                isExpanded: Boolean = false,
    ){
        this.type = type
        this.date = date
        this.list = list
        this.isExpanded = isExpanded
    }

    constructor(type: Int,
                weather: WeatherForecastTable,
                isExpanded: Boolean = false,
    ){
        this.type = type
        this.weather = weather
        this.isExpanded = isExpanded
    }

    companion object{
        const val PARENT = 1
        const val CHILD = 0
    }
}