package com.praktikum.abstreetfood_management.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.praktikum.abstreetfood_management.R
import com.praktikum.abstreetfood_management.databinding.ItemTopSellingBinding // <<< Asumsi Anda membuat layout ini
import com.praktikum.abstreetfood_management.domain.model.TopSellingProduct
import coil.load
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TopSellingAdapter(private val onClick: (TopSellingProduct) -> Unit) :
    ListAdapter<TopSellingProduct, TopSellingAdapter.ViewHolder>(TopSellingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTopSellingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemTopSellingBinding,
        val onClick: (TopSellingProduct) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: TopSellingProduct) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("in", "ID")) // Format tanggal

            binding.tvProductName.text = product.name
            binding.tvSalesQuantity.text = "${product.totalQuantitySold}"
//            binding.tvSalesDate.text = "${product.transactionTime}"
//            binding.tvSalesDate.text = dateFormat.format(Date(product.transactionTime))

            //product.productItem.productImage sepertinya untuk penanganan ini harus tepat, mungkin nanti productItem itu id dari Product Item yang di dalamnya
//            binding.ivProductImage.load(product.productItem.productImage) {
//                // Pengaturan tambahan
//                placeholder(R.drawable.ayam_goreng) // Gambar saat memuat
//                error(R.drawable.ayam_bakar) // Gambar jika gagal
//            }
            // Set listener untuk item detail
            binding.root.setOnClickListener {
                onClick(product)
            }
        }
    }
}
