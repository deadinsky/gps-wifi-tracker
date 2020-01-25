package com.example.gpswifitracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// Class extends AndroidViewModel and requires application as a parameter.
class WifiLocationViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: WifiLocationRepository
    // LiveData gives us updated words when they change.
    val count: LiveData<Int>
    val bestLocation: MutableLiveData<Location>
    val guessDistance: MutableLiveData<Double>

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val wifiLocationDao = AppDatabase.getInstance(application, viewModelScope).wifiLocationDao()
        repository = WifiLocationRepository(wifiLocationDao)
        count = repository.count
        bestLocation = repository.bestLocation
        guessDistance = repository.guessDistance
    }

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on
     * the main thread, blocking the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called
     * viewModelScope which we can use here.
     */
    fun insert(wifiLocation: WifiLocation) = viewModelScope.launch {
        repository.insert(wifiLocation)
    }

    fun insertAll(wifiLocations: List<WifiLocation>) = viewModelScope.launch {
        repository.insertAll(wifiLocations)
    }

    fun findByBSSID(bssid: String) = viewModelScope.launch {
        repository.findByBSSID(bssid)
    }

    fun findLocationsBySSID(bssids: List<String>) = viewModelScope.launch {
        repository.findLocationsBySSID(bssids)
    }

    fun findBestLocationBySSID(bssid: String, currentLocation: Location) = viewModelScope.launch {
        repository.findBestLocationBySSID(bssid, currentLocation)
    }
}