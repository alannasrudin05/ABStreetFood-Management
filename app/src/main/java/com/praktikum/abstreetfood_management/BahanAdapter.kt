package com.praktikum.abstreetfood_management

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BahanAdapter(private val context: Context) :
    RecyclerView.Adapter<BahanAdapter.BahanViewHolder>() {

    private var bahanList: List<Bahan> = ArrayList()
    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onEditClick(bahan: Bahan, position: Int)
    }

    fun setBahanList(bahanList: List<Bahan>) {
        this.bahanList = bahanList
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BahanViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_bahan, parent, false)
        return BahanViewHolder(view)
    }

    override fun onBindViewHolder(holder: BahanViewHolder, position: Int) {
        val bahan = bahanList[position]
        holder.bind(bahan, position)
    }

    override fun getItemCount(): Int {
        return bahanList.size
    }

    inner class BahanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivBahanImage: ImageView = itemView.findViewById(R.id.ivBahanImage)
        private val tvBahanName: TextView = itemView.findViewById(R.id.tvBahanName)
        private val tvBahanQuantity: TextView = itemView.findViewById(R.id.tvBahanQuantity)
        private val tvStatusBadge: TextView = itemView.findViewById(R.id.tvStatusBadge)
        private val viewStatusDot: View = itemView.findViewById(R.id.viewStatusDot)
        private val ivEdit: ImageView = itemView.findViewById(R.id.ivEdit)

        fun bind(bahan: Bahan, position: Int) {
            tvBahanName.text = bahan.name
            tvBahanQuantity.text = bahan.quantity

            // Set status based on stock level
            if (bahan.isLowStock) {
                tvStatusBadge.setText(R.string.stok_rendah)
                tvStatusBadge.setBackgroundResource(R.drawable.badge_red)
                viewStatusDot.setBackgroundResource(R.drawable.circle_red)
            } else {
                tvStatusBadge.setText(R.string.stok_aman)
                tvStatusBadge.setBackgroundResource(R.drawable.badge_green)
                viewStatusDot.setBackgroundResource(R.drawable.circle_green)
            }

            // Set image if available
            if (bahan.imageResId != 0) {
                ivBahanImage.setImageResource(bahan.imageResId)
            } else {
                ivBahanImage.setImageDrawable(null)
            }

            // Edit button click
            ivEdit.setOnClickListener {
                onItemClickListener?.onEditClick(bahan, position)
            }
        }
    }
}