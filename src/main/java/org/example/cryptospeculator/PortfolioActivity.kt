package org.example.cryptospeculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.LinearLayoutManager
import org.example.cryptospeculator.adapters.PortfolioAdapter
import org.example.cryptospeculator.adapters.TransactionsAdapter
import org.example.cryptospeculator.databinding.ActivityPortfolioBinding
import org.example.cryptospeculator.databinding.ActivityTransactionsBinding
import org.example.cryptospeculator.viewmodels.PortfolioViewModel
import org.example.cryptospeculator.viewmodels.TransactionsViewModel


class PortfolioActivity : AppCompatActivity() {

    private lateinit var viewModel: PortfolioViewModel
    private lateinit var binding: ActivityPortfolioBinding
    private lateinit var listAdapter : PortfolioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_CryptoSpeculator)
        binding = ActivityPortfolioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = PortfolioViewModel()

        listAdapter = PortfolioAdapter(ArrayList())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = listAdapter
        viewModel.refreshLists()

        viewModel.liveData.observe(this, {
            listAdapter.update(it)
            viewModel.totalPortfolioValue.value = it.sumByDouble { it.value }
            binding.portfolioValue.text = "Total value: \$${String.format("%.2f", viewModel.totalPortfolioValue.value)}"
        })

        viewModel.isLoading.observe(this, {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.INVISIBLE
                viewModel.makeNewPortfolioItemList()
            }

        })

        binding.btnTransactions.setOnClickListener {
            val intent = Intent(this, TransactionsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        binding.headerContainer.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshLists()
    }

}