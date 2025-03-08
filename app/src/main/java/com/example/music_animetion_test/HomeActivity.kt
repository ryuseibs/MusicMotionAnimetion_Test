package com.example.music_animetion_test

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnGoToMusicList).setOnClickListener {
            val intent = Intent(this, MusicListActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnGoToAlbumList).setOnClickListener {
            val intent = Intent(this, AlbumListActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnGoToArtistList).setOnClickListener {
            val intent = Intent(this, ArtistListActivity::class.java)
            startActivity(intent)
        }
    }
}