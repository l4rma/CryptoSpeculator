package org.example.cryptospeculator.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.cryptospeculator.datastorage.CurrencyDatabase
import org.example.cryptospeculator.datastorage.PortfolioDao
import org.example.cryptospeculator.datastorage.PortfolioEntity
import org.example.cryptospeculator.models.CurrencyItem

class CurrencyViewModel(context: Context) : ViewModel() {
    val availableCash = MutableLiveData<List<PortfolioEntity>>()
    val currentCurrency = MutableLiveData<List<PortfolioEntity>>()
    private var portfolioDao: PortfolioDao = CurrencyDatabase.get(context).getPortfolioDao()

    fun refresh(id: String?) {
        getPortfolioEntity("dollar", availableCash)
        getPortfolioEntity(id!!, currentCurrency)
    }

    fun getPortfolioEntity(id: String, liveData: MutableLiveData<List<PortfolioEntity>>) {
        viewModelScope.launch {
            var result = withContext(Dispatchers.IO) {
                portfolioDao.getPortfolioEntry(id)
            }
            liveData.value = result
        }
    }

}