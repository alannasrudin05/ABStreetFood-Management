package com.praktikum.abstreetfood_management

class Riwayat {
    // Setters
    // Getters
    var name: String
    var status: String
    var jumlah: String
    var price: String
    var time: String
    var imageResId: Int = 0

    constructor(name: String, status: String, jumlah: String, price: String, time: String) {
        this.name = name
        this.status = status
        this.jumlah = jumlah
        this.price = price
        this.time = time
    }

    constructor(
        name: String,
        status: String,
        jumlah: String,
        price: String,
        time: String,
        imageResId: Int
    ) {
        this.name = name
        this.status = status
        this.jumlah = jumlah
        this.price = price
        this.time = time
        this.imageResId = imageResId
    }
}