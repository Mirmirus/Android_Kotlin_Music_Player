package com.myapp.kotlintest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private var mp: MediaPlayer? = null
    private var currentSong: MutableList<Int> = mutableListOf(R.raw.pirates)
    private lateinit var seekBar: SeekBar
    private lateinit var fabStop: FloatingActionButton
    private lateinit var fabPause: FloatingActionButton
    private lateinit var fabPlay: FloatingActionButton
    private lateinit var currentTimeTextView: TextView
    private lateinit var totalTimeTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        seekBar = findViewById(R.id.seekbar)
        fabStop = findViewById(R.id.fab_stop)
        fabPause = findViewById(R.id.fab_pause)
        fabPlay = findViewById(R.id.fab_play)
        currentTimeTextView = findViewById(R.id.currentTimeTextView)
        totalTimeTextView = findViewById(R.id.totalTimeTextView)

        controlSound(currentSong[0])
    }

    private fun controlSound(id: Int) {
        fabPlay.setOnClickListener {
            if (mp == null) {
                mp = MediaPlayer.create(this, id)
                Log.d("MainActivity", "ID: ${mp!!.audioSessionId}")
                initialiseSeeker()
            }
            mp?.start()
            Log.d("MainActivity", "Duration: ${mp!!.audioSessionId}")
        }

        fabPause.setOnClickListener {
            if (mp !== null) {
                mp?.pause()
                Log.d("MainActivity", "Paused at: ${mp!!.currentPosition / 1000} seconds")
            }
        }

        fabStop.setOnClickListener {
            if (mp !== null) {
                mp?.stop()
                mp?.reset()
                mp?.release()
                mp = null
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mp?.seekTo(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
    }

    private fun initialiseSeeker() {
        seekBar.max = mp!!.duration
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    val currentPosition = mp!!.currentPosition
                    seekBar.progress = currentPosition

                    // Обновление текста с информацией о времени
                    val currentTimeText = formatTime(currentPosition)
                    val totalTimeText = formatTime(mp!!.duration)

                    currentTimeTextView.text = currentTimeText
                    totalTimeTextView.text = totalTimeText

                    handler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    seekBar.progress = 0
                }
            }
        }, 0)
    }

    private fun formatTime(timeInMillis: Int): String {
        val totalSeconds = timeInMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
