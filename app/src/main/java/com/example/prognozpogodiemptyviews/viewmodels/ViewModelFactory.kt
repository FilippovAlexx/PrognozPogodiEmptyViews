package com.example.prognozpogodiemptyviews.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.prognozpogodiemptyviews.interfaces.CityRepository

class ViewModelFactory(private val repository: CityRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CitiesViewModel::class.java) -> CitiesViewModel(repository) as T
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> DetailViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}