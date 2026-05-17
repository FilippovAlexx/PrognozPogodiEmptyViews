package com.example.prognozpogodiemptyviews.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prognozpogodiemptyviews.data.CityRepository
import com.example.prognozpogodiemptyviews.models.City
import com.example.prognozpogodiemptyviews.models.Forecast
import kotlinx.coroutines.launch
import java.util.UUID

class CitiesViewModel(private val repository: CityRepository) : ViewModel() {

    private val _cities = MutableLiveData<List<City>>()
    val cities: LiveData<List<City>> = _cities

    init {
        loadCities()
    }

    fun loadCities() {
        _cities.value = repository.getAllCitiesSorted()
    }

    fun addCity(cityName: String): Boolean {
        if (cityName.isBlank()) return false
        if (repository.getCityByName(cityName) != null) return false

        val newCity = City(
            id = UUID.randomUUID().toString(),
            name = cityName,
            forecasts = emptyList(),
            isFavorite = false
        )
        repository.addCity(newCity)
        loadCities()
        return true
    }

    fun deleteCity(city: City) {
        repository.deleteCity(city)
        loadCities()
    }

    fun toggleFavorite(city: City) {
        val updated = city.copy(isFavorite = !city.isFavorite)
        repository.updateCity(updated)
        loadCities()
    }

    fun addDemoCities() {
        if (repository.getAllCitiesSorted().isNotEmpty()) return
        repository.addCity(City(UUID.randomUUID().toString(), "Москва", emptyList(), true))
        repository.addCity(City(UUID.randomUUID().toString(), "Санкт-Петербург", emptyList(), false))
        repository.addCity(City(UUID.randomUUID().toString(), "Новосибирск", emptyList(), false))
        loadCities()
    }

    fun refreshWeather(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val serverCities = repository.fetchWeatherFromServer()
                if (serverCities.isNotEmpty()) {
                    repository.mergeWithServerCities(serverCities)
                    loadCities()
                    onResult(true, "Данные обновлены")
                } else {
                    onResult(false, "Сервер вернул пустой ответ")
                }
            } catch (e: Exception) {
                onResult(false, "Ошибка: ${e.localizedMessage ?: "Неизвестная ошибка"}")
            }
        }
    }
}