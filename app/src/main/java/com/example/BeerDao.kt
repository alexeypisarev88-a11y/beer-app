package com.example

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BeerDao {
    @Query("SELECT * FROM beer_consumption ORDER BY date DESC")
    fun getAllConsumption(): Flow<List<BeerConsumption>>

    @Query("SELECT EXISTS(SELECT 1 FROM beer_consumption WHERE date >= :startOfDay AND date < :endOfDay)")
    suspend fun hasConsumedToday(startOfDay: Long, endOfDay: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsumption(consumption: BeerConsumption)
}
