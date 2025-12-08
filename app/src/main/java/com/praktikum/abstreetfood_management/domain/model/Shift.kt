package com.praktikum.abstreetfood_management.domain.model

data class Shift(
    val id: String,
    val userId: String,
    val outletId: String,
    val startTime: Long,
    val endTime: Long? = null,
    val startCash: Double,
    val endCashActual: Double? = null,
    val isClosed: Boolean
)