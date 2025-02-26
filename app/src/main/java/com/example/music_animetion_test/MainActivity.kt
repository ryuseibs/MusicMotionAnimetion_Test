package com.example.music_animetion_test

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.os.Build
import android.Manifest

data class Song(val title: String, val artist: String, val album: String, val datapass: String)

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE =100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //権限チェック実行
        checkPermission()

        // 曲情報の取得
        val songs = getLocalMusic(this)

        // 曲情報をログに表示
        for (song in songs) {
            Log.d("MusicList", "Title: ${song.title}, Artist: ${song.artist}, Album: ${song.album}, Data: ${song.datapass}")
        }
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
                getLocalMusic(this)
            } else {
                Log.e("Permission", "ストレージアクセス権限が拒否されました")
            }
        }
    }

    // 曲情報を取得するメソッド
    private fun getLocalMusic(context: Context): List<Song> {
        val songList = mutableListOf<Song>()
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,       // 曲名
            MediaStore.Audio.Media.ARTIST,      // アーティスト名
            MediaStore.Audio.Media.ALBUM,       // アルバム名
            MediaStore.Audio.Media.DATA     // データパス(ファイルパス)
        )

        // 取得したいフォルダのパス（末尾に "%" をつけることで「このフォルダ内」を指定）
        val selection = "(${MediaStore.Audio.Media.DATA} LIKE ? OR ${MediaStore.Audio.Media.DATA} LIKE ?) " +
                "AND ${MediaStore.Audio.Media.IS_MUSIC} != 0"

        val selectionArgs = arrayOf(
            "/storage/emulated/0/Music/%",    // Musicフォルダ内
        )
        val cursor: Cursor? = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null)

        cursor?.use {
            val titleIndex = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistIndex = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val albumIndex = it.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val dataIndex = it.getColumnIndex(MediaStore.Audio.Media.DATA)

            while (it.moveToNext()) {
                val title = it.getString(titleIndex) ?: "Unknown Title"
                val artist = it.getString(artistIndex) ?: "Unknown Artist"
                val album = it.getString(albumIndex) ?: "Unknown Album"
                val data = it.getString(dataIndex) ?: "Found not data"

                songList.add(Song(title, artist, album, data))
                Log.d("MusicInfo", "Title: $title, Artist: $artist, Album: $album, Data: $data")
            }
        }

        return songList
    }
}