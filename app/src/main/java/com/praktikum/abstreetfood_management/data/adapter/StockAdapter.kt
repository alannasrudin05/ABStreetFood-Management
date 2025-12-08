//package com.praktikum.abstreetfood_management.data.adapter
//
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.praktikum.abstreetfood_management.R
//import com.praktikum.abstreetfood_management.domain.model.StockItem
//
//class StockAdapter(private val onClick: (StockItem) -> Unit) :
//    ListAdapter<StockItem, StockAdapter.ViewHolder>(StockDiffCallback()) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = ItemStockItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(binding, onClick)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(getItem(position))
//    }
//
//    class ViewHolder(
//        private val binding: ItemStockItemBinding,
//        val onClick: (StockItem) -> Unit
//    ) : RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(item: StockItem) {
//            binding.tvItemName.text = item.itemName // Ganti nama TextView sesuai layout baru
//            binding.tvCurrentStock.text = "Stok Tersisa: ${item.currentStock} ${item.unit}"
//            binding.tvStatus.text = if (item.isCritical) "Stok Kritis!" else "Normal"
//            binding.tvStatus.setTextColor(
//                itemView.context.getColor(if (item.isCritical) R.color.red_negative else R.color.green_positive)
//            )
//
//            // Set listener untuk item detail
//            binding.root.setOnClickListener {
//                onClick(item)
//            }
//        }
//    }
//}
//
