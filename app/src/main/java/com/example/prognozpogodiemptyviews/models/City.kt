package com.example.prognozpogodiemptyviews.models

data class City(
    val id: String,
    val name: String,
    val forecasts: List<Forecast> = emptyList(),
    val isFavorite: Boolean = false
)