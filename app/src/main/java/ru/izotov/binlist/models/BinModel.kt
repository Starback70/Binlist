package ru.izotov.binlist.models


data class BinModel(
    val cardNumber: String,
    val scheme : String,
    val brand: String,
    val type : String,
    val prepaid: String,
    // number
    val length: String,
    val luhn: String,
    // country
    val countryName: String,
    val countryEmoji: String,
    val countryLatitude: String,
    val countryLongitude: String,
    // bank
    val bankName: String,
    val bankUrl: String,
    val bankPhone: String,
    val bankCity: String,
)
