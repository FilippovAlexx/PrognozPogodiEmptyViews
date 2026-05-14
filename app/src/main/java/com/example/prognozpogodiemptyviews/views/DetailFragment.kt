package com.example.prognozpogodiemptyviews.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.prognozpogodiemptyviews.MyApplication
import com.example.prognozpogodiemptyviews.R
import com.example.prognozpogodiemptyviews.viewmodels.DetailViewModel
import com.example.prognozpogodiemptyviews.viewmodels.ViewModelFactory
import kotlin.random.Random

class DetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cityName = arguments?.getString("city_name")
        if (cityName == null) {
            view.findViewById<TextView>(R.id.tvCityNameDetail).text = "Ошибка"
            return
        }

        val repository = (requireActivity().application as MyApplication).cityRepository
        val factory = ViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory).get(DetailViewModel::class.java)

        viewModel.city.observe(viewLifecycleOwner) { city ->
            if (city == null) {
                view.findViewById<TextView>(R.id.tvCityNameDetail).text = "Город не найден"
                return@observe
            }
            view.findViewById<TextView>(R.id.tvCityNameDetail).text = city.name
            view.findViewById<TextView>(R.id.tvTemperatureDetail).text = "${city.weather.temperature}°C"
            view.findViewById<TextView>(R.id.tvIconDetail).text = city.weather.icon
            view.findViewById<TextView>(R.id.tvDescriptionDetail).text = city.weather.description

            val humidity = Random.nextInt(30, 90)
            val windSpeed = Random.nextInt(0, 20)
            val pressure = Random.nextInt(980, 1040)

            view.findViewById<TextView>(R.id.tvHumidity).text = "Влажность: $humidity%"
            view.findViewById<TextView>(R.id.tvWind).text = "Ветер: $windSpeed м/с"
            view.findViewById<TextView>(R.id.tvPressure).text = "Давление: $pressure гПа"
        }

        viewModel.loadCity(cityName)
    }
}