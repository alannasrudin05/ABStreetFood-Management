//package com.praktikum.abstreetfood_management
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.praktikum.abstreetfood_management.RiwayatAdapter.RiwayatViewHolder
//
//class RiwayatAdapter(private val context: Context) : RecyclerView.Adapter<RiwayatViewHolder>() {
//    private var riwayatList: List<Riwayat>
//
//    init {
//        this.riwayatList = ArrayList()
//    }
//
//    fun setRiwayatList(riwayatList: List<Riwayat>) {
//        this.riwayatList = riwayatList
//        notifyDataSetChanged()
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiwayatViewHolder {
//        val view = LayoutInflater.from(context).inflate(R.layout.item_riwayat, parent, false)
//        return RiwayatViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: RiwayatViewHolder, position: Int) {
//        val riwayat = riwayatList[position]
//        holder.bind(riwayat)
//    }
//
//    override fun getItemCount(): Int {
//        return riwayatList.size
//    }
//
//    class RiwayatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val ivRiwayatImage: ImageView =
//            itemView.findViewById(R.id.ivRiwayatImage)
//        private val tvRiwayatName: TextView =
//            itemView.findViewById(R.id.tvIdTransaksi)
//        private val tvRiwayatStatus: TextView =
//            itemView.findViewById(R.id.tvRiwayatStatus)
//        private val tvSelesai: TextView =
//            itemView.findViewById(R.id.tvSelesai)
//        private val tvRiwayatJumlah: TextView =
//            itemView.findViewById(R.id.tvRiwayatJumlah)
//        private val tvRiwayatPrice: TextView =
//            itemView.findViewById(R.id.tvGrandTotal)
//        private val tvRiwayatTime: TextView =
//            itemView.findViewById(R.id.tvRiwayatTime)
//
//        fun bind(riwayat: Riwayat) {
//            tvRiwayatName.text = riwayat.name
//            tvRiwayatStatus.text = riwayat.status
//            tvRiwayatJumlah.text = riwayat.jumlah
//            tvRiwayatPrice.text = riwayat.price
//            tvRiwayatTime.text = riwayat.time
//
//            // Set image if available
//            if (riwayat.imageResId != 0) {
//                ivRiwayatImage.setImageResource(riwayat.imageResId)
//            } else {
//                // Set default pink background (already set in XML)
//                ivRiwayatImage.setImageDrawable(null)
//            }
//        }
//    }
//}