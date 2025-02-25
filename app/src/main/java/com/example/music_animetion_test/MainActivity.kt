package com.example.music_animetion_test

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.content.ContentResolver

data class Song(val title: String, val artist: String, val album: String, val datapass: String)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 曲情報の取得
        val songs = getLocalMusic(this)

        // 曲情報をログに表示
        for (song in songs) {
            Log.d("MusicList", "Title: ${song.title}, Artist: ${song.artist}, Album: ${song.album}, Data: ${song.datapass}")
        }
    }

    // 曲情報を取得するメソッド
    fun getLocalMusic(context: Context): List<Song> {
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