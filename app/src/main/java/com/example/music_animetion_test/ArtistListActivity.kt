package com.example.music_animetion_test

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class ArtistItem(val artistId: Long, val artistName: String)

class ArtistListActivity : AppCompatActivity() {

    private val REQUEST_CODE = 100
    private lateinit var recyclerView: RecyclerView
    private lateinit var artistAdapter: ArtistAdapter
    private var artistList: MutableList<ArtistItem> = mutableListOf() // アルバムリスト

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_list)

        recyclerView = findViewById(R.id.recyclerViewArtist)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 権限チェック実行
        checkPermission()

        // Adapterをセット
        artistAdapter = ArtistAdapter(artistList)
        recyclerView.adapter = artistAdapter

        // 取得したアーティストをリストに追加
        loadArtists()
    }

    //権限チェック用の関数
    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_AUDIO), REQUEST_CODE)
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE)
            }
        }
    }

    private fun loadArtists() {

        val progression = arrayOf(
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Media.ARTIST
        )

        val cursor: Cursor? = contentResolver.query(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
            progression,
            null,
            null,
            MediaStore.Audio.Artists.ARTIST + " ASC"
        )

        cursor?.use {
            val idColumn = it.getColumnIndex(MediaStore.Audio.Artists._ID)
            val artistNameColumn = it.getColumnIndex(MediaStore.Audio.Artists.ARTIST)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val artistName = it.getString(artistNameColumn)

                artistList.add(ArtistItem(id,artistName))
                Log.d("ArtistDebug", "Found artist: $artistName (ID: $id)")
            }
        }
        Log.d("ArtistDebug", "Retrieved ${artistList.size} artists") // 取得件数の確認
        artistAdapter.notifyDataSetChanged()
    }
}