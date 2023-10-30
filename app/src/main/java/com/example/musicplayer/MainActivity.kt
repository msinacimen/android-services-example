package com.example.musicplayer


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle



class MainActivity : AppCompatActivity() {
    private var musicService: MusicService? = null
    private var isMusicServiceBound = false

    private val serviceConnection = object : ServiceConnection {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, MusicService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
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

    override fun onDestroy() {
        super.onDestroy()
        if (isMusicServiceBound) {
            unbindService(serviceConnection)
        }
    }
}
