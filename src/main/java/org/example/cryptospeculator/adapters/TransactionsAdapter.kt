package org.example.cryptospeculator.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.example.cryptospeculator.R
import org.example.cryptospeculator.databinding.ItemTransactionViewBinding
import org.example.cryptospeculator.datastorage.TransactionHistoryEntity
import org.example.cryptospeculator.models.CurrencyItem

class TransactionsAdapter(private var list: List<TransactionHistoryEntity>) :
    RecyclerView.Adapter<TransactionsAdapter.TransactionsViewHolder>() {

    class TransactionsViewHolder(private val binding: ItemTransactionViewBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(transaction : TransactionHistoryEntity ) {
            Glide.with(binding.image)
                .load("https://static.coincap.io/assets/icons/${transaction.currencySymbol.toLowerCase()}@2x.png")
                .error(R.drawable.ic_dollar)
                .into(binding.image)
            binding.transactionType.text = transaction.buySell.capitalize()
            if(transaction.buySell == "bought") binding.transactionType.setTextColor(Color.BLUE) else binding.transactionType.setTextColor(Color.parseColor("#048700"))
            if(transaction.buySell == "Installation Reward") binding.transactionInfo.text = "Reward: \$${transaction.amount}" else binding.transactionInfo.text = "${setCorrectNumberOfDecimals(transaction.amount)} ${transaction.currencySymbol} for \$${setCorrectNumberOfDecimals(transaction.price)}"
            binding.date.text = transaction.timestamp
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsViewHolder {
        return TransactionsViewHolder(ItemTransactionViewBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: TransactionsViewHolder, position: Int) {
        holder.bind(list[(list.size-1)-position])
    }

    override fun getItemCount(): Int = list.size

    fun update(newList: List<TransactionHistoryEntity>) {
        list = newList
        notifyDataSetChanged()
    }
}
