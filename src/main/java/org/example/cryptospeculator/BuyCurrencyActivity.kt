package org.example.cryptospeculator

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import org.example.cryptospeculator.databinding.ActivityBuyCurrencyBinding
import org.example.cryptospeculator.datastorage.PortfolioEntity
import org.example.cryptospeculator.datastorage.TransactionHistoryEntity
import org.example.cryptospeculator.models.CurrencyItem
import org.example.cryptospeculator.viewmodels.BuyCurrencyViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter



class BuyCurrencyActivity : AppCompatActivity() {

    private lateinit var viewModel : BuyCurrencyViewModel
    private lateinit var binding : ActivityBuyCurrencyBinding
    private lateinit var currencyItem: CurrencyItem

    private var id: String = ""
    private var buyPrice: Double = 0.0
    private var buyAmount: Double = 0.0
    private var availableCash: Double = 0.0
    private var currentCurrencyAmount: Double? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_CryptoSpeculator)

        binding  = ActivityBuyCurrencyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = BuyCurrencyViewModel(this)

        viewModel.liveStats.observe(this, {
            currencyItem = it

            binding.currencyName.text = currencyItem.name
            binding.currencySymbol.text = currencyItem.symbol
            binding.valueSymbol.text = currencyItem.symbol
            binding.currencyPrice.text = "\$${setCorrectNumberOfDecimals(currencyItem.priceUsd)}"
            Glide.with(binding.image)
                .load("https://static.coincap.io/assets/icons/${currencyItem.symbol.toLowerCase()}@2x.png")
                .error(R.drawable.ic_dollar)
                .into(binding.image)
        })

        // Get dollar from portfolio
        viewModel.availableCash.observe(this, {
            availableCash = it[0].amount
        })
        // Get amount of current currency
        viewModel.currentCurrency.observe(this, {
            currentCurrencyAmount = if(it.isNullOrEmpty()) null else it[0].amount
        })

        // On button click
        binding.btnConfirm.setOnClickListener {
            binding.btnConfirm.isEnabled = false

            // Add transaction to transaction table
            var entity =
                TransactionHistoryEntity(
                    "bought",
                    currencyItem.name,
                    currencyItem.symbol,
                    buyAmount,
                    buyPrice,
                    DateTimeFormatter
                        .ofPattern("dd-MM-yyyy HH:mm:ss")
                        .withZone(ZoneId.of("Europe/Paris"))
                        .format(Instant.now()).toString()
                )
            viewModel.insertTransaction(entity)

            // Update portfolio
            if(currentCurrencyAmount != null) {
                val newValue = buyAmount + currentCurrencyAmount!!
                viewModel.updatePortfolioEntity(id, newValue)
            } else {
                viewModel.insertEntityInPortfolio(PortfolioEntity(id, buyAmount))
            }
            val newDollar = availableCash - buyPrice
            viewModel.updatePortfolioEntity("dollar", newDollar)

            finish()
        }

        binding.buySellEdit.addTextChangedListener(textWatcher)
    }

    override fun onResume() {
        super.onResume()
        id = intent.getStringExtra("id").toString()
        viewModel.refresh(id)
    }

    var textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            if (binding.buySellEdit.text.isNotEmpty()) {
                buyPrice = binding.buySellEdit.text.toString().replace(",",".").toDouble()
                buyAmount = setCorrectNumberOfDecimals(buyPrice / currencyItem.priceUsd).replace(",", ".").toDouble()

                if (buyPrice > 0) {
                    binding.buySellValue.text = buyAmount.toString().replace(",", ".")
                } else {
                    binding.buySellValue.text = "0"
                }
                if (binding.buySellEdit.text.toString().replace(",",".").toDouble() > availableCash) {
                    binding.btnConfirm.isEnabled = false
                    binding.buySellInfo.text = "You dont have that much cash"
                } else {
                    binding.buySellInfo.text = "You can only buy cryptocurrency with USD"
                    binding.btnConfirm.isEnabled =
                        binding.buySellValue.text.isNotEmpty() && binding.buySellEdit.text.isNotEmpty()
                }
            } else {
                binding.btnConfirm.isEnabled = false
                binding.buySellValue.text = "0"
            }
        }
    }

    private fun setCorrectNumberOfDecimals(number: Double): String {
        var str = "";
        when {
            number < 0.0001 -> {
                str = "0"
            }
            number > 0.9 -> {
                str = String.format("%.2f", number)
            }
            number > 0.0001 -> {
                str = String.format("%.5f", number)
            }
        }
        return str
    }
}