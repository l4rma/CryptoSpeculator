package org.example.cryptospeculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import org.example.cryptospeculator.databinding.ActivityCurrencyBinding
import org.example.cryptospeculator.viewmodels.CurrencyViewModel


class CurrencyActivity : AppCompatActivity() {

    private var id: String? = null
    private var amount: Double = 0.0

    private var price: Double = 0.0
    private var symbol: String? = ""
    private lateinit var viewModel: CurrencyViewModel
    private lateinit var binding: ActivityCurrencyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_CryptoSpeculator)

        binding  = ActivityCurrencyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getExtra(intent, binding)

        viewModel = CurrencyViewModel(this)

        viewModel.availableCash.observe(this, {
            binding.header.text = "Cash: \$${setCorrectNumberOfDecimals(it[0].amount)}"
        })

        viewModel.currentCurrency.observe(this, {
            binding.btnSell.isEnabled = false
            amount = if(it.isNullOrEmpty()) 0.0 else it[0].amount
            if(amount != 0.0) binding.btnSell.isEnabled = true

            binding.currencyFragmentYouHave.text = "You currently have ${setCorrectNumberOfDecimals(amount)} $symbol"
            binding.currencyFragmentCalculate.text = "${setCorrectNumberOfDecimals(amount)} x \$${setCorrectNumberOfDecimals(price)}"
            binding.currencyFragmentValue.text = "Value: \$${String.format("%.2f",(amount * price))}"
        })

        binding.btnBuy.setOnClickListener {
            val intent = Intent(this, BuyCurrencyActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
        binding.btnSell.setOnClickListener {
            val intent = Intent(this, SellCurrencyActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh(id)
    }

    fun getExtra(intent: Intent, binding: ActivityCurrencyBinding) {
        id = intent.getStringExtra("id")
        val name = intent.getStringExtra("name")
        symbol = intent.getStringExtra("symbol")
        price = intent.getDoubleExtra("price", 0.0)
        binding.currencyName.text = name
        binding.currencySymbol.text = symbol
        binding.currencyPrice.text = "\$${setCorrectNumberOfDecimals(price)}"
        Glide.with(binding.image)
            .load("https://static.coincap.io/assets/icons/${symbol?.toLowerCase()}@2x.png")
            .error(R.drawable.ic_dollar)
            .into(binding.image)
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