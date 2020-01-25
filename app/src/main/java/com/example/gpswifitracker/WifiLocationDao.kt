package com.example.gpswifitracker

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WifiLocationDao {
    @Query("SELECT * FROM wifilocation")
    fun getAll(): List<WifiLocation>

    @Query("SELECT bssid FROM wifilocation WHERE latitude >= :latStart AND latitude <= :latEnd AND longitude >= :longStart AND longitude <= :longEnd")
    fun loadAllNearby(latStart : Double, latEnd : Double, longStart : Double, longEnd : Double): List<String>

    @Query("SELECT * FROM wifilocation WHERE bssid = :bssid")
    suspend fun findByBSSID(bssid: String): List<WifiLocation>

    //SELECT w1.* FROM wifilocation w1 LEFT JOIN wifilocation w2 ON (w1.bssid = w2.bssid AND (w1.level < w2.level OR (w1.level = w2.level AND w1.date < w2.date))) WHERE bssid IN (:bssids)
    @Query("SELECT * FROM wifilocation WHERE bssid IN (:bssids)")
    suspend fun findLocationsBySSID(bssids: List<String>): List<WifiLocation>

    @Query("SELECT latitude, longitude FROM wifilocation WHERE bssid = :bssid ORDER BY distance DESC, date DESC LIMIT 1")
    suspend fun findBestLocationBySSID(bssid: String): Location

    @Insert
    suspend fun insert(wifiLocations: WifiLocation)

    @Insert
    suspend fun insertAll(wifiLocations: List<WifiLocation>)

    @Delete
    suspend fun delete(wifiLocation: WifiLocation)

    @Query("DELETE FROM wifilocation")
    suspend fun wipe()

    @Query("SELECT count(*) FROM wifilocation")
    fun getCount(): LiveData<Int>
}
