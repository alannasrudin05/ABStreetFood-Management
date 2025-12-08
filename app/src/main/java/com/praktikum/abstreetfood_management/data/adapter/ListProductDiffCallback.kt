package com.praktikum.abstreetfood_management.data.adapter

import androidx.recyclerview.widget.DiffUtil
import com.praktikum.abstreetfood_management.domain.model.ProductItem

class ListProductDiffCallback : DiffUtil.ItemCallback<ProductItem>() {
    override fun areItemsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean {
        return oldItem == newItem
    }
}