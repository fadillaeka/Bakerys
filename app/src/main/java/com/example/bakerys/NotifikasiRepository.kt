package com.example.bakerys

object NotifikasiRepository {
    // List penampung notifikasi di memori
    val daftarNotifikasi = mutableListOf<NotifikasiModel>()

    fun tambahNotifikasi(pesan: String, tipe: String) {
        // Menambahkan notifikasi baru di baris paling atas
        daftarNotifikasi.add(0, NotifikasiModel(pesan, "Baru saja", tipe))
        // Sinkronisasi ke tabel notifikasi Supabase
        BakeryStorageManager.simpanNotifikasiKeSupabase(pesan)
    }
}
