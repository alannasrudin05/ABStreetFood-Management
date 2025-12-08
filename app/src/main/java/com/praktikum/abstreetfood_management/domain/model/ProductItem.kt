package com.praktikum.abstreetfood_management.domain.model

data class ProductItem(
    val id: String,
    val productId: String, // Ke Product (Ayam Bakar)
//    val productImage: String?, // tipe data apa yang baik?
    val name: String, // Nama varian (Dada/Paha)
    val sellingPrice: Double,
    val variantType: String
)