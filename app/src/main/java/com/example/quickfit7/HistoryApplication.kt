package com.example.quickfit7

import android.app.Application

class HistoryApplication: Application() {

    val db by lazy{
        HistoryDatabase.getInstance(this)
    }

}