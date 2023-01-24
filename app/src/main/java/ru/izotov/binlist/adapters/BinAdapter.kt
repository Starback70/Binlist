package ru.izotov.binlist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.izotov.binlist.models.BinModel
import ru.izotov.binlist.R
import ru.izotov.binlist.databinding.ListItemBinding

class BinAdapter(private val listener: Listener) :
    ListAdapter<BinModel, BinAdapter.Holder>(Comparator()) {

    class Holder(view: View, private val listener: Listener) : RecyclerView.ViewHolder(view) {
        private val binding = ListItemBinding.bind(view)
        var itemTemp: BinModel? = null

        init {
            itemView.setOnClickListener {
                itemTemp?.let { it1 -> listener.onClick(it1) }
            }
        }

        fun bind(item: BinModel) = with(binding) {
            itemTemp = item
            liTvNumber.text = item.cardNumber
            liTvCountry.text = item.countryEmoji + " " + item.countryName
            liTvSheme.text = item.scheme
            liTvType.text = item.type
        }
    }

    class Comparator : DiffUtil.ItemCallback<BinModel>() {
        override fun areItemsTheSame(oldItem: BinModel, newItem: BinModel): Boolean {
            return oldItem.cardNumber == newItem.cardNumber
        }

        override fun areContentsTheSame(oldItem: BinModel, newItem: BinModel): Boolean {
            return oldItem.cardNumber == newItem.cardNumber
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return Holder(view, listener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    interface Listener {
        fun onClick(item: BinModel)
    }
}