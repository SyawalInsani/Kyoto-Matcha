package com.example.kyotomatcha.data.repository

import com.example.kyotomatcha.data.model.OnboardingPage

class OnboardingRepository {
    fun getPages(): List<OnboardingPage> {
        return listOf(
            OnboardingPage("01 / 03", "Absensi per lokasi", "Catat kehadiran di Kyoto-Matcha dengan verifikasi lokasi."),
            OnboardingPage("02 / 03", "Catat pesanan & pembayaran QRIS", "Catat sekitar 30 pesanan per hari dan pembayaran QRIS dalam satu aplikasi."),
            OnboardingPage("03 / 03", "Pantau stok & laporan", "Pantau persediaan bahan dan laporan harian untuk satu cabang dan tiga karyawan.")
        )
    }
}
