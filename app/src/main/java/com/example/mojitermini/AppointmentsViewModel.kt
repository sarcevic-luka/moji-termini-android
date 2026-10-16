package com.example.mojitermini

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Timer
import kotlin.concurrent.schedule

class AppointmentsViewModel {
    var appointments by mutableStateOf(listOf<Appointment>())
    var isLoading by mutableStateOf(false)

    var timer: Timer? = null
    var onUpdate: (() -> Unit)? = null

    init {
        // osvjezi termine svakih 30 sekundi
        timer = Timer()
        timer?.schedule(30000, 30000) {
            this@AppointmentsViewModel.loadAppointments()
        }

        onUpdate = {
            Log.d("AppointmentsViewModel", "Appointments updated, count: ${this.appointments.size}")
        }
    }

    fun loadAppointments() {
        isLoading = true
        NetworkManager.getAppointments { result ->
            this.isLoading = false
            if (result != null) {
                this.appointments = result!!
            }
            this.onUpdate?.invoke()
        }
    }

    fun cancel(index: Int) {
        val appointment = appointments[index]
        NetworkManager.cancelAppointment(appointment.id) { success ->
            if (success) {
                this.appointments = this.appointments.toMutableList().apply { removeAt(index) }
            }
        }
    }
}
