package com.praktikum.abstreetfood_management.domain.model

data class DailyMetric(
    val currentTotal: Double,
    val previousTotal: Double,
    val percentageChange: Double // Positif untuk naik, Negatif untuk turun
)