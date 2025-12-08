package com.praktikum.abstreetfood_management.data.adapter

import androidx.recyclerview.widget.DiffUtil
import com.praktikum.abstreetfood_management.domain.model.Transaction // Menggunakan Model TransactionItem
import com.praktikum.abstreetfood_management.domain.model.TransactionItem

class HistoryDiffCallback : DiffUtil.ItemCallback<Transaction>() {
    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        // ID unik untuk item transaksi
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        // Membandingkan seluruh konten
        return oldItem == newItem
    }
}