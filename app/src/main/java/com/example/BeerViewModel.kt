package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class BeerViewModel(private val beerDao: BeerDao) : ViewModel() {
    val consumption = beerDao.getAllConsumption()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val achievements = consumption.map { list ->
        // Total distinct days all time
        val distinctDaysAllTime = list.map {
            val cal = Calendar.getInstance().apply { timeInMillis = it.date }
            Pair(cal.get(Calendar.YEAR), cal.get(Calendar.DAY_OF_YEAR))
        }.distinct().size

        // Distinct days grouped by calendar week
        val consumptionByWeek = list.groupBy {
            val cal = Calendar.getInstance().apply { timeInMillis = it.date }
            Pair(cal.get(Calendar.YEAR), cal.get(Calendar.WEEK_OF_YEAR))
        }
        val maxDaysInAWeek = consumptionByWeek.values.maxOfOrNull { weekList ->
            weekList.map {
                val cal = Calendar.getInstance().apply { timeInMillis = it.date }
                cal.get(Calendar.DAY_OF_YEAR)
            }.distinct().size
        } ?: 0

        // Distinct days grouped by calendar month
        val consumptionByMonth = list.groupBy {
            val cal = Calendar.getInstance().apply { timeInMillis = it.date }
            Pair(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
        }
        val maxDaysInAMonth = consumptionByMonth.values.maxOfOrNull { monthList ->
            monthList.map {
                val cal = Calendar.getInstance().apply { timeInMillis = it.date }
                cal.get(Calendar.DAY_OF_MONTH)
            }.distinct().size
        } ?: 0

        listOf(
            Achievement("Beer Connoisseur", "Drunk beer for 10 distinct days", distinctDaysAllTime >= 10),
            Achievement("Beer Veteran", "Drunk beer for 100 distinct days", distinctDaysAllTime >= 100),
            Achievement("Beer Legend", "Drunk beer for 1,000 distinct days", distinctDaysAllTime >= 1000),
            Achievement("Weekend Warrior", "Drunk beer on > 5 days in a week", maxDaysInAWeek > 5),
            Achievement("Flawless Week", "All 7 days in a single calendar week", maxDaysInAWeek >= 7),
            Achievement("Oktoberfest Spirit", "20+ days with beer in a single calendar month", maxDaysInAMonth >= 20)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addConsumption(count: Int) {
        viewModelScope.launch {
            beerDao.insertConsumption(BeerConsumption(date = System.currentTimeMillis(), count = count))
        }
    }
}

class BeerViewModelFactory(private val beerDao: BeerDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BeerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BeerViewModel(beerDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
