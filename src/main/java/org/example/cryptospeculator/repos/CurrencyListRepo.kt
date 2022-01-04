package org.example.cryptospeculator.repos

import org.example.cryptospeculator.datasources.retrofit.CoinCapApiClient
import org.example.cryptospeculator.models.CurrencyItem

class CurrencyListRepo {

    private val coinCapApiClient = CoinCapApiClient()

    suspend fun getCurrencySummary() : List<CurrencyItem> {
        var list = coinCapApiClient.getSummary()
        return list
    }

    suspend fun getCurrencyById(id: String) : CurrencyItem {
        var currencyItem = coinCapApiClient.getCurrencyById(id)
        return currencyItem
    }
}