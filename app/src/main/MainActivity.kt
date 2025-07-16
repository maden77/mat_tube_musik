package com.mattube.musik

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mattube.musik.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var player: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        player = MediaPlayer.create(this, R.raw.sample_music)

        binding.playButton.setOnClickListener {
            if (!player.isPlaying) player.start()
        }

        binding.pauseButton.setOnClickListener {
            if (player.isPlaying) player.pause()
        }
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }
}
