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

data class AlbumItem(val albumId: Long, val albumName: String, val artistName: String)

class AlbumListActivity : AppCompatActivity() {

    private val REQUEST_CODE = 100
    private lateinit var recyclerView: RecyclerView
    private lateinit var albumAdapter: AlbumAdapter
    private var albumList: MutableList<AlbumItem> = mutableListOf() // アルバムリスト

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_list)

        recyclerView = findViewById(R.id.recyclerViewAlbum)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 権限チェック実行
        checkPermission()

        // Adapterをセット
        albumAdapter = AlbumAdapter(albumList) { albumItem ->
            // クリック時の処理（次の画面に遷移する場合など）
            Log.d("AlbumDebug", "Clicked album: ${albumItem.albumName} by ${albumItem.artistName}")
        }
        recyclerView.adapter = albumAdapter

        // 取得したアルバムをリストに追加
        loadAlbums()
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

    // アルバム情報を取得する
    private fun loadAlbums() {

        val progression = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST
        )

        val cursor: Cursor? = contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            progression,
            null,
            null,
            MediaStore.Audio.Albums.ALBUM + " ASC"
        )

        cursor?.use {
            val idColumn = it.getColumnIndex(MediaStore.Audio.Albums._ID)
            val albumColumn = it.getColumnIndex(MediaStore.Audio.Albums.ALBUM)
            val artistColumn = it.getColumnIndex(MediaStore.Audio.Albums.ARTIST)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val albumName = it.getString(albumColumn) ?: "Unknown Album"
                val artistName = it.getString(artistColumn) ?: "Unknown Artist"

                albumList.add(AlbumItem(id, albumName, artistName))
                Log.d("AlbumDebug", "Found Album: $albumName by $artistName")
            }
        }
        Log.d("AlbumDebug", "Retrieved ${albumList.size} albums")
        albumAdapter.notifyDataSetChanged() //リスト更新
    }

}