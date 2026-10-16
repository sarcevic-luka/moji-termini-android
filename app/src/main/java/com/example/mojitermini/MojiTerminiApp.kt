package com.example.mojitermini

import android.app.Application
import android.content.Context

class MojiTerminiApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // spremi token i podatke pacijenta da ih imamo kasnije
        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("token", Constants.apiToken)
            .putString("patientName", "Ivana Horvat")
            .putString("patientOIB", "12345678903")
            .apply()
    }
}
