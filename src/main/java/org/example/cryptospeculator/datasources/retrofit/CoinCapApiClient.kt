package org.example.cryptospeculator.datasources.retrofit

import com.google.gson.GsonBuilder
import org.example.cryptospeculator.models.CurrencyItem
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CoinCapApiClient {
    private var coinCapApi : CoinCapApi

    init {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl("https://api.coincap.io/")
            .build()

        coinCapApi = retrofit.create(CoinCapApi::class.java)
    }

    fun getSummary(): List<CurrencyItem> {
        var response = coinCapApi.getListAllCurrencies().execute()

        try {
            if(response.isSuccessful) {
                response.body()?.data?.let { currencies ->
                    return currencies
                }
            }
        }
        catch (ex: Exception){
            ex.printStackTrace()
        }

        return ArrayList()
    }

    fun getCurrencyById(id: String): CurrencyItem {
        var response = coinCapApi.getCurrency(id).execute()

        try {
            if(response.isSuccessful) {
                response.body()?.data?.let {
                    return it
                }
            }
        }
        catch (ex: Exception){
            ex.printStackTrace()
        }

        return CurrencyItem("","","xrp",0.0,0.0)
    }
}