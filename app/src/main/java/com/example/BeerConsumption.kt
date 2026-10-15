package com.example

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "beer_consumption")
data class BeerConsumption(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long,
    val count: Int
)
