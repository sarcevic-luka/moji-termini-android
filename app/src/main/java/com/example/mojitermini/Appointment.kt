package com.example.mojitermini

data class Appointment(
    var id: Int,
    var doctorName: String,
    var specialty: String,
    var clinic: String,
    var address: String,
    var phone: String,
    var date: String, // npr. "2026-10-20 09:30"
    var status: String, // "booked", "cancelled", "done"
    var note: String?,
    var room: String?
)

data class AppointmentsResponse(
    var appointments: List<Appointment>
)

data class Doctor(
    var name: String,
    var specialty: String,
    var freeSlots: List<String>
)
