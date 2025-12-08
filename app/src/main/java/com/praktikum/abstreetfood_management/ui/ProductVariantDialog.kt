//package com.praktikum.abstreetfood_management.ui
//
//import android.content.Context
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.RadioButton
//import android.widget.Toast
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment
//import com.praktikum.abstreetfood_management.databinding.FragmentProductVariantBinding
//import com.praktikum.abstreetfood_management.domain.model.NewTransactionItem
//import com.praktikum.abstreetfood_management.domain.model.ProductCartItem
//import com.praktikum.abstreetfood_management.domain.model.ProductItem
//import com.praktikum.abstreetfood_management.ui.dialog.VariantSelectionListener
//import timber.log.Timber
//import java.text.NumberFormat
//import java.util.Locale
//
//class ProductVariantDialog : BottomSheetDialogFragment() {
//
//    companion object {
//        const val TAG = "ProductVariantDialog"
//        private const val ARG_PRODUCT_ITEM = "product_item"
//
////        fun newInstance(item: ProductCartItem): ProductVariantDialog {
////            val fragment = ProductVariantDialog()
////            val args = Bundle()
////            args.putSerializable(ARG_PRODUCT_ITEM, item)
////            fragment.arguments = args
////            return fragment
////        }
//        fun newInstance(item: ProductItem): ProductVariantDialog {
//            val fragment = ProductVariantDialog()
//            val args = Bundle()
//
//            // KOREKSI: Ganti putSerializable dengan putParcelable
//            args.putParcelable(ARG_PRODUCT_ITEM, item)
//
//            fragment.arguments = args
//            return fragment
//        }
//    }
//
//    private var _binding: FragmentProductVariantBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var productItem: ProductItem
//    private var listener: VariantSelectionListener? = null
//
//    // Override onAttach untuk mendapatkan listener dari Fragment induk
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        try {
//            // Fragment Induk harus mengimplementasikan interface ini
//            listener = targetFragment as? VariantSelectionListener
//        } catch (e: ClassCastException) {
//            throw ClassCastException("$context must implement VariantSelectionListener")
//        }
//    }
//    // Simulasikan Modifiers Varian
//    private val variantModifiers = mapOf(
//        "TANPA_NASI" to 0.0,
//        "NASI_BIASA" to 3000.0,
//        "NASI_DOUBLE" to 5000.0
//    )
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentProductVariantBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Ambil data ProductItem yang dilewatkan
////        productItem = arguments?.getSerializable(ARG_PRODUCT_ITEM) as? ProductItem
////            ?: run { dismiss(); return } // Tutup jika data hilang
//
////        productItem = arguments?.getParcelable<ProductItem>(ARG_PRODUCT_ITEM) // <<< KOREKSI KRITIS
////            ?: run {
//        productItem = arguments?.getParcelable(ARG_PRODUCT_ITEM, ProductItem::class.java) // <- Solusi umum untuk SDK 33+
//            ?: run {
//                Timber.e("DIALOG_ERROR", "ProductItem data hilang dari Bundle atau bukan Parcelable.")
//                dismiss()
//                return
//            }
//        // Setup Judul dan Harga Awal
//        binding.tvVariantTitle.text = "Pilih Varian untuk ${productItem.name}"
//
//        // Listener untuk RadioGroup
//        binding.rgVarian.setOnCheckedChangeListener { group, checkedId ->
//            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)
//            val variantTag = selectedRadioButton.tag as String
//            calculateAndDisplayPrice(variantTag)
//        }
//
//        // Set default selection (misalnya, Nasi Biasa)
//        binding.rbNasiBiasa.isChecked = true
//        calculateAndDisplayPrice("NASI_BIASA")
//
//        // Listener Tombol Tambah
//        binding.btnAddToCart.setOnClickListener {
//            val selectedVariantKey = (binding.rgVarian.findViewById<RadioButton>(binding.rgVarian.checkedRadioButtonId)?.tag as? String) ?: "TANPA_NASI"
//            val calculatedPrice = calculatePrice(selectedVariantKey)
//
//            // 1. Buat NewTransactionItem hasil kalkulasi
//            val newItem = NewTransactionItem(
//                productItemId = productItem.id, // ID Produk Item
//                quantity = 1, // Asumsi kuantitas 1 untuk awal
//                itemPrice = calculatedPrice,
//                isExtraRice = selectedVariantKey.contains("NASI")
//            )
//
//            // 2. Kirim hasil kalkulasi ke Fragment Induk
//            listener?.onVariantItemAdded(newItem)
//
////            Toast.makeText(requireContext(), "Item ditambahkan: ${formatRupiah.format(calculatedPrice)}", Toast.LENGTH_SHORT).show()
//            Toast.makeText(requireContext(), "Item ditambahkan: $calculatedPrice", Toast.LENGTH_SHORT).show()
//            dismiss()
//        }
//    }
//    private fun calculatePrice(variantKey: String): Double {
//        val modifier = variantModifiers[variantKey] ?: 0.0
//        return productItem.sellingPrice + modifier
//    }
//
//    private fun calculateAndDisplayPrice(variantKey: String) {
//        val modifier = variantModifiers[variantKey] ?: 0.0
//
//        // Kalkulasi: Harga Jual (sellingPrice) + Modifier Varian
//        val newPrice = productItem.sellingPrice + modifier
//
//        // Format Rupiah
//        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
//
//        // Tampilkan harga yang dikalkulasikan
//        binding.tvCalculatedPrice.text = formatRupiah.format(newPrice)
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
//
////private fun Bundle.putParcelable(
////    string: String,
////    item: ProductItem
////) {
////}
//
//
