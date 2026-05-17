package com.example.prognozpogodiemptyviews.models

data class Forecast(
    val id: String,
    val date: String,
    val temperature: Int,
    val description: String,
    val icon: String,
    val updatedAt: String
)