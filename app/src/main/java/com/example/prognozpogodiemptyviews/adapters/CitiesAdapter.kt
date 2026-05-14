package com.example.prognozpogodiemptyviews.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prognozpogodiemptyviews.R
import com.example.prognozpogodiemptyviews.models.City

class CitiesAdapter(
    private var cities: List<City>,
    private val onItemClick: (City) -> Unit,
    private val onFavoriteClick: (City) -> Unit,
    private val onDeleteClick: (City) -> Unit
) : RecyclerView.Adapter<CitiesAdapter.CityViewHolder>() {

    inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCityName: TextView = itemView.findViewById(R.id.tvCityName)
        private val tvTemperature: TextView = itemView.findViewById(R.id.tvTemperature)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvIcon: TextView = itemView.findViewById(R.id.weatherIcon)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        private val imgFavorite: ImageView = itemView.findViewById(R.id.imgFavorite)

        fun bind(city: City) {
            tvCityName.text = city.name
            tvTemperature.text = "${city.weather.temperature}°C"
            tvDescription.text = city.weather.description
            tvIcon.text = city.weather.icon

            imgFavorite.setImageResource(
                if (city.isFavorite) android.R.drawable.btn_star_big_on
                else android.R.drawable.btn_star_big_off
            )

            imgFavorite.setOnClickListener { onFavoriteClick(city) }
            btnDelete.setOnClickListener { onDeleteClick(city) }
            itemView.setOnClickListener { onItemClick(city) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weather, parent, false)
        return CityViewHolder(view)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(cities[position])
    }

    override fun getItemCount(): Int = cities.size

    fun updateData(newCities: List<City>) {
        cities = newCities
        notifyDataSetChanged()
    }
}