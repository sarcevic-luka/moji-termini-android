package com.example.mojitermini

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import kotlin.random.Random

object NetworkManager {
    lateinit var context: Context

    fun getAppointments(completion: (List<Appointment>?) -> Unit) {
        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)!!
        Log.d("NetworkManager", "Fetching appointments with token: $token")

        Thread {
            Thread.sleep(1500)
            // simuliramo da server nekad ne radi
            if (Random.nextInt(1, 6) == 3) {
                Log.e("NetworkManager", "ERROR: server error")
                completion(null)
                return@Thread
            }

            val json = context.assets.open("appointments.json").bufferedReader().readText()
            val response = Gson().fromJson(json, AppointmentsResponse::class.java)

            val name = prefs.getString("patientName", null)!!
            val oib = prefs.getString("patientOIB", null)!!
            Log.d("NetworkManager", "Loaded ${response.appointments.size} appointments for $name, OIB: $oib")

            completion(response.appointments)
        }.start()
    }

    fun getDoctors(completion: (List<Doctor>) -> Unit) {
        Thread {
            Thread.sleep(1000)
            val json = context.assets.open("doctors.json").bufferedReader().readText()
            val doctors = Gson().fromJson(json, Array<Doctor>::class.java).toList()
            completion(doctors)
        }.start()
    }

    fun bookAppointment(doctor: Doctor, slot: String, completion: (Boolean) -> Unit) {
        Log.d("NetworkManager", "Booking $slot with ${doctor.name}")
        Thread {
            Thread.sleep(2000)
            completion(true)
        }.start()
    }

    fun cancelAppointment(id: Int, completion: (Boolean) -> Unit) {
        Thread {
            Thread.sleep(1000)
            completion(true)
        }.start()
    }
}
