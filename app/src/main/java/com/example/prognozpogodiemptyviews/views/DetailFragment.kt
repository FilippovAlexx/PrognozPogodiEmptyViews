package com.example.prognozpogodiemptyviews.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prognozpogodiemptyviews.MyApplication
import com.example.prognozpogodiemptyviews.R
import com.example.prognozpogodiemptyviews.adapters.ForecastsAdapter
import com.example.prognozpogodiemptyviews.viewmodels.DetailViewModel
import com.example.prognozpogodiemptyviews.viewmodels.ViewModelFactory

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

        val rvForecasts = view.findViewById<RecyclerView>(R.id.rvForecasts)
        val adapter = ForecastsAdapter()
        rvForecasts.layoutManager = LinearLayoutManager(requireContext())
        rvForecasts.adapter = adapter

        viewModel.city.observe(viewLifecycleOwner) { city ->
            if (city == null) {
                view.findViewById<TextView>(R.id.tvCityNameDetail).text = "Город не найден"
                return@observe
            }
            view.findViewById<TextView>(R.id.tvCityNameDetail).text = city.name
            adapter.updateData(city.forecasts)
        }

        viewModel.loadCity(cityName)
    }
}