package com.praktikum.abstreetfood_management.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.praktikum.abstreetfood_management.databinding.ItemRiwayatBinding
import com.praktikum.abstreetfood_management.domain.model.Transaction
import com.praktikum.abstreetfood_management.domain.model.TransactionItem // Model yang digunakan
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(private val onClick: (Transaction) -> Unit) :
    ListAdapter<Transaction, HistoryAdapter.ViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRiwayatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemRiwayatBinding,
        val onClick: (Transaction) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Transaction) {

//            // Format Mata Uang Indonesia
//            val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
//
//            // 1. Info Produk
//            binding.tvProductName.text = item.productItemId // Asumsi productItemId berisi nama/ID sementara
//            // Perlu lookup nama produk yang sebenarnya jika productItemId hanya berisi ID unik
//
//            // 2. Info Status/Varian
//            binding.tvRiwayatStatus.text = if (item.isExtraRice) "Ekstra Nasi" else "Normal"
//
//            // 3. Status Selesai
//            // binding.tvSelesai.text = itemView.context.getString(R.string.selesai) // Asumsi ini statis atau perlu data tambahan
//
//            // 4. Info Kuantitas dan Harga
//            binding.tvRiwayatJumlah.text = "Jumlah ${item.quantity}x"
//            binding.tvRiwayatPrice.text = formatRupiah.format(item.itemPrice * item.quantity)
//
//            // 5. Info Waktu (TIDAK ADA di TransactionItem Model, Anda butuh Transaction Model)
//            // binding.tvRiwayatTime.text = // Anda harus mendapatkan waktu dari Transaction Entity/Model


            // Format Mata Uang Indonesia
//            val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
                minimumFractionDigits = 0
            }
            // Format Waktu
            val formatTime = SimpleDateFormat("HH:mm", Locale("in", "ID"))
            val formatDate = SimpleDateFormat("dd MMM yyyy", Locale("in", "ID"))


            // 1. Info Produk/Header (Ganti dengan ID Transaksi)
            binding.tvTransactionId.text = "Nota: ${item.id.take(8).uppercase(Locale.ROOT)}"
            // 💡 Catatan: productItemId diganti dengan ID Transaksi

            // 2. Status Sinkronisasi
//            binding.tvVarian.text = "Nasi ${item.id}"
            binding.tvVarian.text = "Tersinkron"
//            val isSynced = try { (item as com.praktikum.abstreetfood_management.data.local.entity.TransactionEntity).isSynced } catch (e: Exception) { true }
//            binding.tvVarian.text = if (isSynced) "Tersinkron" else "Pending Sync"

            // 3. Tanggal (Menggunakan tvSelesai untuk menampilkan tanggal)
            val transactionDate = Date(item.transactionTime)
            binding.tvSelesai.text = formatDate.format(transactionDate) // Tampilkan tanggal penuh

            // 4. Grand Total
            // 💡 Menggunakan GrandTotal dari model Transaction (Header)
            binding.tvGrandTotal.text = formatRupiah.format(item.grandTotal)

            // 5. Waktu (Jam)
            binding.tvRiwayatTime.text = formatTime.format(transactionDate)

            // 💡 Catatan: tvRiwayatJumlah (Jumlah item) - Akan sulit diisi tanpa Join ke TransactionItemEntity.
            // Biarkan kosong/gunakan data statis sampai Anda implementasi query COUNT.
            binding.tvRiwayatJumlah.text = "Total Item: N/A"
            // Set listener untuk item detail
            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }
}