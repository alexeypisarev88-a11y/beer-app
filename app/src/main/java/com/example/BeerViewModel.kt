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
        val distinctDaysAllTime = list.map {
            val cal = Calendar.getInstance()
            cal.timeInMillis = it.date
            Pair(cal.get(Calendar.YEAR), cal.get(Calendar.DAY_OF_YEAR))
        }.distinct().size

        val consumptionByWeek = list.groupBy {
            val cal = Calendar.getInstance()
            cal.timeInMillis = it.date
            Pair(cal.get(Calendar.YEAR), cal.get(Calendar.WEEK_OF_YEAR))
        }
        val maxBeersInAWeek = consumptionByWeek.values.maxOfOrNull { weekList -> weekList.sumOf { it.count } } ?: 0

        listOf(
            Achievement("Beer Connoisseur", "Drunk beer for 10 distinct days", distinctDaysAllTime >= 10),
            Achievement("Weekend Warrior", "Drunk > 5 beers in a week", maxBeersInAWeek > 5)
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
