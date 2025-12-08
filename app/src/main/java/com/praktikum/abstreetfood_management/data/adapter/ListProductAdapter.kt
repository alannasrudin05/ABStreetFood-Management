package com.praktikum.abstreetfood_management.data.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.praktikum.abstreetfood_management.R
import com.praktikum.abstreetfood_management.databinding.ItemProductBinding
import com.praktikum.abstreetfood_management.domain.model.ProductItem
import java.text.NumberFormat
import java.util.Locale
import coil.load

class ListProductAdapter(private val onClick: (ProductItem) -> Unit) :
    ListAdapter<ProductItem, ListProductAdapter.ViewHolder>(ListProductDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

//    class ViewHolder(
//        private val binding: ItemProductBinding,
//        val onClick: (ProductItem) -> Unit
//    ) : RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(item: ProductItem) {
//
//            val data = listOf(
//                Triple("Ayam Bakar", "27/11/2025", 10),
//                Triple("Ayam Goreng", "27/11/2025", 8),
//            )
//
//
//            // Ambil elemen pertama dari list
//            val firstItem = data.firstOrNull()
//
//            if (firstItem != null) {
//                // Destructuring Declaration untuk memudahkan akses
//                val (name, date, quantity) = firstItem
//
//                // Asign nilai ke TextView
//                binding.tvProductName.text = name
//                binding.tvProductDate.text = date
//                binding.tvProductQuantity.text = quantity.toString() // Wajib konversi Int/Double ke String
//            } else {
//                // Penanganan jika list kosong
//                binding.tvProductName.text = "Data Kosong"
//                binding.tvProductDate.text = "-"
//                binding.tvProductQuantity.text = "0"
//            }
//
////            binding.ivProductImage
//
////            binding.tvItemName.text = item.itemName // Ganti nama TextView sesuai layout baru
////            binding.tvCurrentStock.text = "Stok Tersisa: ${item.currentStock} ${item.unit}"
////            binding.tvStatus.text = if (item.isCritical) "Stok Kritis!" else "Normal"
////            binding.tvStatus.setTextColor(
////                itemView.context.getColor(if (item.isCritical) R.color.red_negative else R.color.green_positive)
////            )
//
//            // Set listener untuk item detail
//            binding.root.setOnClickListener {
//                onClick(item)
//            }
//        }
//    }
    class ViewHolder(
        private val binding: ItemProductBinding,
        val onClick: (ProductItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProductItem) {

            val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))


            binding.tvProductName.text = item.name
            binding.tvVariant.text = item.variantType
            binding.tvSellingPrice.text = formatRupiah.format(item.sellingPrice)

//            binding.ivProductImage.load(item.productImage) {
//                // Pengaturan tambahan
//                placeholder(R.drawable.ayam_goreng) // Gambar saat memuat
//                error(R.drawable.ayam_bakar) // Gambar jika gagal
//            }
            // Set listener untuk item detail
            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }
}

