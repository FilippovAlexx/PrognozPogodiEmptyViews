package com.example.prognozpogodiemptyviews.repositories

import android.content.Context
import android.content.SharedPreferences
import com.example.prognozpogodiemptyviews.interfaces.CityRepository
import com.example.prognozpogodiemptyviews.models.City
import com.example.prognozpogodiemptyviews.network.RetrofitClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CityRepositoryImpl(context: Context) : CityRepository {
    private val prefs: SharedPreferences = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    override fun saveCities(cities: List<City>) {
        val json = gson.toJson(cities)
        prefs.edit().putString("cities_list", json).apply()
    }

    override fun loadCities(): MutableList<City> {
        val json = prefs.getString("cities_list", null)
        if (json == null) return mutableListOf()
        val type = object : TypeToken<MutableList<City>>() {}.type
        return gson.fromJson(json, type)
    }

    override fun getCityByName(name: String): City? {
        return loadCities().find { it.name.equals(name, ignoreCase = true) }
    }

    override fun addCity(city: City) {
        val cities = loadCities().toMutableList()
        cities.add(city)
        saveCities(cities)
    }

    override fun deleteCity(city: City) {
        val cities = loadCities().toMutableList()
        cities.removeAll { it.id == city.id }
        saveCities(cities)
    }

    override fun updateCity(updatedCity: City) {
        val cities = loadCities().toMutableList()
        val index = cities.indexOfFirst { it.id == updatedCity.id }
        if (index != -1) {
            cities[index] = updatedCity
            saveCities(cities)
        }
    }

    override fun getAllCitiesSorted(): List<City> {
        return loadCities().sortedByDescending { it.isFavorite }
    }

    override suspend fun fetchWeatherFromServer(): List<City> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.apiService.getWeatherForecast()
            response.cities
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun mergeWithServerCities(serverCities: List<City>) = withContext(Dispatchers.IO) {
        val localCities = loadCities().toMutableList()
        val serverMap = serverCities.associateBy { it.id }

        val updatedLocal = localCities.map { localCity ->
            val serverCity = serverMap[localCity.id]
            if (serverCity != null) {
                localCity.copy(
                    name = serverCity.name,
                    forecasts = serverCity.forecasts,
                    isFavorite = localCity.isFavorite
                )
            } else {
                localCity
            }
        }.toMutableList()

        serverCities.forEach { serverCity ->
            if (updatedLocal.none { it.id == serverCity.id }) {
                updatedLocal.add(serverCity.copy(isFavorite = false))
            }
        }

        saveCities(updatedLocal)
    }
}