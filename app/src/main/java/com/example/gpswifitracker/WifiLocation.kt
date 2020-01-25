package com.example.gpswifitracker

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["date", "bssid"])
data class WifiLocation(
    @ColumnInfo(name = "date") @NonNull var date: Long,
    @ColumnInfo(name = "bssid") @NonNull val bssid: String,
    @ColumnInfo(name = "ssid") @NonNull val ssid: String,
    @ColumnInfo(name = "level") @NonNull val level: Int,
    @ColumnInfo(name = "frequency") @NonNull val frequency: Int,
    @ColumnInfo(name = "distance") @NonNull val distance: Double,
    @ColumnInfo(name = "latitude") @NonNull val latitude: Double,
    @ColumnInfo(name = "longitude") @NonNull val longitude: Double
)

data class Location(val latitude: Double, val longitude: Double)