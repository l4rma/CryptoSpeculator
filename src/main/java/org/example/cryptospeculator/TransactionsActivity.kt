package org.example.cryptospeculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import org.example.cryptospeculator.adapters.TransactionsAdapter
import org.example.cryptospeculator.databinding.ActivityTransactionsBinding
import org.example.cryptospeculator.viewmodels.TransactionsViewModel


class TransactionsActivity : AppCompatActivity() {

    private lateinit var viewModel: TransactionsViewModel
    private lateinit var binding: ActivityTransactionsBinding
    private lateinit var listAdapter : TransactionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_CryptoSpeculator)
        binding = ActivityTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = TransactionsViewModel()

        listAdapter = TransactionsAdapter(ArrayList())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = listAdapter

        viewModel.isLoading.observe(this, {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })

        viewModel.liveStats.observe(this, { newList->
            listAdapter.update(newList)
        })

        binding.headerContainer.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

}