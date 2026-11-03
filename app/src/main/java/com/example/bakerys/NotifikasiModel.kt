package com.example.bakerys

data class NotifikasiModel(
    val pesan: String,       // Contoh: "User menyukai resep Anda."
    val waktu: String,       // Contoh: "Baru saja"
    val tipe: String         // "LIKE", "KOMENTAR", atau "FAVORIT"
)