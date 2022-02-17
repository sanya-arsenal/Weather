package com.example.weather.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.data.ExpandableDateModel
import com.example.weather.data.PressureModel
import com.example.weather.data.TemperatureModel
import com.example.weather.data.WindSpeedModel
import com.example.weather.databinding.ItemHolderDayBinding
import com.example.weather.databinding.ItemHolderTimeBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class ForecastDetailsAdapter(
    private var forecastDataList: MutableList<ExpandableDateModel>,
    private val temperatureUnit: TemperatureModel,
    private val pressureUnit: PressureModel,
    private val windSpeedUnit: WindSpeedModel
    ):  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            ExpandableDateModel.PARENT -> {
                WeatherForecastParentViewHolder(
                    ItemHolderDayBinding.inflate(
                        LayoutInflater.from(parent.context), parent,false
                    )
                )
            }
            else -> {
                WeatherForecastChildViewHolder(
                    ItemHolderTimeBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int = forecastDataList.size

    @SuppressLint("StringFormatMatches")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val row = forecastDataList[position]
        when(holder){
            is WeatherForecastParentViewHolder -> {
                with(holder.binding){
                    tvDay.text = holder.itemView.context.convertStringToDate(row.date)
                    if (forecastDataList[position].isExpanded){
                        ivArrowDown.visibility = View.GONE
                        ivArrowDropUp.visibility = View.VISIBLE
                    }else{
                        ivArrowDown.visibility = View.VISIBLE
                        ivArrowDropUp.visibility = View.GONE
                    }
                    ivArrowDown.setOnClickListener {
                        forecastDataList[position].isExpanded = true
                        ivArrowDown.visibility = View.GONE
                        ivArrowDropUp.visibility = View.VISIBLE
                        expandRow(position)
                    }
                    ivArrowDropUp.setOnClickListener {
                        forecastDataList[position].isExpanded = false
                        collapseRow(position)
                        ivArrowDown.visibility = View.VISIBLE
                        ivArrowDropUp.visibility = View.GONE
                    }
                }
            }

            is WeatherForecastChildViewHolder ->{
                with(holder.binding) {
                    ivIcon.let { Glide.with(holder.itemView).load(row.weather.icon).into(it) }
                    tvWeatherMain.text = row.weather.description
                    tvTime.text = row.weather.time
                    tvDegree.text = holder.itemView.context.convertUnitsOfMeasurements(row.weather.temp,temperatureUnit)
                    tvHumidity.text = holder.itemView.context.getString(R.string.humidity_info,row.weather.humidity)
                    tvPressure.text = holder.itemView.context.convertUnitsOfMeasurements(row.weather.pressure, pressureUnit)
                    tvWindSpeed.text = holder.itemView.context.convertUnitsOfMeasurements(row.weather.speed, windSpeedUnit)
                    tvWindDegree.text = holder.itemView.context.getString(convertDegreesToWindDirection(row.weather.deg.toInt()))
                }
            }
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

    override fun getItemViewType(position: Int): Int = forecastDataList[position].type

    private fun expandRow(position: Int){
        val row = forecastDataList[position]
        var nextPosition = position
        when (row.type) {
            ExpandableDateModel.PARENT -> {
                for(child in row.list){
                    forecastDataList.add(++nextPosition, ExpandableDateModel(ExpandableDateModel.CHILD, child))
                }
                notifyItemRangeInserted(position+1,row.list.size)
            }
            ExpandableDateModel.CHILD ->{
            }
        }
    }

    private fun collapseRow(position: Int){
        val row = forecastDataList[position]
        val nextPosition = position + 1
        var countRemoved = 0
        when (row.type) {
            ExpandableDateModel.PARENT -> {
                while (true) {
                    if (nextPosition == forecastDataList.size
                        || forecastDataList[nextPosition].type == ExpandableDateModel.PARENT
                    ) {
                        break
                    }
                    forecastDataList.removeAt(nextPosition)
                    countRemoved++
                }
                notifyItemRangeRemoved(nextPosition,countRemoved)
            }
        }
    }

    private fun Context.convertUnitsOfMeasurements(value: Int, unitOfMeasurement: Enum<*>): String{
        return when(unitOfMeasurement){
            TemperatureModel.Celsius -> getString(R.string.temperature_info_C, value)
            TemperatureModel.Fahrenheit -> getString(R.string.temperature_info_F, (value*1.8+32).roundToInt())
            PressureModel.Hpa -> getString(R.string.pressure_info_hPa, value)
            PressureModel.MmHg -> getString(R.string.pressure_info_mmHg,value*100/133.3.roundToInt())
            WindSpeedModel.Ms -> getString(R.string.wind_info_ms, value)
            else -> getString(R.string.wind_info_kmh, (value.toDouble()/1000*3600).roundToInt())
        }
    }

    class WeatherForecastParentViewHolder(val binding: ItemHolderDayBinding) : RecyclerView.ViewHolder(binding.root)
    class WeatherForecastChildViewHolder(val binding: ItemHolderTimeBinding) : RecyclerView.ViewHolder(binding.root)

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun Context.convertStringToDate(date: String?): String{
        val calendar = Calendar.getInstance().time
        val calendarDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val calendarDate = calendarDateFormat.format(calendar)
        return if (date != calendarDate ){
            val firstFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val secondFormat = DateTimeFormatter.ofPattern("E, d MMMM")
            secondFormat.format(LocalDate.parse(date, firstFormat))
        }else{
            getString(R.string.today)
        }
    }

}