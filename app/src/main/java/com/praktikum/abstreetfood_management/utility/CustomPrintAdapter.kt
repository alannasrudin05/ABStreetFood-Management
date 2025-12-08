package com.praktikum.abstreetfood_management.utility

import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.view.View
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import timber.log.Timber
import java.io.FileOutputStream
import java.io.IOException

/**
 * Adapter kustom untuk merender View Android (seperti Nota) menjadi dokumen cetak PDF.
 * Ini digunakan oleh PrintManager sistem Android.
 */
class CustomPrintAdapter(
    private val context: Context,
    private val view: View,
    private val documentName: String
) : PrintDocumentAdapter() {

    private var pageCount: Int = 1 // Asumsi Nota dicetak dalam satu halaman

    // Dimensi yang digunakan untuk rendering PDF
    private var viewWidth: Int = 0
    private var viewHeight: Int = 0

    /**
     * Dipanggil untuk menentukan tata letak dokumen cetak (ukuran halaman, jumlah halaman).
     */
    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback,
        extras: Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback.onLayoutCancelled()
            return
        }

        // 1. Dapatkan ukuran View yang sudah diukur di Helper
        viewWidth = view.measuredWidth
        viewHeight = view.measuredHeight

        // 2. Tentukan ukuran dokumen dan jumlah halaman
        val info = PrintDocumentInfo.Builder(documentName)
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(pageCount) // Hanya satu halaman untuk Nota
            .build()

        // 3. Beri tahu framework bahwa layout sudah siap
        // Menggunakan true jika atribut cetak berubah, agar sistem tau layout harus di-render ulang
        callback.onLayoutFinished(info, oldAttributes != newAttributes)
    }

    /**
     * Dipanggil untuk menulis konten dokumen cetak ke file output (PDF).
     */
    override fun onWrite(
        pages: Array<out PageRange>?,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback.onWriteCancelled()
            return
        }

        val pdfDocument = PdfDocument()

        // 1. Buat informasi halaman
        // Menggunakan ukuran View yang sebenarnya
        val pageInfo = PageInfo.Builder(viewWidth, viewHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        // 2. Render View ke Canvas PDF
        // Canvas PDF memiliki dimensi yang sama dengan PageInfo
        view.draw(page.canvas)

        pdfDocument.finishPage(page)

        // 3. Tulis dokumen PDF ke ParcelFileDescriptor (file output)
        try {
            pdfDocument.writeTo(FileOutputStream(destination.fileDescriptor))
        } catch (e: IOException) {
            Timber.e(e, "Gagal menulis PDF ke file output.")
            callback.onWriteFailed(e.toString())
            return
        } finally {
            pdfDocument.close()
            // Tutup ParcelFileDescriptor
            try {
                destination.close()
            } catch (e: IOException) {
                Timber.e(e, "Gagal menutup ParcelFileDescriptor")
            }
        }

        // 4. Beri tahu framework bahwa penulisan selesai
        callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
    }

    /**
     * Dipanggil ketika proses cetak selesai (baik sukses, gagal, atau dibatalkan).
     */
    override fun onFinish() {
        Timber.i("Print Job Selesai untuk: $documentName")
        super.onFinish()
    }
}