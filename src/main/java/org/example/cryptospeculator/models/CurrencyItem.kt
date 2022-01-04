package org.example.cryptospeculator.models

data class CurrencyItem(
    val id: String,
    val symbol: String,
    val name: String,
    val priceUsd: Double,
    val changePercent24Hr: Double
    )
