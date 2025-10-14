package com.praktikum.abstreetfood_management

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.praktikum.abstreetfood_management.ProductAdapter.ProductViewHolder

class ProductAdapter(private val context: Context) : RecyclerView.Adapter<ProductViewHolder>() {
    private var products: List<Product>

    init {
        this.products = ArrayList()
    }

    fun setProducts(products: List<Product>) {
        this.products = products
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivProductImage: ImageView =
            itemView.findViewById(R.id.ivProductImage)
        private val tvProductName: TextView =
            itemView.findViewById(R.id.tvProductName)
        private val tvProductDate: TextView =
            itemView.findViewById(R.id.tvProductDate)
        private val tvProductQuantity: TextView =
            itemView.findViewById(R.id.tvProductQuantity)

        fun bind(product: Product) {
            tvProductName.text = product.name
            tvProductDate.text = product.date
            tvProductQuantity.text = product.quantity

            // Set image if available
            if (product.imageResId != 0) {
                ivProductImage.setImageResource(product.imageResId)
            } else {
                // Set default pink background (already set in XML)
                ivProductImage.setImageDrawable(null)
            }
        }
    }
}