package com.praktikum.abstreetfood_management.data.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.praktikum.abstreetfood_management.databinding.ItemTransaksiCartBinding
import com.praktikum.abstreetfood_management.domain.model.NewTransactionItem
import java.text.NumberFormat
import java.util.Locale

// Callback dan Enum CartAction tetap sama
typealias CartActionListener = (item: NewTransactionItem, action: CartAction) -> Unit
enum class CartAction { ADD, REMOVE, DELETE }

class CartAdapter(private val listener: CartActionListener) :
    ListAdapter<NewTransactionItem, CartAdapter.ViewHolder>(CartDiffCallback()) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Pastikan ItemTransaksiCartBinding yang digunakan
        val binding = ItemTransaksiCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemTransaksiCartBinding,
        private val listener: CartActionListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NewTransactionItem) {

            val TAG = "TRANSAKSI_LOG"
            val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            val subtotal = item.itemPrice * item.quantity

            // --- BINDING KE ID BARU ---

            // 1. Nama Produk  // 2. Varian
//            binding.tvProductName.text = "Produk ID: ${item.productItemId.take(6)}" // Harus diganti dengan lookup nama produk
            binding.tvProductName.text = item.productName // Harus diganti dengan lookup nama produk
//            binding.tvVarian.text = if (item.isExtraRice) "Ekstra Nasi" else "Varian Standar"
            binding.tvVarian.text = item.variantName

            // 3. Harga Total Item (Harga Satuan * Kuantitas)
//            binding.tvPrice.text = formatRupiah.format(item.itemPrice * item.quantity)
            binding.tvPrice.text = formatRupiah.format(subtotal)
            Log.d(TAG, " Harga item : ${subtotal}")
            // 4. Kuantitas
            binding.quantityProduct.text = item.quantity.toString()

            // Listener untuk menambah kuantitas
            binding.linearPlus.setOnClickListener { // Klik di container linearPlus
                listener.invoke(item, CartAction.ADD)
            }
            // Tambahkan listener langsung ke ImageView jika diperlukan presisi
            // binding.ivPlus.setOnClickListener { listener.invoke(item, CartAction.ADD) }

            // Listener untuk mengurangi kuantitas
            binding.linearMinus.setOnClickListener { // Klik di container linearMinus
                listener.invoke(item, CartAction.REMOVE)
            }
            // Tambahkan listener langsung ke ImageView jika diperlukan presisi
            // binding.ivMinus.setOnClickListener { listener.invoke(item, CartAction.REMOVE) }

            // TODO: Tambahkan logika untuk binding.ivRiwayatImage (gambar produk)
        }
    }
}