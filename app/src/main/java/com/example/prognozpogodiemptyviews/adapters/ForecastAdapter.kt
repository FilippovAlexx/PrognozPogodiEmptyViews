package com.example.prognozpogodiemptyviews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prognozpogodiemptyviews.R
import com.example.prognozpogodiemptyviews.models.Forecast

class ForecastsAdapter : RecyclerView.Adapter<ForecastsAdapter.ForecastViewHolder>() {

    private var forecasts: List<Forecast> = emptyList()

    inner class ForecastViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvTemperature: TextView = itemView.findViewById(R.id.tvTemperature)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvIcon: TextView = itemView.findViewById(R.id.tvIcon)

        fun bind(forecast: Forecast) {
            tvDate.text = forecast.date
            tvTemperature.text = "${forecast.temperature}°C"
            tvDescription.text = forecast.description
            tvIcon.text = getIconEmoji(forecast.icon)
        }

        private fun getIconEmoji(iconName: String): String {
            return when (iconName) {
                "sunny", "clear" -> "☀️"
                "partly_cloudy" -> "⛅"
                "cloudy", "overcast" -> "☁️"
                "rain" -> "🌧️"
                "storm" -> "⛈️"
                "snow" -> "❄️"
                "wind" -> "💨"
                else -> "❓"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_forecast, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(forecasts[position])
    }

    override fun getItemCount(): Int = forecasts.size

    fun updateData(newForecasts: List<Forecast>) {
        forecasts = newForecasts
        notifyDataSetChanged()
    }
}