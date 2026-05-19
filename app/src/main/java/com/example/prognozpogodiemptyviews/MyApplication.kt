package com.example.prognozpogodiemptyviews

import android.app.Application
import com.example.prognozpogodiemptyviews.interfaces.CityRepository
import com.example.prognozpogodiemptyviews.repositories.CityRepositoryImpl

class MyApplication : Application() {
    lateinit var cityRepository: CityRepository
        private set

    override fun onCreate() {
        super.onCreate()
        cityRepository = CityRepositoryImpl(applicationContext)
    }
}