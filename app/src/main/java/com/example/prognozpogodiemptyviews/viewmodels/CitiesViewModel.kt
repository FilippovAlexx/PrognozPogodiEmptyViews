package com.example.prognozpogodiemptyviews.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prognozpogodiemptyviews.interfaces.CityRepository
import com.example.prognozpogodiemptyviews.models.City
import com.example.prognozpogodiemptyviews.models.Forecast
import com.example.prognozpogodiemptyviews.models.WeatherVariant
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import kotlin.random.Random

class CitiesViewModel(private val repository: CityRepository) : ViewModel() {

    private val _cities = MutableLiveData<List<City>>()
    val cities: LiveData<List<City>> = _cities

    private val weatherVariants = listOf(
        WeatherVariant("Солнечно", "sunny", 20, 35),
        WeatherVariant("Облачно", "cloudy", 10, 20),
        WeatherVariant("Дождливо", "rain", 5, 15),
        WeatherVariant("Снег", "snow", -10, 0),
        WeatherVariant("Ветрено", "wind", 5, 20)
    )

    init {
        loadCities()
    }

    fun loadCities() {
        _cities.value = repository.getAllCitiesSorted()
    }

    private fun generateRandomForecasts(days: Int = 3): List<Forecast> {
        val forecasts = mutableListOf<Forecast>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val now = Calendar.getInstance()

        for (i in 0 until days) {
            val variant = weatherVariants.random()
            val temperature = Random.nextInt(variant.minTemp, variant.maxTemp + 1)
            val forecastCalendar = now.clone() as Calendar
            forecastCalendar.add(Calendar.DAY_OF_MONTH, i)
            forecasts.add(
                Forecast(
                    id = UUID.randomUUID().toString(),
                    date = dateFormat.format(forecastCalendar.time),
                    temperature = temperature,
                    description = variant.description,
                    icon = variant.icon,
                    updatedAt = dateFormat.format(System.currentTimeMillis())
                )
            )
        }
        return forecasts
    }

    fun addCity(cityName: String): Boolean {
        if (cityName.isBlank()) return false
        if (repository.getCityByName(cityName) != null) return false

        val newCity = City(
            id = UUID.randomUUID().toString(),
            name = cityName,
            forecasts = generateRandomForecasts(3),
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
        repository.addCity(City(UUID.randomUUID().toString(), "Москва", generateRandomForecasts(3), true))
        repository.addCity(City(UUID.randomUUID().toString(), "Санкт-Петербург", generateRandomForecasts(3), false))
        repository.addCity(City(UUID.randomUUID().toString(), "Новосибирск", generateRandomForecasts(3), false))
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