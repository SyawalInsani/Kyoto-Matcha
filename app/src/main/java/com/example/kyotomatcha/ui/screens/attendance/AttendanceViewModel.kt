package com.example.kyotomatcha.ui.screens.attendance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kyotomatcha.data.location.LocationHelper
import com.example.kyotomatcha.data.location.Outlet
import com.example.kyotomatcha.data.remote.AttendanceDto
import com.example.kyotomatcha.data.repository.AttendanceRepository
import com.example.kyotomatcha.data.repository.AttendanceResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AttendanceRepository()

    private var records: List<AttendanceDto> = emptyList()
    private var lat: Double? = null
    private var lng: Double? = null

    // Shift awal ditebak dari jam HP: sebelum 17:00 = pagi, sesudahnya = malam. Pengguna bisa mengganti.
    private val _selectedShift = MutableStateFlow(guessShift())
    val selectedShift: StateFlow<String> = _selectedShift.asStateFlow()

    private val _attendanceStatus = MutableStateFlow("Belum masuk") // Belum masuk, Tepat waktu, Terlambat, Selesai
    val attendanceStatus: StateFlow<String> = _attendanceStatus.asStateFlow()

    private val _hasCheckedIn = MutableStateFlow(false)
    val hasCheckedIn: StateFlow<Boolean> = _hasCheckedIn.asStateFlow()

    private val _hasCheckedOut = MutableStateFlow(false)
    val hasCheckedOut: StateFlow<Boolean> = _hasCheckedOut.asStateFlow()

    private val _distance = MutableStateFlow<Int?>(null)
    val distance: StateFlow<Int?> = _distance.asStateFlow()

    private val _gpsStatus = MutableStateFlow("Mencari lokasi...")
    val gpsStatus: StateFlow<String> = _gpsStatus.asStateFlow()

    private val _gpsValid = MutableStateFlow(false)
    val gpsValid: StateFlow<Boolean> = _gpsValid.asStateFlow()

    // Pesan hasil terakhir dari server. Awalan "!" = gagal.
    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()

    private val _selectedTab = MutableStateFlow("Semua")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    /** Ambil lokasi dan catatan absen hari ini. Dipanggil saat layar dibuka dan setelah izin lokasi diberikan. */
    fun refresh() {
        viewModelScope.launch {
            // Catatan absen dulu (cepat), baru lokasi (bisa sampai 10 detik).
            repository.today()?.let {
                records = it
                applyRecords()
            }
            refreshLocation()
        }
    }

    fun selectShift(shift: String) {
        _selectedShift.value = shift
        applyRecords()
    }

    fun checkIn() {
        viewModelScope.launch {
            val (la, ln) = locationOrNull() ?: return@launch
            _isBusy.value = true
            when (val r = repository.checkIn(_selectedShift.value, la, ln)) {
                is AttendanceResult.Success -> _message.value = r.message
                is AttendanceResult.Error -> _message.value = "!${r.message}"
            }
            repository.today()?.let { records = it; applyRecords() }
            _isBusy.value = false
        }
    }

    fun checkOut() {
        viewModelScope.launch {
            val (la, ln) = locationOrNull() ?: return@launch
            _isBusy.value = true
            when (val r = repository.checkOut(_selectedShift.value, la, ln)) {
                is AttendanceResult.Success -> _message.value = r.message
                is AttendanceResult.Error -> _message.value = "!${r.message}"
            }
            repository.today()?.let { records = it; applyRecords() }
            _isBusy.value = false
        }
    }

    fun setSelectedTab(tab: String) {
        _selectedTab.value = tab
    }

    /** Ambil lokasi segar; bila gagal, beri tahu pengguna. */
    private suspend fun locationOrNull(): Pair<Double, Double>? {
        refreshLocation()
        val la = lat
        val ln = lng
        if (la == null || ln == null) {
            _message.value = "!${_gpsStatus.value}"
            return null
        }
        return la to ln
    }

    private suspend fun refreshLocation() {
        val ctx = getApplication<Application>()
        when {
            !LocationHelper.hasPermission(ctx) -> setGps("Izin lokasi diperlukan", false, null)
            !LocationHelper.isEnabled(ctx) -> setGps("GPS belum aktif", false, null)
            else -> {
                val loc = LocationHelper.current(ctx)
                if (loc == null) {
                    setGps("Lokasi belum ditemukan", false, null)
                } else {
                    lat = loc.latitude
                    lng = loc.longitude
                    val d = LocationHelper.distanceMeters(loc.latitude, loc.longitude, Outlet.LAT, Outlet.LNG)
                    setGps(if (d <= Outlet.RADIUS_M) "GPS valid" else "Di luar radius", d <= Outlet.RADIUS_M, d)
                }
            }
        }
    }

    private fun setGps(text: String, valid: Boolean, distance: Int?) {
        _gpsStatus.value = text
        _gpsValid.value = valid
        _distance.value = distance
        if (distance == null) {
            lat = null
            lng = null
        }
    }

    private fun applyRecords() {
        val rec = records.firstOrNull { it.shift == _selectedShift.value }
        _hasCheckedIn.value = rec != null
        _hasCheckedOut.value = rec?.check_out_at != null
        _attendanceStatus.value = when {
            rec == null -> "Belum masuk"
            rec.check_out_at != null -> "Selesai"
            rec.status == "terlambat" -> "Terlambat"
            else -> "Tepat waktu"
        }
    }

    private fun guessShift(): String =
        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 17) "pagi" else "malam"
}
