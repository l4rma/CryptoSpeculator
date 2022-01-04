package org.example.cryptospeculator.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.example.cryptospeculator.R
import org.example.cryptospeculator.databinding.ItemCurrencyViewBinding
import org.example.cryptospeculator.models.CurrencyItem

class CurrencyListAdapter(private var list: List<CurrencyItem>, private val listener: OnItemClickListener) : RecyclerView.Adapter<CurrencyListAdapter.CurrencyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int): CurrencyViewHolder {

        return CurrencyViewHolder(ItemCurrencyViewBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class CurrencyViewHolder(private val binding: ItemCurrencyViewBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener{

        fun bind(currency : CurrencyItem) {
            Glide.with(binding.image)
                .load("https://static.coincap.io/assets/icons/${currency.symbol.toLowerCase()}@2x.png")
                .error(R.drawable.ic_dollar)
                .into(binding.image)
            binding.name.text = currency.name
            binding.symbol.text = currency.symbol
            binding.price.text = "\$" + String.format("%.2f", currency.priceUsd)
            binding.changePro.text = String.format("%.2f", currency.changePercent24Hr) + "%"
            if (currency.changePercent24Hr>0) binding.changePro.setTextColor(Color.parseColor("#048700")) else binding.changePro.setTextColor(Color.RED)
        }

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            val id = list[position].id
            val name = list[position].name
            val symbol = list[position].symbol
            val price = list[position].priceUsd
            listener.onItemClick(id, name, symbol, price)
        }
        init {
            itemView.setOnClickListener(this)
        }
    }

    fun update(newList: List<CurrencyItem>) {
        list = newList
        notifyDataSetChanged()
    }
    interface OnItemClickListener {
        fun onItemClick(id: String, name: String, symbol: String, price: Double)
    }
}