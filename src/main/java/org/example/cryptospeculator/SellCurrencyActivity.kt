package org.example.cryptospeculator

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import org.example.cryptospeculator.databinding.ActivitySellCurrencyBinding
import org.example.cryptospeculator.datastorage.TransactionHistoryEntity
import org.example.cryptospeculator.models.CurrencyItem
import org.example.cryptospeculator.viewmodels.SellCurrencyViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


class SellCurrencyActivity : AppCompatActivity() {

    private lateinit var currencyItem: CurrencyItem
    private lateinit var viewModel : SellCurrencyViewModel
    private lateinit var binding : ActivitySellCurrencyBinding

    private var sellAmount: Double = 0.0
    private var sellPrice: Double = 0.0
    private var id: String = ""
    private var availableCash: Double = 0.0
    private var currentCurrencyAmount: Double? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_CryptoSpeculator)
        binding  = ActivitySellCurrencyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = SellCurrencyViewModel(this)

        viewModel.liveStats.observe(this, {
            currencyItem = it
            binding.currencyName.text = currencyItem.name
            binding.currencySymbol.text = currencyItem.symbol
            binding.buySellSymbol.text = currencyItem.symbol
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
            currentCurrencyAmount = if (it.isNullOrEmpty()) null else it[0].amount
        })

        // On button click
        binding.btnConfirm.setOnClickListener {
            binding.btnConfirm.isEnabled = false

            // Add transaction to transaction table
            var entity =
                    TransactionHistoryEntity(
                        "sold",
                        currencyItem.name,
                        currencyItem.symbol,
                        sellPrice,
                        sellAmount,
                        DateTimeFormatter
                            .ofPattern("dd-MM-yyyy HH:mm:ss")
                            .withZone(ZoneId.of("Europe/Paris"))
                            .format(Instant.now()).toString()
                    )
            viewModel.insertTransaction(entity)

            // Update portfolio
            val newValue = currentCurrencyAmount!! - sellPrice
            if (newValue == 0.0) {
                viewModel.deletePortfolioEntity(id);
            } else {
                viewModel.updatePortfolioEntity(id, newValue)
            }

            val newDollar = availableCash + sellAmount
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
                sellPrice = binding.buySellEdit.text.toString().replace(",",".").toDouble()

                sellAmount = setCorrectNumberOfDecimals(sellPrice * currencyItem.priceUsd).replace(",", ".").toDouble()

                if (sellPrice > 0) {
                    binding.buySellValue.text = sellAmount.toString()
                } else {
                    binding.buySellValue.text = "0"
                }
                if (binding.buySellEdit.text.toString().replace(",",".").toDouble() > currentCurrencyAmount!!) {
                    binding.btnConfirm.isEnabled = false
                    binding.buySellInfo.text = "You dont have that much cryptocurrency"
                } else {
                    binding.buySellInfo.text = "You can only sell cryptocurrency for USD"
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