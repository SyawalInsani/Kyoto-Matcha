package com.example.kyotomatcha.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

// Lokasi kedai Kyoto-Matcha, hanya untuk MENAMPILKAN jarak di layar.
// Keputusan sah/tidaknya absen tetap dihitung server.
object Outlet {
    const val LAT = -5.1656793
    const val LNG = 119.4281962
    const val RADIUS_M = 100
}

object LocationHelper {

    fun hasPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    fun isEnabled(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return PROVIDERS.any { runCatching { lm.isProviderEnabled(it) }.getOrDefault(false) }
    }

    /** Lokasi terkini, atau null jika izin/GPS tidak tersedia atau habis waktu (10 detik). */
    @SuppressLint("MissingPermission")
    suspend fun current(context: Context): Location? {
        if (!hasPermission(context)) return null
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        for (provider in PROVIDERS) {
            if (!runCatching { lm.isProviderEnabled(provider) }.getOrDefault(false)) continue

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val fresh = withTimeoutOrNull(10_000) {
                    suspendCancellableCoroutine<Location?> { cont ->
                        val signal = CancellationSignal()
                        cont.invokeOnCancellation { signal.cancel() }
                        lm.getCurrentLocation(provider, signal, ContextCompat.getMainExecutor(context)) { cont.resume(it) }
                    }
                }
                if (fresh != null) return fresh
            }
            lm.getLastKnownLocation(provider)?.let { return it }
        }
        return null
    }

    /** Jarak dua titik dalam meter (rumus Haversine, sama dengan yang dipakai server). */
    fun distanceMeters(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Int {
        val result = FloatArray(1)
        Location.distanceBetween(lat1, lng1, lat2, lng2, result)
        return Math.round(result[0])
    }

    private val PROVIDERS = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, "fused")
}
