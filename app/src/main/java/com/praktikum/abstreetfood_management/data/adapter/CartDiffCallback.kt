package com.praktikum.abstreetfood_management.data.adapter

import androidx.recyclerview.widget.DiffUtil
import com.praktikum.abstreetfood_management.domain.model.NewTransactionItem

class CartDiffCallback : DiffUtil.ItemCallback<NewTransactionItem>() {
    override fun areItemsTheSame(oldItem: NewTransactionItem, newItem: NewTransactionItem): Boolean {
        // Karena NewTransactionItem tidak punya ID, kita anggap item sama jika ProductItemId dan Varian sama
//        return oldItem.productItemId == newItem.productItemId
        return oldItem.productItemId == newItem.productItemId && oldItem.itemPrice == newItem.itemPrice
    }

    override fun areContentsTheSame(oldItem: NewTransactionItem, newItem: NewTransactionItem): Boolean {
        // Membandingkan semua properti (termasuk kuantitas dan harga)
        return oldItem == newItem
    }
}