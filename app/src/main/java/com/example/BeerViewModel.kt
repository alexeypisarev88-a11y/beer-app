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
    val consumption = beerDao.getRecentConsumption()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val achievements = consumption.map { list ->
        val totalBeers = list.sumOf { it.count }
        val distinctDays = list.map {
            val cal = Calendar.getInstance()
            cal.timeInMillis = it.date
            cal.get(Calendar.DAY_OF_YEAR)
        }.distinct().size

        listOf(
            Achievement("Beer Connoisseur", "Logged 10+ beers", totalBeers >= 10),
            Achievement("Weekend Warrior", "Logged beer on 5+ days this week", distinctDays >= 5)
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
