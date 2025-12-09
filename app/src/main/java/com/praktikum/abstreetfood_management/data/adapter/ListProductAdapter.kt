// ListProductAdapter.kt

package com.praktikum.abstreetfood_management.data.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.praktikum.abstreetfood_management.R
import com.praktikum.abstreetfood_management.databinding.ItemProductBinding
import com.praktikum.abstreetfood_management.domain.model.ProductItem // <-- Masih diimpor untuk kompatibilitas
import com.praktikum.abstreetfood_management.domain.model.TopSellingProduct // <-- MODEL BARU
import java.text.NumberFormat
import java.util.Locale
import coil.load

// GANTI: Ubah tipe generic ListAdapter dan tipe lambda onClick menjadi TopSellingProduct
class ListProductAdapter(private val onClick: (TopSellingProduct) -> Unit) :
    ListAdapter<TopSellingProduct, ListProductAdapter.ViewHolder>(TopSellingProductDiffCallback()) { // Ganti DiffCallback

    // Ganti DiffCallback baru (harus Anda buat secara eksternal)
    // Jika Anda belum membuat TopSellingProductDiffCallback, ganti nama DiffCallback sementara
    // class ListProductDiffCallback : DiffUtil.ItemCallback<TopSellingProduct>() { /* ... */ }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // GANTI: Ubah tipe item di ViewHolder dan fungsi bind menjadi TopSellingProduct
    class ViewHolder(
        private val binding: ItemProductBinding,
        val onClick: (TopSellingProduct) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        // Pastikan Anda memodifikasi item_product.xml untuk menampung data TopSales
        // Asumsi: Anda akan memodifikasi layout untuk menyajikan data baru
        fun bind(item: TopSellingProduct) {

            val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

            // --- DATA DARI TopSellingProduct ---

            // Asumsi: item_product.xml dimodifikasi untuk menampilkan data Top Sales:
            binding.tvProductName.text = item.name // Nama Produk

            // CATATAN PENTING:
            // Karena ini Top Selling Products, tampilkan data agregasi BUKAN data ProductItem

            // Asumsi: Anda memiliki TextView baru di ItemProductBinding untuk QTY dan REVENUE
            // binding.tvTotalQuantity.text = "Terjual: ${item.totalQuantitySold} item"
            // binding.tvTotalRevenue.text = formatRupiah.format(item.totalRevenue)

            // --- Menggunakan binding yang sudah ada sebagai placeholder ---
            // Tampilkan kuantitas sebagai ganti Varian
            binding.tvVariant.text = "Terjual: ${item.totalQuantitySold} item"

            // Tampilkan Revenue sebagai ganti SellingPrice
            binding.tvSellingPrice.text = formatRupiah.format(item.totalRevenue)

            // Set listener untuk item detail
            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }
}

// Catatan: Anda perlu membuat TopSellingProductDiffCallback.kt (sebagai pengganti ListProductDiffCallback)
class TopSellingProductDiffCallback : DiffUtil.ItemCallback<TopSellingProduct>() {
    override fun areItemsTheSame(oldItem: TopSellingProduct, newItem: TopSellingProduct): Boolean {
        // Asumsi nama produk unik untuk Top Sales
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: TopSellingProduct, newItem: TopSellingProduct): Boolean {
        return oldItem == newItem
    }
}


//package com.praktikum.abstreetfood_management.data.adapter
//
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.praktikum.abstreetfood_management.R
//import com.praktikum.abstreetfood_management.databinding.ItemProductBinding
//import com.praktikum.abstreetfood_management.domain.model.ProductItem
//import java.text.NumberFormat
//import java.util.Locale
//import coil.load
//
//class ListProductAdapter(private val onClick: (ProductItem) -> Unit) :
//    ListAdapter<ProductItem, ListProductAdapter.ViewHolder>(ListProductDiffCallback()) {
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(binding, onClick)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(getItem(position))
//    }
//
////    class ViewHolder(
////        private val binding: ItemProductBinding,
////        val onClick: (ProductItem) -> Unit
////    ) : RecyclerView.ViewHolder(binding.root) {
////
////        fun bind(item: ProductItem) {
////
////            val data = listOf(
////                Triple("Ayam Bakar", "27/11/2025", 10),
////                Triple("Ayam Goreng", "27/11/2025", 8),
////            )
////
////
////            // Ambil elemen pertama dari list
////            val firstItem = data.firstOrNull()
////
////            if (firstItem != null) {
////                // Destructuring Declaration untuk memudahkan akses
////                val (name, date, quantity) = firstItem
////
////                // Asign nilai ke TextView
////                binding.tvProductName.text = name
////                binding.tvProductDate.text = date
////                binding.tvProductQuantity.text = quantity.toString() // Wajib konversi Int/Double ke String
////            } else {
////                // Penanganan jika list kosong
////                binding.tvProductName.text = "Data Kosong"
////                binding.tvProductDate.text = "-"
////                binding.tvProductQuantity.text = "0"
////            }
////
//////            binding.ivProductImage
////
//////            binding.tvItemName.text = item.itemName // Ganti nama TextView sesuai layout baru
//////            binding.tvCurrentStock.text = "Stok Tersisa: ${item.currentStock} ${item.unit}"
//////            binding.tvStatus.text = if (item.isCritical) "Stok Kritis!" else "Normal"
//////            binding.tvStatus.setTextColor(
//////                itemView.context.getColor(if (item.isCritical) R.color.red_negative else R.color.green_positive)
//////            )
////
////            // Set listener untuk item detail
////            binding.root.setOnClickListener {
////                onClick(item)
////            }
////        }
////    }
//    class ViewHolder(
//        private val binding: ItemProductBinding,
//        val onClick: (ProductItem) -> Unit
//    ) : RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(item: ProductItem) {
//
//            val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
//
//
//            binding.tvProductName.text = item.name
//            binding.tvVariant.text = item.variantType
//            binding.tvSellingPrice.text = formatRupiah.format(item.sellingPrice)
//
////            binding.ivProductImage.load(item.productImage) {
////                // Pengaturan tambahan
////                placeholder(R.drawable.ayam_goreng) // Gambar saat memuat
////                error(R.drawable.ayam_bakar) // Gambar jika gagal
////            }
//            // Set listener untuk item detail
//            binding.root.setOnClickListener {
//                onClick(item)
//            }
//        }
//    }
//}
//
