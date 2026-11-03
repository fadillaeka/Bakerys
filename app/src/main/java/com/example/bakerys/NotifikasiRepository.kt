package com.example.bakerys

object NotifikasiRepository {
    // List penampung notifikasi sementara di memori
    val daftarNotifikasi = mutableListOf<NotifikasiModel>()

    fun tambahNotifikasi(pesan: String, tipe: String) {
        // Menambahkan notifikasi baru di baris paling atas
        daftarNotifikasi.add(0, NotifikasiModel(pesan, "Baru saja", tipe))
    }
}