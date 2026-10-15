package com.example

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BeerDao {
    @Query("SELECT * FROM beer_consumption ORDER BY date DESC LIMIT 7")
    fun getRecentConsumption(): Flow<List<BeerConsumption>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsumption(consumption: BeerConsumption)
}
