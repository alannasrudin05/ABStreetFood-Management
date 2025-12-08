package com.praktikum.abstreetfood_management.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.chip.Chip
import com.praktikum.abstreetfood_management.R
import com.praktikum.abstreetfood_management.data.adapter.ReportDetailAdapter
import com.praktikum.abstreetfood_management.data.repository.DailySalesData
import com.praktikum.abstreetfood_management.databinding.FragmentLaporanBinding
import com.praktikum.abstreetfood_management.domain.usecase.ReportPeriod
import com.praktikum.abstreetfood_management.viewmodel.ExportStatus
import com.praktikum.abstreetfood_management.viewmodel.LaporanViewModel
import com.praktikum.abstreetfood_management.viewmodel.TransaksiViewModel
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.AndroidEntryPoint
import com.github.mikephil.charting.formatter.ValueFormatter
import java.io.File
import java.io.FileWriter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class LaporanFragment : Fragment() {

    private var _binding: FragmentLaporanBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaporanViewModel by viewModels() // Asumsi Anda punya LaporanViewModel
    private lateinit var reportAdapter: ReportDetailAdapter // Adapter untuk preview


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLaporanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi Chart
//        setupChart(binding.lineChartSales)

        // Amati data dari ViewModel
//        viewModel.dailySalesData.observe(viewLifecycleOwner) { data ->
//            if (data.isNotEmpty()) {
//                loadChartData(data)
//                setupChartAndLoadData(binding.lineChartSales, data)
//            }
//        }


        setupToolbar()
        setupRecyclerView()
        setupListeners()
        setupObservers()

        viewModel.fetchReport(ReportPeriod.TODAY)
    }

    /**
     * ✅ FUNGSI UTAMA: Memuat data, mengatur styling, dan memplot LineChart.
     */
    private fun loadAndStyleChart(chart: LineChart, salesData: List<DailySalesData>) {
        val dataSize = salesData.size
        val entries = mutableListOf<Entry>()

        // 1. Konversi data Domain ke Entry Chart
        salesData.forEachIndexed { index, data ->
            // X: Index Hari | Y: Revenue
            entries.add(Entry(index.toFloat(), data.revenue.toFloat()))
        }

        val maxRevenue = salesData.maxOfOrNull { it.revenue } ?: 0.0
        val maxAxisValue = (maxRevenue * 1.15f).toFloat()
        // 2. Setup Styling dan Data Set
        val chartColor = ContextCompat.getColor(requireContext(), R.color.primary_teal)
        var textColor = ContextCompat.getColor(requireContext(), R.color.text_primary)

        val dataSet = LineDataSet(entries, "Pendapatan Harian").apply {
            color = chartColor
            lineWidth = 2f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawCircles(true)
            circleRadius = 4f
            setCircleColor(chartColor)
            circleHoleColor = Color.WHITE
            setDrawValues(false)
            setDrawFilled(true)
            // Asumsi chart_gradient_fill.xml ada
            fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.chart_gradient_fill)
        }

        // 3. Setup X-Axis (Tanggal di Bawah)
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM // ✅ Memindahkan label ke bawah
            setDrawGridLines(false)
            textColor = textColor
            granularity = 1f
            axisMinimum = -0.5f
            axisMaximum = dataSize.toFloat() - 0.5f
            labelCount = dataSize

            // Value Formatter untuk Tanggal (mm/dd)
            valueFormatter = object : ValueFormatter() {
                private val dateFormat = SimpleDateFormat("dd/MM", Locale.ROOT)
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    if (index < 0 || index >= salesData.size) return ""
                    val timestamp = salesData[index].dayTimestamp
                    return dateFormat.format(Date(timestamp))
                }
            }
        }

        // 4. Setup Y-Axis (Pendapatan)
        chart.axisLeft.apply {
            setDrawGridLines(true)
            textColor = textColor

            // Mengatur Batas Y-Axis
            axisMinimum = 0f // ✅ Wajib agar tidak ada nilai negatif
            axisMaximum = if (maxAxisValue > 1000f) maxAxisValue else 1000f // Set minimal 1000f atau maks data + buffer
//            axisMaximum = (maxRevenue * 1.15f).toFloat()

            // Formatter Rupiah sederhana
            valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
                        minimumFractionDigits = 0
                    }
                    return formatRupiah.format(value)
                }
            }
        }

        chart.axisRight.isEnabled = false // Sembunyikan Axis Y Kanan
        chart.description.isEnabled = false
        chart.legend.isEnabled = false

        // 5. Apply Data & Refresh
        val lineData = LineData(dataSet)
        chart.data = lineData
        chart.animateY(800)
        chart.invalidate()
    }

    private fun setupChart(chart: LineChart, dataSize: Int, salesData: List<DailySalesData>) {
        // Pengaturan visual dasar
//        chart.description.isEnabled = false
//        chart.legend.isEnabled = false
//        // ... (Pengaturan Axis X dan Y) ...

        // --- PENGATURAN SUMBU X (Tanggal) ---
        chart.xAxis.apply {
            // ✅ 1. Pindahkan posisi sumbu X ke bawah
            position = XAxis.XAxisPosition.BOTTOM

            // Atur agar label X tidak miring (jika diperlukan)
            setLabelRotationAngle(0f)

            // Tentukan batas data untuk mengontrol lebar horizontal
            axisMinimum = -0.5f
            axisMaximum = dataSize.toFloat() - 0.5f
            labelCount = dataSize // Paksa label untuk setiap hari

            // ✅ 2. Gunakan ValueFormatter untuk menampilkan Tanggal
            valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                private val dateFormat = SimpleDateFormat("dd/MM", Locale.ROOT)

                override fun getFormattedValue(value: Float): String {
                    // value adalah index hari (0, 1, 2, ...)
                    val index = value.toInt()
                    if (index < 0 || index >= salesData.size) return ""

                    // Ambil timestamp dari data yang sesuai
                    val timestamp = salesData[index].dayTimestamp
                    return dateFormat.format(Date(timestamp))
                }
            }
        }

        // --- PENGATURAN SUMBU Y (Pendapatan) ---
//        chart.axisLeft.apply {
//            // ... (Pengaturan warna dan grid) ...
//            // ...
//            // Pastikan axisMinimum tidak -Rp1.2 (nilai yang Anda lihat di gambar),
//            // atau pastikan rentang Y tidak terlalu ketat (misalnya, rentang default
//            // chart adalah -1.2 hingga 1.2 karena nilai 40000.0 tidak cocok)
//            // Coba tambahkan:
//             axisMinimum = 0f
//             axisMaximum = 50000f // Atau lebih besar dari nilai maks Anda
//        }
//        chart.axisLeft.apply {
//            setDrawGridLines(true)
//            textColor = textColor
//
//            // Mengatur Batas Y-Axis
//            axisMinimum = 0f // ✅ Wajib agar tidak ada nilai negatif
//            axisMaximum = if (maxAxisValue > 1000) maxAxisValue else 1000f // Set minimal 1000f atau maks data + buffer
//
//            valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
//                override fun getFormattedValue(value: Float): String {
//                    val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
//                        minimumFractionDigits = 0
//                    }
//                    return formatRupiah.format(value)
//                }
//            }
//        }

        chart.axisRight.isEnabled = false // Sembunyikan sumbu Y kanan

        // ... (Pengaturan LineData dan Invalidate) ...
    }

    private fun loadChartData(salesData: List<DailySalesData>) {
        val entries = mutableListOf<Entry>()

        // Mengkonversi model Domain menjadi Entry Chart
        salesData.forEachIndexed { index, data ->
            // X: Nomor Hari (0, 1, 2, ...) | Y: Revenue
            entries.add(Entry(index.toFloat(), data.revenue.toFloat()))
        }

        val dataSet = LineDataSet(entries, "Pendapatan Harian")
        // ... (Pengaturan warna, garis, lingkaran) ...

        val lineData = LineData(dataSet)
        binding.lineChartSales.data = lineData
        binding.lineChartSales.invalidate() // Refresh Chart
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        reportAdapter = ReportDetailAdapter() // Buat adapter ini
        binding.rvReportPreview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reportAdapter
        }
    }

    private fun setupListeners() {
        // 1. Filter Chip Listener
        binding.chipGroupPeriod.setOnCheckedStateChangeListener { group, checkedIds ->

            val activeId = checkedIds.firstOrNull()
            val period = when (checkedIds.firstOrNull()) {
                binding.chip7Days.id -> ReportPeriod.LAST_7_DAYS
                binding.chip30Days.id -> ReportPeriod.LAST_30_DAYS
                else -> ReportPeriod.TODAY
            }

            val chipName = when (period) {
                ReportPeriod.TODAY -> "Hari Ini"
                ReportPeriod.LAST_7_DAYS -> "7 Hari Terakhir"
                ReportPeriod.LAST_30_DAYS -> "30 Hari Terakhir"
            }

            if (activeId != null) {
                // Log ini akan muncul saat chip diaktifkan (checked=true)
                Log.d("CHIP_STATUS", "$chipName diaktifkan (checked: true), ID: $activeId")
            } else {
                // Kasus ini jarang terjadi karena ChipGroup menggunakan app:singleSelection="true"
                // yang menjamin selalu ada chip yang aktif.
                Log.d("CHIP_STATUS", "Semua chip tidak aktif atau terjadi kesalahan.")
            }

            viewModel.fetchReport(period)
        }

        // Panggil fetchReport saat pertama kali dibuka (default: TODAY)
//        viewModel.fetchReport(ReportPeriod.TODAY)


        // 2. Export CSV Listener
        binding.btnExportCsv.setOnClickListener {
            exportData()
        }
    }

    private fun setupObservers() {
        // Amati data laporan untuk preview
//        viewModel.salesReportData.observe(viewLifecycleOwner) { data ->
//            reportAdapter.submitList(data)
//            // TODO: Update LineChart di sini menggunakan data
//            // updateChart(data)
//        }

        viewModel.dailySalesData.observe(viewLifecycleOwner) { data ->
            if (data.isNotEmpty()) {
                loadAndStyleChart(binding.lineChartSales, data) // Panggil fungsi utama
            } else {
                binding.lineChartSales.clear()
                binding.lineChartSales.setNoDataText("Tidak ada data penjualan dalam periode ini.")
                binding.lineChartSales.invalidate()
            }
        }

        // Amati data laporan untuk preview tabel
        viewModel.salesReportData.observe(viewLifecycleOwner) { data ->
            reportAdapter.submitList(data)
        }

        // Amati status ekspor
        viewModel.csvExportStatus.observe(viewLifecycleOwner) { status ->
            if (status is ExportStatus.Success) {
                Toast.makeText(context, "Export Sukses: ${status.filePath}", Toast.LENGTH_LONG).show()
            } else if (status is ExportStatus.Error) {
                Toast.makeText(context, "Export Gagal: ${status.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun exportData() {
        // Ambil periode yang sedang aktif dari ChipGroup
        val checkedId = binding.chipGroupPeriod.checkedChipId
        val period = when (checkedId) {
            binding.chip7Days.id -> ReportPeriod.LAST_7_DAYS
            binding.chip30Days.id -> ReportPeriod.LAST_30_DAYS
            else -> ReportPeriod.TODAY
        }
        viewModel.exportSalesData(period)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}