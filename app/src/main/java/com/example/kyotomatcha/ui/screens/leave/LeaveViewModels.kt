package com.example.kyotomatcha.ui.screens.leave

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kyotomatcha.data.remote.LeaveBody
import com.example.kyotomatcha.data.remote.LeaveDto
import com.example.kyotomatcha.data.remote.StaffDto
import com.example.kyotomatcha.data.remote.UserDto
import com.example.kyotomatcha.data.repository.LeaveRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Kasir/karyawan: ajukan izin tidak hadir dan lihat izin saya. */
class LeaveViewModel(application: Application) : AndroidViewModel(application) {
    private val _staff = MutableStateFlow<List<StaffDto>>(emptyList())
    val staff: StateFlow<List<StaffDto>> = _staff.asStateFlow()

    private val _myLeaves = MutableStateFlow<List<LeaveDto>>(emptyList())
    val myLeaves: StateFlow<List<LeaveDto>> = _myLeaves.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    // false sampai daftar izin pertama kali selesai dimuat (agar tidak tampil "belum ada" saat masih memuat).
    private val _loaded = MutableStateFlow(false)
    val loaded: StateFlow<Boolean> = _loaded.asStateFlow()

    // Pesan hasil terakhir. Awalan "!" = gagal.
    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

    init {
        reload()
    }

    fun reload() {
        viewModelScope.launch {
            _staff.value = LeaveRepository.staff()
            LeaveRepository.myLeaves()?.let { _myLeaves.value = it }
            _loaded.value = true
        }
    }

    /** replacementKind: "none", "rekan" (coveredBy) atau "luar" (replacementName). */
    fun submit(
        date: String?,
        shift: String,
        reason: String,
        replacementKind: String,
        coveredBy: Int?,
        replacementName: String,
        onSuccess: () -> Unit
    ) {
        when {
            date == null -> _message.value = "!Pilih tanggal izin."
            reason.isBlank() -> _message.value = "!Alasan wajib diisi."
            replacementKind == "rekan" && coveredBy == null -> _message.value = "!Pilih rekan pengganti."
            replacementKind == "luar" && replacementName.isBlank() -> _message.value = "!Isi nama pengganti."
            _isSaving.value -> Unit
            else -> viewModelScope.launch {
                _isSaving.value = true
                val error = LeaveRepository.submit(
                    LeaveBody(
                        date = date,
                        shift = shift,
                        reason = reason.trim(),
                        covered_by = if (replacementKind == "rekan") coveredBy else null,
                        replacement_name = if (replacementKind == "luar") replacementName.trim() else null
                    )
                )
                _isSaving.value = false
                if (error == null) {
                    _message.value = "Izin diajukan. Menunggu persetujuan pemilik."
                    LeaveRepository.myLeaves()?.let { _myLeaves.value = it }
                    onSuccess()
                } else {
                    _message.value = "!$error"
                }
            }
        }
    }

    fun clearMessage() {
        _message.value = ""
    }
}

/** Pemilik: setujui atau tolak izin dan pendaftar baru. */
class ApprovalViewModel(application: Application) : AndroidViewModel(application) {
    private val _leaves = MutableStateFlow<List<LeaveDto>>(emptyList())
    val leaves: StateFlow<List<LeaveDto>> = _leaves.asStateFlow()

    private val _users = MutableStateFlow<List<UserDto>>(emptyList())
    val users: StateFlow<List<UserDto>> = _users.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

    init {
        reload()
    }

    fun reload() {
        viewModelScope.launch {
            _isLoading.value = true
            LeaveRepository.pendingLeaves()?.let { _leaves.value = it }
            LeaveRepository.pendingUsers()?.let { _users.value = it }
            _isLoading.value = false
        }
    }

    fun decideLeave(id: Int, approve: Boolean, reason: String? = null) {
        viewModelScope.launch {
            val error = LeaveRepository.decideLeave(id, approve, reason)
            _message.value = error?.let { "!$it" } ?: if (approve) "Izin disetujui." else "Izin ditolak."
            reload()
        }
    }

    fun decideUser(id: Int, approve: Boolean, role: String) {
        viewModelScope.launch {
            val error = LeaveRepository.decideUser(id, approve, role)
            _message.value = error?.let { "!$it" } ?: if (approve) "Akun disetujui." else "Pendaftaran ditolak."
            reload()
        }
    }
}
