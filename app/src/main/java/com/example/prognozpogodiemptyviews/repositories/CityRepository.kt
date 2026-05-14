package com.example.prognozpogodiemptyviews.repositories

import android.content.Context
import android.content.SharedPreferences
import com.example.prognozpogodiemptyviews.interfaces.CityRepository
import com.example.prognozpogodiemptyviews.models.City
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CityRepositoryImpl(context: Context) : CityRepository {
    //узнать, где хранится (какая память используется)
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
        cities.removeAll { it.name.equals(city.name, ignoreCase = true) }
        saveCities(cities)
    }

    override fun updateCity(updatedCity: City) {
        val cities = loadCities().toMutableList()
        val index = cities.indexOfFirst { it.name.equals(updatedCity.name, ignoreCase = true) }
        if (index != -1) {
            cities[index] = updatedCity
            saveCities(cities)
        }
    }

    override fun getAllCitiesSorted(): List<City> {
        val cities = loadCities()
        return cities.sortedByDescending { it.isFavorite }
    }
}