package com.example.gpswifitracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import mumayank.com.airlocationlibrary.AirLocation
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class MainActivity : AppCompatActivity() {

    private var airLocation: AirLocation? = null
    var context: Context = this
    private lateinit var wifiLocationViewModel: WifiLocationViewModel
    private var wifiManager: WifiManager? = null
    var closestBSSID: String = "null"
    var currentLocation: com.example.gpswifitracker.Location = Location(0.0, 0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wifiLocationViewModel = ViewModelProviders.of(this).get(WifiLocationViewModel::class.java)
        wifiLocationViewModel.count.observe(this, Observer { count ->
            countView.text = "\n\n\nCount: ${count}"
        })
        wifiLocationViewModel.bestLocation.observe(this, Observer { bestLocation ->
            if (bestLocation.latitude != 0.0 || bestLocation.longitude != 0.0) {
                textView.text = "LAT=${currentLocation.latitude}N\nLONG=${currentLocation.longitude}E\n\n" +
                        "Guess:\nLAT=${bestLocation.latitude}N\nLONG=${bestLocation.longitude}E\n\n" +
                        "Distance between locations:\n${wifiLocationViewModel.guessDistance.value}m"
            }
        })


        wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val self = this

        val wifiScanReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                if (success) {

                    airLocation = AirLocation(self, true, true, object: AirLocation.Callbacks {
                        override fun onSuccess(location: Location) {
                            // location fetched successfully, proceed with it
                            currentLocation = Location(location.latitude, location.longitude)
                            var wifiString = ""
                            var wifiLocations: MutableList<WifiLocation> = ArrayList()
                            var bestDistance = Double.MAX_VALUE
                            wifiManager?.scanResults?.forEach {
                                //val level = WifiManager.calculateSignalLevel(it.level, 5)
                                //https://stackoverflow.com/a/18359639 - constant used returns meters
                                val distance = Math.pow(10.0, (27.55 - 20 * Math.log10(it.frequency.toDouble()) + Math.abs(it.level)) / 20.0)
                                wifiString += "\n" + it.BSSID + ": " + it.SSID + ": " + distance
                                wifiLocations.add(WifiLocation(it.timestamp, it.BSSID, it.SSID, it.level, it.frequency, distance, location.latitude, location.longitude))
                                if (bestDistance > distance) {
                                    bestDistance = distance
                                    closestBSSID = it.BSSID
                                }
                            }
                            textView.text = "LAT=${location.latitude}N\nLONG=${location.longitude}E\n${wifiString}\n${wifiLocations.size}"
                            wifiLocationViewModel.insertAll(wifiLocations)
                        }

                        override fun onFailed(locationFailedEnum: AirLocation.LocationFailedEnum) {
                            // couldn't fetch location due to reason available in locationFailedEnum
                            // you may optionally do something to inform the user, even though the reason may be obvious
                        }

                    })
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(wifiScanReceiver, intentFilter)

        buttonView.setOnClickListener {
            wifiLocationViewModel.findBestLocationBySSID(closestBSSID, currentLocation)
        }

        areaScan()
    }

    fun areaScan() {
        wifiManager?.startScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        airLocation?.onActivityResult(requestCode, resultCode, data) // ADD THIS LINE INSIDE onActivityResult
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        airLocation?.onRequestPermissionsResult(requestCode, permissions, grantResults) // ADD THIS LINE INSIDE onRequestPermissionResult
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
