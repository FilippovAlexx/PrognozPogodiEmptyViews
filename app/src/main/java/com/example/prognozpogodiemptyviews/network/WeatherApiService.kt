package com.example.prognozpogodiemptyviews.network

import com.example.prognozpogodiemptyviews.network.responses.ServerResponse
import retrofit2.http.GET

interface WeatherApiService {
    @GET("weather-forecast")
    suspend fun getWeatherForecast(): ServerResponse
}