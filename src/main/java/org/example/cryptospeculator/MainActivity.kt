package org.example.cryptospeculator

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import org.example.cryptospeculator.adapters.CurrencyListAdapter
import org.example.cryptospeculator.databinding.ActivityMainBinding
import org.example.cryptospeculator.datastorage.PortfolioEntity
import org.example.cryptospeculator.datastorage.TransactionHistoryEntity
import org.example.cryptospeculator.viewmodels.MainViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), CurrencyListAdapter.OnItemClickListener {

    private lateinit var currencyListAdapter: CurrencyListAdapter
    private lateinit var viewModel : MainViewModel
    private lateinit var binding : ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_CryptoSpeculator)

        binding  = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = MainViewModel(this)
        currencyListAdapter = CurrencyListAdapter(ArrayList(), this)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = currencyListAdapter

        viewModel.liveStats.observe(this, {
            currencyListAdapter.update(it)
        })

        viewModel.isLoading.observe(this, {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })

        viewModel.portfolioEntity.observe(this, {
            if(it.isNullOrEmpty()) {
                var starterCash = TransactionHistoryEntity(
                    "Installation Reward",
                    null,
                    "USD",
                    10000.00,
                    0.0,
                    DateTimeFormatter
                        .ofPattern("dd-MM-yyyy HH:mm:ss")
                        .withZone(ZoneId.of("Europe/Paris"))
                        .format(Instant.now()).toString()
                )
                viewModel.insertTransaction(starterCash)
                viewModel.insertPortfolioEntity(PortfolioEntity("dollar", 10000.00))
            } else {
                val amount = String.format("%.2f", it[0].amount)
                binding.header.text = "Cash \$$amount"
            }
        })

        binding.headerContainer.setOnClickListener {
            val intent = Intent(this, PortfolioActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onItemClick(id: String, name: String, symbol: String, price: Double) {
        val intent = Intent(this, CurrencyActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("name", name)
        intent.putExtra("symbol", symbol)
        intent.putExtra("price", price)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

}