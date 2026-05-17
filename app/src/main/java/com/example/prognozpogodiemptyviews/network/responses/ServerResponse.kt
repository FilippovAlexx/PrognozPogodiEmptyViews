package com.example.prognozpogodiemptyviews.network.responses

import com.example.prognozpogodiemptyviews.models.City

data class ServerResponse(
    val cities: List<City>
)