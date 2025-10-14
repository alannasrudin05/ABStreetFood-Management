package com.praktikum.abstreetfood_management

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.praktikum.abstreetfood_management.NotifikasiAdapter.NotifikasiViewHolder

class NotifikasiAdapter(private val context: Context) :
    RecyclerView.Adapter<NotifikasiViewHolder>() {
    private var notifikasiList: List<Notifikasi>

    init {
        this.notifikasiList = ArrayList()
    }

    fun setNotifikasiList(notifikasiList: List<Notifikasi>) {
        this.notifikasiList = notifikasiList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifikasiViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_notifikasi, parent, false)
        return NotifikasiViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotifikasiViewHolder, position: Int) {
        val notifikasi = notifikasiList[position]
        holder.bind(notifikasi)
    }

    override fun getItemCount(): Int {
        return notifikasiList.size
    }

    class NotifikasiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNotifikasiName: TextView =
            itemView.findViewById(R.id.tvNotifikasiName)
        private val tvNotifikasiMessage: TextView =
            itemView.findViewById(R.id.tvNotifikasiMessage)

        fun bind(notifikasi: Notifikasi) {
            tvNotifikasiName.text = notifikasi.itemName
            tvNotifikasiMessage.text = notifikasi.message
        }
    }
}