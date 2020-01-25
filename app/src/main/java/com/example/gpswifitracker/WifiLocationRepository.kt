package com.example.gpswifitracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class WifiLocationRepository(private val wifiLocationDao: WifiLocationDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val count: LiveData<Int> = wifiLocationDao.getCount()
    val bestLocation: MutableLiveData<Location> = MutableLiveData(Location(0.0, 0.0))
    val guessDistance: MutableLiveData<Double> = MutableLiveData(0.0)

    suspend fun insert(wifiLocation: WifiLocation) {
        wifiLocationDao.insert(wifiLocation)
    }

    suspend fun insertAll(wifiLocations: List<WifiLocation>) {
        wifiLocationDao.insertAll(wifiLocations)
    }

    suspend fun findByBSSID(bssid: String) {
        wifiLocationDao.findByBSSID(bssid)
    }

    suspend fun findLocationsBySSID(bssids: List<String>) {
        wifiLocationDao.findLocationsBySSID(bssids)
    }

    suspend fun findBestLocationBySSID(bssid: String, currentLocation: Location) {
        val tempLocation = wifiLocationDao.findBestLocationBySSID(bssid)
        // From https://www.movable-type.co.uk/scripts/latlong.html
        val R = 6371e3 // metres
        val l1 = Math.toRadians(currentLocation.latitude)
        val l2 = Math.toRadians(tempLocation.latitude)
        val d1 = Math.toRadians(tempLocation.latitude - currentLocation.latitude)
        val d2 = Math.toRadians(tempLocation.longitude - currentLocation.longitude)

        val a = Math.sin(d1/2) * Math.sin(d1/2) + Math.cos(l1) * Math.cos(l2) * Math.sin(d2/2) * Math.sin(d2/2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))

        guessDistance.setValue(R * c)
        bestLocation.setValue(tempLocation)
    }
}