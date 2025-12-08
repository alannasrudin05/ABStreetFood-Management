package com.praktikum.abstreetfood_management.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.praktikum.abstreetfood_management.databinding.ItemReportDetailBinding // Asumsi ViewBinding
import com.praktikum.abstreetfood_management.domain.model.SaleReportItem
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportDetailAdapter : ListAdapter<SaleReportItem, ReportDetailAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemReportDetailBinding) : RecyclerView.ViewHolder(binding.root) {

        // Helper function untuk format Rupiah
        private val rupiahFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("in", "ID"))

        fun bind(item: SaleReportItem) {
            val totalRevenueFormatted = rupiahFormat.format(item.totalItemRevenue)
            val itemPriceFormatted = rupiahFormat.format(item.itemPrice)
            val timeFormatted = dateFormat.format(Date(item.transactionTime))

            binding.tvTransactionId.text = item.transactionId
            binding.tvTransactionTime.text = timeFormatted
            binding.tvProductNameAndQty.text = "${item.productName} (x${item.quantity})"
            binding.tvItemRevenue.text = totalRevenueFormatted
            binding.tvItemPrice.text = "$itemPriceFormatted / item"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReportDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<SaleReportItem>() {
        override fun areItemsTheSame(oldItem: SaleReportItem, newItem: SaleReportItem): Boolean {
            // Unik untuk setiap baris laporan, gunakan gabungan ID dan Waktu
            return oldItem.transactionId == newItem.transactionId && oldItem.transactionTime == newItem.transactionTime
        }

        override fun areContentsTheSame(oldItem: SaleReportItem, newItem: SaleReportItem): Boolean {
            return oldItem == newItem
        }
    }
}