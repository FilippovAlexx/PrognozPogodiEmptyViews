package com.example.prognozpogodiemptyviews.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.prognozpogodiemptyviews.interfaces.CityRepository
import com.example.prognozpogodiemptyviews.models.City

class DetailViewModel(private val repository: CityRepository) : ViewModel() {

    private val _city = MutableLiveData<City?>()
    val city: LiveData<City?> = _city

    fun loadCity(cityName: String) {
        val found = repository.getCityByName(cityName)
        _city.value = found
    }
}