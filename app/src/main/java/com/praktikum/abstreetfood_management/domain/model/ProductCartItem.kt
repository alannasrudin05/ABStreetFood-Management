package com.praktikum.abstreetfood_management.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ProductCartItem(
    // ID Unik Sesi: Digunakan untuk mengidentifikasi item ini selama berada di keranjang
    val cartItemId: String = UUID.randomUUID().toString(),

    // --- Data Produk Utama ---
    val productItemId: String,
    val productName: String,
    val variantName: String,

    // --- Data Harga & Kuantitas ---
    val pricePerUnit: Double,
    var quantity: Int,
    val isExtraRice: Boolean = false
) : Parcelable {

    /**
     * Properti helper untuk mendapatkan subtotal item ini
     */
    val subtotal: Double
        get() = pricePerUnit * quantity
}