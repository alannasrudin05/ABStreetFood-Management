package com.praktikum.abstreetfood_management.ui.dialog

import com.praktikum.abstreetfood_management.domain.model.NewTransactionItem
import com.praktikum.abstreetfood_management.domain.model.ProductCartItem

/**
 * Interface untuk mengirim kembali item yang sudah dikalkulasikan dari Dialog ke Fragment Transaksi
 */
interface VariantSelectionListener {
    fun onVariantItemAdded(item: NewTransactionItem)
}