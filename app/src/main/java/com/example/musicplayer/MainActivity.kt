package com.example.musicplayer

import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private var musicService: MusicService? = null
    private var isMusicServiceBound = false
    private val musicServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            isMusicServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            isMusicServiceBound = false
        }
    }

    private var locationService: LocationService? = null
    private var isLocationServiceBound = false
    private val locationServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.getService()
            isLocationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            locationService = null
            isLocationServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Müzik servisi bağlantısını oluşturun
        val musicServiceIntent = Intent(this, MusicService::class.java)
        bindService(musicServiceIntent, musicServiceConnection, Context.BIND_AUTO_CREATE)

        // Konum servisi bağlantısını oluşturun
        val locationServiceIntent = Intent(this, LocationService::class.java)
        bindService(locationServiceIntent, locationServiceConnection, Context.BIND_AUTO_CREATE)
    }

    fun playMusic(view: View) {
        musicService?.playMusic()
    }

    fun pauseMusic(view: View) {
        musicService?.pauseMusic()
    }

    fun stopMusic(view: View) {
        musicService?.stopMusic()
    }

    fun getLocationAndShowAlert(view: View) {
        locationService?.getLocation(object : LocationService.LocationListener {
            override fun onLocationReceived(location: Location) {
                // Konum bilgisini alın
                val latitude = location.latitude
                val longitude = location.longitude

                // Uyarı (AlertDialog) oluşturun ve konum bilgisini gösterin
                val alertDialog = AlertDialog.Builder(this@MainActivity)
                    .setTitle("Konum Bilgisi")
                    .setMessage("Latitude: $latitude\nLongitude: $longitude")
                    .setPositiveButton("Tamam", DialogInterface.OnClickListener { dialog, _ ->
                        dialog.dismiss()
                    })
                    .create()
                alertDialog.show()
            }

            override fun onLocationError(error: String) {
                // Hata durumunda uyarı gösterin
                val alertDialog = AlertDialog.Builder(this@MainActivity)
                    .setTitle("Hata")
                    .setMessage(error)
                    .setPositiveButton("Tamam", DialogInterface.OnClickListener { dialog, _ ->
                        dialog.dismiss()
                    })
                    .create()
                alertDialog.show()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isMusicServiceBound) {
            unbindService(musicServiceConnection)
        }
        if (isLocationServiceBound) {
            unbindService(locationServiceConnection)
        }
    }
}
