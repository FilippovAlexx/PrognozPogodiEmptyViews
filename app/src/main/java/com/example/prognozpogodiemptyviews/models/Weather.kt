package com.example.prognozpogodiemptyviews.models

class Weather(
    var temperature: Int,
    var description: String,
    var icon: String
) {
    constructor() : this(0, "", "")
}