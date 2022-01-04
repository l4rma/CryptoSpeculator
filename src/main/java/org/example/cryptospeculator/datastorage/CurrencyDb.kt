package org.example.cryptospeculator.datastorage

import android.content.Context
import androidx.room.*


@Entity
data class PortfolioEntity(
    @ColumnInfo(name = "currency_id") var currencyName: String,
    @ColumnInfo(name = "amount") var amount: Double
) {
    @PrimaryKey(autoGenerate = true) var priId: Int? = null
}

@Entity
data class TransactionHistoryEntity(
    @ColumnInfo(name = "buy_sell") var buySell: String,
    @ColumnInfo(name = "currency_id") var currencyId: String?,
    @ColumnInfo(name = "currency_symbol") var currencySymbol: String,
    @ColumnInfo(name = "amount") var amount: Double,
    @ColumnInfo(name = "price") var price: Double,
    @ColumnInfo(name = "timestamp") var timestamp: String,
) {
    @PrimaryKey(autoGenerate = true) var priId: Int? = null
}

@Dao
interface PortfolioDao {
    @Insert
    fun insertCurrency(vararg item: PortfolioEntity)

    @Query("Update PortfolioEntity SET amount=:amount WHERE currency_id=:currencyId")
    fun updateCurrency(currencyId: String, amount: Double)

    @Query("SELECT * FROM PortfolioEntity WHERE currency_id=:currencyId")
    fun getPortfolioEntry(currencyId: String) : List<PortfolioEntity>

    @Query("SELECT * FROM PortfolioEntity")
    fun listPortfolio() : List<PortfolioEntity>

    @Query("DELETE FROM PortfolioEntity WHERE currency_id=:currencyId")
    fun deletePortfolioEntry(currencyId: String)
}

@Dao
interface TransactionHistoryDao {
    @Insert
    fun insertTransaction(vararg item: TransactionHistoryEntity)

    @Update
    fun updateTransaction(vararg item: TransactionHistoryEntity)

    @Query("SELECT * FROM TransactionHistoryEntity")
    fun listTransactionHistory() : List<TransactionHistoryEntity>
}

@Database(entities = [PortfolioEntity::class, TransactionHistoryEntity::class], version = 1, exportSchema = false)
abstract class CurrencyDatabase: RoomDatabase() {
    abstract fun getPortfolioDao() : PortfolioDao
    abstract fun getTransactionHistoryDao() : TransactionHistoryDao

    companion object {
        var DB_FILENAME = "currencydatabasefile"

        @Volatile
        private var INSTANCE: CurrencyDatabase? = null

        fun get(context: Context) : CurrencyDatabase {
            // Singleton
            val tmp =
                INSTANCE
            if (tmp != null) {
                return tmp
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CurrencyDatabase::class.java,
                    DB_FILENAME)
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}

