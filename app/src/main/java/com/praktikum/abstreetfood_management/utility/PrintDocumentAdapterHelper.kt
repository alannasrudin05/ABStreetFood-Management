package com.praktikum.abstreetfood_management.utility

import android.content.Context
import android.print.PrintDocumentAdapter
import android.view.View

/**
 * Helper class untuk membuat PrintDocumentAdapter dari sebuah View.
 */
object PrintDocumentAdapterHelper {

    /**
     * Membuat PrintDocumentAdapter yang menangani layout View untuk proses cetak.
     * @param viewRoot View yang ingin dicetak (Contoh: dialog_receipt.xml root)
     * @param context Konteks aplikasi
     */
//    fun createPrintAdapter(viewRoot: View, context: Context): PrintDocumentAdapter {
//        // TODO: Anda harus mengimplementasikan logika konversi View ke dokumen cetak di sini.
//        // Implementasi terbaik adalah membuat subclass dari PrintDocumentAdapter
//        // yang menangani pemotongan halaman dan rendering View ke PDF.
//
//        // Untuk penyederhanaan cepat, Anda bisa menggunakan Webview jika itu opsi,
//        // tetapi untuk View yang kompleks seperti Nota, Anda memerlukan implementasi kustom.
//
//        // KARENA INI KOMPLEKS, KITA HANYA MENGEMBALIKAN IMPLEMENTASI MINIMAL UNTUK KOMPILASI:
//        return object : PrintDocumentAdapter() {
//            // ... (Implementasi onLayout, onWrite, dst.)
//            // Biasanya, menggunakan WebView atau mencetak PDF yang di-*generate* dari HTML/View
//            // adalah cara terbaik.
//        }
//    }
    /**
     * Membuat PrintDocumentAdapter yang menangani layout View untuk proses cetak.
     * @param viewRoot View yang ingin dicetak
     * @param context Konteks aplikasi
     * @param documentName Nama dokumen cetak (Wajib ditambahkan)
     */
    fun createPrintAdapter(viewRoot: View, context: Context, documentName: String): PrintDocumentAdapter {

        // 1. Memastikan View sudah diukur sebelum dicetak (penting untuk rendering yang benar)
        viewRoot.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        viewRoot.layout(0, 0, viewRoot.measuredWidth, viewRoot.measuredHeight)

        // 2. Mengembalikan instance dari CustomPrintAdapter
        return CustomPrintAdapter(context, viewRoot, documentName)
    }
}