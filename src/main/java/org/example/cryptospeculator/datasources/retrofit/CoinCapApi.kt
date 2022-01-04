package org.example.cryptospeculator.datasources.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CoinCapApi {

    @GET("v2/assets")
    fun getListAllCurrencies() : Call<SummaryResponse>

    @GET("v2/assets/{currencyId}")
    fun getCurrency(@Path("currencyId") currencyId: String): Call<CurrencyResponse>


}