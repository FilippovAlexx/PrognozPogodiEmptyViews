package com.example.prognozpogodiemptyviews.interfaces

import com.example.prognozpogodiemptyviews.models.City

interface CityRepository {
    fun saveCities(cities: List<City>)
    fun loadCities(): MutableList<City>
    fun getCityByName(name: String): City?
    fun addCity(city: City)
    fun deleteCity(city: City)
    fun updateCity(updatedCity: City)
    fun getAllCitiesSorted(): List<City>
}