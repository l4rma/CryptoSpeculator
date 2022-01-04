package org.example.cryptospeculator.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.example.cryptospeculator.R.drawable.*
import org.example.cryptospeculator.databinding.ItemPortfolioEntityViewBinding
import org.example.cryptospeculator.models.PortfolioItem


class PortfolioAdapter(private var list: List<PortfolioItem>) :
    RecyclerView.Adapter<PortfolioAdapter.PortfolioViewHolder>() {

    class PortfolioViewHolder(private val binding: ItemPortfolioEntityViewBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: PortfolioItem) {
            if (item.id == "dollar") {
                binding.amount.text = "${String.format("%.2f", item.amount)} USD"
                binding.value.text = ""
            } else {
                binding.amount.text = "${setCorrectNumberOfDecimals(item.amount)} ${item.symbol}"
                binding.value.text = "Value: \$${setCorrectNumberOfDecimals(item.value)}"
            }
            Glide.with(binding.image)
                .load("https://static.coincap.io/assets/icons/${item.symbol?.toLowerCase()}@2x.png")
                .error(ic_dollar)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        return PortfolioViewHolder(ItemPortfolioEntityViewBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun update(newList: List<PortfolioItem>) {
        list = newList
        notifyDataSetChanged()
    }
}
