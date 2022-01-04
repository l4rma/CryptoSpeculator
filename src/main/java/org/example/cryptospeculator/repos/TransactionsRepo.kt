package org.example.cryptospeculator.repos

import org.example.cryptospeculator.TransactionsActivity
import org.example.cryptospeculator.datastorage.CurrencyDatabase
import org.example.cryptospeculator.datastorage.PortfolioDao
import org.example.cryptospeculator.datastorage.TransactionHistoryDao
import org.example.cryptospeculator.datastorage.TransactionHistoryEntity

class TransactionsRepo {

    private var transactionHistoryDao : TransactionHistoryDao = CurrencyDatabase.get(TransactionsActivity()).getTransactionHistoryDao()

    suspend fun getTransactionHistory() : List<TransactionHistoryEntity>  {
        return transactionHistoryDao.listTransactionHistory()
    }

}