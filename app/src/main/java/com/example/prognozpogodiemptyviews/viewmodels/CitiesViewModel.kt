package com.example.prognozpogodiemptyviews.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.prognozpogodiemptyviews.interfaces.CityRepository
import com.example.prognozpogodiemptyviews.models.City
import com.example.prognozpogodiemptyviews.models.Weather
import com.example.prognozpogodiemptyviews.models.WeatherVariant
import kotlin.random.Random

class CitiesViewModel(private val repository: CityRepository) : ViewModel() {

    private val _cities = MutableLiveData<List<City>>() //использовать StateFlow - более современная штука
    val cities: LiveData<List<City>> = _cities

    private val weatherVariants = listOf(
        WeatherVariant("Солнечно", "☀️", 20, 35),
        WeatherVariant("Облачно", "☁️", 10, 20),
        WeatherVariant("Дождливо", "🌧️", 5, 15),
        WeatherVariant("Снег", "❄️", -10, 0),
        WeatherVariant("Ветрено", "💨", 5, 20)
    )

    init {
        loadCities()
    }

    fun loadCities() {
        val sorted = repository.getAllCitiesSorted()
        _cities.value = sorted
    }

    fun addCity(cityName: String): Boolean {
        if (cityName.isBlank()) return false
        val exists = repository.getCityByName(cityName) != null
        if (exists) return false

        val randomWeather = generateRandomWeather()
        val newCity = City(cityName, randomWeather, false)
        repository.addCity(newCity)
        loadCities()
        return true
    }

    fun deleteCity(city: City) {
        repository.deleteCity(city)
        loadCities()
    }

    fun toggleFavorite(city: City) {
        city.isFavorite = !city.isFavorite
        repository.updateCity(city)
        loadCities()
    }

    private fun generateRandomWeather(): Weather {
        val randomIndex = Random.nextInt(weatherVariants.size)
        val variant = weatherVariants[randomIndex]
        val temperature = Random.nextInt(variant.minTemp, variant.maxTemp + 1)
        return Weather(temperature, variant.description, variant.icon)
    }

    fun addDemoCities() {
        val weather1 = Weather(24, "Солнечно", "☀️")
        val weather2 = Weather(15, "Облачно", "☁️")
        val weather3 = Weather(8, "Дождливо", "🌧️")
        val city1 = City("Москва", weather1, true)
        val city2 = City("Санкт-Петербург", weather2, false)
        val city3 = City("Новосибирск", weather3, false)
        repository.addCity(city1)
        repository.addCity(city2)
        repository.addCity(city3)
        loadCities()
    }
}