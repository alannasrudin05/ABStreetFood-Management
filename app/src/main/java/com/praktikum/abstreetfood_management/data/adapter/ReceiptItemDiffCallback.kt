package com.praktikum.abstreetfood_management.data.adapter

import androidx.recyclerview.widget.DiffUtil
import com.praktikum.abstreetfood_management.domain.model.NewTransactionItem

class ReceiptItemDiffCallback : DiffUtil.ItemCallback<NewTransactionItem>() {
    override fun areItemsTheSame(oldItem: NewTransactionItem, newItem: NewTransactionItem): Boolean {
        return oldItem.productItemId == newItem.productItemId
    }

    override fun areContentsTheSame(oldItem: NewTransactionItem, newItem: NewTransactionItem): Boolean {
        return oldItem == newItem
    }
}