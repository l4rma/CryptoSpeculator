package org.example.cryptospeculator.models

data class PortfolioItem(
    val id: String,
    val symbol: String?,
    val amount: Double,
    val price: Double?,
    val value: Double
)