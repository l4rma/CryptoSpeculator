package org.example.cryptospeculator.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.cryptospeculator.datastorage.*
import org.example.cryptospeculator.models.CurrencyItem
import org.example.cryptospeculator.repos.CurrencyListRepo

class MainViewModel(context: Context) : ViewModel() {
    val liveStats = MutableLiveData<List<CurrencyItem>>()
    val portfolioEntity = MutableLiveData<List<PortfolioEntity>>()

    private var repo = CurrencyListRepo()
    private var portfolioDao: PortfolioDao = CurrencyDatabase.get(context).getPortfolioDao()
    private var transactionHistoryDao : TransactionHistoryDao = CurrencyDatabase.get(context).getTransactionHistoryDao()

    val isLoading = MutableLiveData(false)

    fun refresh() {
        isLoading.value = true
        viewModelScope.launch {
            var result = withContext(Dispatchers.IO) {
                repo.getCurrencySummary()
            }

            isLoading.value = false
            liveStats.value = result

        }
        getPortfolio()
    }

    fun getPortfolio() {
        viewModelScope.launch {
            var result = withContext(Dispatchers.IO) {
                portfolioDao.listPortfolio()
            }
            portfolioEntity.value = result
        }
    }

    fun insertTransaction(entity: TransactionHistoryEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                transactionHistoryDao.insertTransaction(entity)
            }
        }
    }

    fun insertPortfolioEntity(entity: PortfolioEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                portfolioDao.insertCurrency(entity)
            }
        }
    }

}