package com.praktikum.abstreetfood_management.data.repository

import com.praktikum.abstreetfood_management.data.local.dao.TransactionItemDao
import com.praktikum.abstreetfood_management.domain.model.SaleReportItem
import javax.inject.Inject

class ReportRepository @Inject constructor(
    private val transactionItemDao: TransactionItemDao
) {
    suspend fun getDetailedSales(startTime: Long, endTime: Long): List<SaleReportItem> {
        return transactionItemDao.getSalesReport(startTime, endTime)
    }
}