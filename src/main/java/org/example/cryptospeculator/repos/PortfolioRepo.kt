package org.example.cryptospeculator.repos

import org.example.cryptospeculator.PortfolioActivity
import org.example.cryptospeculator.TransactionsActivity
import org.example.cryptospeculator.datastorage.*

class PortfolioRepo {

    private var portfolioDao : PortfolioDao = CurrencyDatabase.get(PortfolioActivity()).getPortfolioDao()

    suspend fun getPortfolio() : List<PortfolioEntity>  {
        return portfolioDao.listPortfolio()
    }
}
