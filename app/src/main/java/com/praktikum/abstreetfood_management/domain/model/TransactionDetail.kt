package com.praktikum.abstreetfood_management.domain.model

/**
 * Model Domain yang menggabungkan Header Transaksi dan Detail Item-item yang dibeli.
 * Digunakan untuk menampilkan Nota/Struk secara lengkap.
 *
 * @param header Objek Transaction (Header)
 * @param items Daftar item yang dibeli (Detail)
 */
data class TransactionDetail(
    val header: Transaction,
    val items: List<NewTransactionItem>
)