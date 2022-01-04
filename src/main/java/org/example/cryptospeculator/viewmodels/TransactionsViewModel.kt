package org.example.cryptospeculator.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.cryptospeculator.datastorage.PortfolioEntity
import org.example.cryptospeculator.datastorage.TransactionHistoryEntity
import org.example.cryptospeculator.repos.TransactionsRepo

class TransactionsViewModel: ViewModel() {

    val liveStats = MutableLiveData<List<TransactionHistoryEntity>> (ArrayList())
    private val repo = TransactionsRepo()
    val isLoading = MutableLiveData(false)

    fun refresh() {
        isLoading.value = true
        viewModelScope.launch  {
            var result =  withContext(Dispatchers.IO){
                repo.getTransactionHistory()
            }
            isLoading.value = false
            liveStats.value = result
        }
    }
}
