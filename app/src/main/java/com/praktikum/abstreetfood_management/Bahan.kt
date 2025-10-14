package com.praktikum.abstreetfood_management

class Bahan {
    // Setters
    // Getters
    @JvmField
    var name: String
    @JvmField
    var quantity: String
    @JvmField
    var isLowStock: Boolean
    @JvmField
    var imageResId: Int = 0

    constructor(name: String, quantity: String, isLowStock: Boolean) {
        this.name = name
        this.quantity = quantity
        this.isLowStock = isLowStock
    }

    constructor(name: String, quantity: String, isLowStock: Boolean, imageResId: Int) {
        this.name = name
        this.quantity = quantity
        this.isLowStock = isLowStock
        this.imageResId = imageResId
    }
}