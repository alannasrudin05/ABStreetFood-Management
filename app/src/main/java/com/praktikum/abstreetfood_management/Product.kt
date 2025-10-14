package com.praktikum.abstreetfood_management

class Product {
    // Setters
    // Getters
    var name: String
    var date: String
    var quantity: String
    var imageResId: Int = 0 // Optional: for product image

    constructor(name: String, date: String, quantity: String) {
        this.name = name
        this.date = date
        this.quantity = quantity
    }

    constructor(name: String, date: String, quantity: String, imageResId: Int) {
        this.name = name
        this.date = date
        this.quantity = quantity
        this.imageResId = imageResId
    }
}