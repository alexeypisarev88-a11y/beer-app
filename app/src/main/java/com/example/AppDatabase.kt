package com.example

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BeerConsumption::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun beerDao(): BeerDao
}
