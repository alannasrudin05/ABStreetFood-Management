package com.praktikum.abstreetfood_management.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.praktikum.abstreetfood_management.databinding.ItemReceiptDetailBinding
import com.praktikum.abstreetfood_management.domain.model.NewTransactionItem
import java.text.NumberFormat
import java.util.Locale


class ReceiptItemAdapter : ListAdapter<NewTransactionItem, ReceiptItemAdapter.ViewHolder>(ReceiptItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReceiptDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemReceiptDetailBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NewTransactionItem) {
            val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply { minimumFractionDigits = 0 }

            // Nama Produk & Varian
            binding.tvItemName.text = "${item.productName} (${item.variantName})"

            // Detail Harga Satuan & Kuantitas
            binding.tvItemQuantityPrice.text =
                "${item.quantity} x ${formatRupiah.format(item.itemPrice)}"

            // Total Harga Baris Item (Qty * Price)
            val totalItemPrice = item.quantity * item.itemPrice
            binding.tvItemTotalPrice.text = formatRupiah.format(totalItemPrice)
        }
    }
}
// Anda juga perlu membuat ReceiptItemDiffCallback