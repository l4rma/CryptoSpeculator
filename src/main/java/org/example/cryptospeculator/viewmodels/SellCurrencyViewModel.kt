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

class SellCurrencyViewModel(context: Context): ViewModel(){
    val liveStats = MutableLiveData<CurrencyItem>()
    val currentCurrency = MutableLiveData<List<PortfolioEntity>>()
    val availableCash = MutableLiveData<List<PortfolioEntity>>()

    val isLoading = MutableLiveData(false)

    private var repo = CurrencyListRepo()
    private var transactionHistoryDao : TransactionHistoryDao = CurrencyDatabase.get(context).getTransactionHistoryDao()
    private var portfolioDao: PortfolioDao = CurrencyDatabase.get(context).getPortfolioDao()

    fun refresh(id: String) {
        isLoading.value = true
        viewModelScope.launch {
            var result = withContext(Dispatchers.IO) {
                repo.getCurrencyById(id)
            }

            isLoading.value = false
            liveStats.value = result
        }
        getPortfolioEntity("dollar", availableCash)
        getPortfolioEntity(id, currentCurrency)
    }

    fun insertTransaction(entity: TransactionHistoryEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                transactionHistoryDao.insertTransaction(entity)
            }
        }
    }

    fun getPortfolioEntity(id: String, liveData: MutableLiveData<List<PortfolioEntity>>) {
        viewModelScope.launch {
            var result = withContext(Dispatchers.IO) {
                portfolioDao.getPortfolioEntry(id)
            }
            liveData.value = result
        }
    }

    fun updatePortfolioEntity(id: String, amount : Double) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                portfolioDao.updateCurrency(id, amount)
            }
        }
    }

    fun deletePortfolioEntity(id: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                portfolioDao.deletePortfolioEntry(id)
            }
        }
    }

    fun insertEntityInPortfolio(entity: PortfolioEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                portfolioDao.insertCurrency(entity)
            }
        }
    }

}