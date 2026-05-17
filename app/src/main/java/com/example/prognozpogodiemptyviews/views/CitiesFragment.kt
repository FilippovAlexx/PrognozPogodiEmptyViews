package com.example.prognozpogodiemptyviews.views

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prognozpogodiemptyviews.MyApplication
import com.example.prognozpogodiemptyviews.R
import com.example.prognozpogodiemptyviews.adapters.CitiesAdapter
import com.example.prognozpogodiemptyviews.viewmodels.CitiesViewModel
import com.example.prognozpogodiemptyviews.viewmodels.ViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class CitiesFragment : Fragment() {

    private lateinit var rvCities: RecyclerView
    private lateinit var fabAddCity: FloatingActionButton
    private lateinit var adapter: CitiesAdapter
    private lateinit var viewModel: CitiesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_cities, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvCities = view.findViewById(R.id.rvCities)
        fabAddCity = view.findViewById(R.id.fabAddCity)

        val btnRefresh = view.findViewById<Button>(R.id.btnRefresh)
        btnRefresh.setOnClickListener {
            viewModel.refreshWeather { success, message ->
                Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
            }
        }

        val repository = (requireActivity().application as MyApplication).cityRepository
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), factory).get(CitiesViewModel::class.java)

        setupRecyclerView(view)
        setupFabButton(view)

        viewModel.cities.observe(viewLifecycleOwner) { cities ->
            adapter.updateData(cities)
        }

        if (repository.getAllCitiesSorted().isEmpty()) {
            viewModel.addDemoCities()
        }
    }

    private fun setupRecyclerView(view: View) {
        adapter = CitiesAdapter(
            cities = emptyList(),
            onItemClick = { city ->
                val bundle = Bundle().apply { putString("city_name", city.name) }
                view.findNavController().navigate(R.id.action_citiesFragment_to_detailFragment, bundle)
                Snackbar.make(view, "Выбран город ${city.name}", Snackbar.LENGTH_SHORT).show()
            },
            onFavoriteClick = { city ->
                viewModel.toggleFavorite(city)
                val msg = if (city.isFavorite) "Добавлен в избранное" else "Удалён из избранного"
                Snackbar.make(view, "$msg: ${city.name}", Snackbar.LENGTH_SHORT).show()
            },
            onDeleteClick = { city ->
                val cityName = city.name
                viewModel.deleteCity(city)
                Snackbar.make(view, "Город $cityName удалён", Snackbar.LENGTH_SHORT)
                    .setAction("Отмена") {
                        viewModel.addCity(cityName)
                    }.show()
            }
        )
        rvCities.layoutManager = LinearLayoutManager(requireContext())
        rvCities.adapter = adapter
    }

    private fun setupFabButton(view: View) {
        fabAddCity.setOnClickListener {
            showAddCityDialog(view)
        }
    }

    private fun showAddCityDialog(view: View) {
        val inputEditText = EditText(requireContext())
        inputEditText.hint = "Название города"
        inputEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS

        AlertDialog.Builder(requireContext())
            .setTitle("Добавить город")
            .setView(inputEditText)
            .setPositiveButton("Добавить") { _, _ ->
                val cityName = inputEditText.text.toString().trim()
                if (cityName.isNotEmpty()) {
                    val success = viewModel.addCity(cityName)
                    if (success) {
                        Snackbar.make(view, "Город $cityName добавлен", Snackbar.LENGTH_SHORT).show()
                    } else {
                        Snackbar.make(view, "Город $cityName уже есть в списке", Snackbar.LENGTH_SHORT).show()
                    }
                } else {
                    Snackbar.make(view, "Название не может быть пустым", Snackbar.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}