package com.example.music_animetion_test

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MusicListActivity : AppCompatActivity() {

    private val REQUEST_CODE = 100
    private lateinit var recyclerView: RecyclerView
    private lateinit var musicAdapter: MusicAdapter
    private var musicList: MutableList<MusicItem> = mutableListOf() // 曲リスト

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_list)

        recyclerView = findViewById(R.id.recyclerViewMusic)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //権限チェック実行
        checkPermission()

        // Adapterをセット
        musicAdapter = MusicAdapter(musicList)
        recyclerView.adapter = musicAdapter

        // タップしたらMainActivityに遷移
        musicAdapter.setOnItemClickListener { song ->
            Log.d("MusicDebug", "選択された曲: ${song.title}, URI: ${song.uri}, AlbumID: ${song.albumId}")

            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("SONG_URI", song.uri.toString())  // 曲の `Uri` を `String` にして渡す
                putExtra("ALBUM_ID", song.albumId)         // アルバム ID を渡す
            }
            // **データが正しく入っているかログを出力**
            Log.d("MusicDebug", "Intent に格納するデータ -> SONG_URI: ${song.uri}, ALBUM_ID: ${song.albumId}")
            Log.d("MusicDebug", "Intent で MainActivity に遷移: $intent")
            startActivity(intent)
        }

        // 取得した曲をリストに追加
        loadMusic()
    }

    //権限チェック用の関数
    private fun checkPermission(){
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

    //権限リクエスト結果を処理
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "ストレージアクセス権限が許可されました")
            } else {
                Log.e("Permission", "ストレージアクセス権限が拒否されました")
            }
        }
    }

    // 曲情報を取得する（View)
    private fun loadMusic() {
        Log.d("MusicDebug", "loadMusic() called")  // 確認用ログ

        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID),
            null,
            null,
            null
        )
        cursor?.use {
            val idColumn = it.getColumnIndex(MediaStore.Audio.Media._ID)
            val titleColumn = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val albumColumn = it.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn)
                val albumId = it.getLong(albumColumn)

                val songUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
                )

                Log.d("MusicDebug", "Found song: $title by $artist, URI: $songUri, AlbumID: $albumId")  // デバッグ用
                musicList.add(MusicItem(title, artist, songUri, albumId))
            }
        }
        Log.d("MusicDebug", "Retrieved ${musicList.size} songs")  // 取得件数の確認

        // RecyclerViewにデータがセットされたことを通知
        musicAdapter.notifyDataSetChanged()
        Log.d("MusicDebug", "RecyclerView updated, total items: ${musicList.size}")
    }

}