package org.example.cryptospeculator.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.cryptospeculator.datastorage.PortfolioEntity
import org.example.cryptospeculator.models.CurrencyItem
import org.example.cryptospeculator.models.PortfolioItem
import org.example.cryptospeculator.repos.CurrencyListRepo
import org.example.cryptospeculator.repos.PortfolioRepo

class PortfolioViewModel : ViewModel() {

    val currencyItemList = MutableLiveData<List<CurrencyItem>> (ArrayList())
    val portfolioList = MutableLiveData<List<PortfolioEntity>> (ArrayList())
    val liveData = MutableLiveData<List<PortfolioItem>> (ArrayList())
    val totalPortfolioValue = MutableLiveData<Double?>()

    private var currencyRepo = CurrencyListRepo()
    private val portfolioRepo = PortfolioRepo()

    val isLoading = MutableLiveData(true)

    fun refreshLists() {
        isLoading.value = true
        viewModelScope.launch  {
            var result =  withContext(Dispatchers.IO){
                portfolioRepo.getPortfolio()
            }
            portfolioList.value = result
        }
        viewModelScope.launch  {
            var result =  withContext(Dispatchers.IO){
                currencyRepo.getCurrencySummary()
            }
            isLoading.value = false
            currencyItemList.value = result
        }
    }

    fun makeNewPortfolioItemList() {
        var newList: MutableList<PortfolioItem> = mutableListOf()
        portfolioList.value?.forEach { portItem ->
            if (portItem.currencyName == "dollar") {
                var portfolioItem = PortfolioItem(
                    portItem.currencyName,
                    "USD",
                    portItem.amount,
                    portItem.amount,
                    portItem.amount
                )
                newList.add(portfolioItem)
            } else {
                var currency = currencyItemList.value?.find { currItem ->
                    currItem.id == portItem.currencyName
                }
                var value = currency?.priceUsd?.times(portItem.amount)
                value = value?: 0.0
                var portfolioItem = PortfolioItem(
                    portItem.currencyName,
                    currency?.symbol,
                    portItem.amount,
                    currency?.priceUsd,
                    value
                )
                newList.add(portfolioItem)
            }
        }
        liveData.value = newList
    }
}
