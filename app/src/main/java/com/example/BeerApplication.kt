package com.example

import android.app.Application
import androidx.room.Room

class BeerApplication : Application() {
    val database by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "beer_database"
        ).build()
    }
}
