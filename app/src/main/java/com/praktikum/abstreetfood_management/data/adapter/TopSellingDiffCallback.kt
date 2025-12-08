package com.praktikum.abstreetfood_management.data.adapter

import androidx.recyclerview.widget.DiffUtil
import com.praktikum.abstreetfood_management.domain.model.TopSellingProduct


class TopSellingDiffCallback : DiffUtil.ItemCallback<TopSellingProduct>() {
    override fun areItemsTheSame(oldItem: TopSellingProduct, newItem: TopSellingProduct): Boolean {
//        return oldItem.productName == newItem.productName
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: TopSellingProduct, newItem: TopSellingProduct): Boolean {
        return oldItem == newItem
    }
}