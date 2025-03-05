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
import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import android.view.animation.AnimationUtils
import android.widget.ImageView

data class Song(val title: String, val artist: String, val album: String, val datapass: String, val uri: Uri)

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 100
    private lateinit var recyclerView: RecyclerView
    private lateinit var musicAdapter: MusicAdapter
    private var musicList: MutableList<MusicItem> = mutableListOf() // 曲リスト
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var btnPlayPause: Button
    private var isPlaying = false // 再生中かどうかを管理
    private var currentIndex = 0 // 現在再生中の曲のインデックス
    private lateinit var seekBar: SeekBar
    private val handler = Handler(Looper.getMainLooper()) // メインスレッドで更新するハンドラー
    private lateinit var artworkImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPlayPause = findViewById(R.id.btnPlayPause)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        artworkImage = findViewById(R.id.artworkImage)

        // 再生・停止ボタン
        btnPlayPause.setOnClickListener {
            if (isPlaying) {
                pauseMusic()
                btnPlayPause.text = "再生"
                btnPlayPause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play, 0, 0, 0)
            } else {
                playMusic(musicList[currentIndex].uri, musicList[currentIndex].albumId)
                btnPlayPause.text = "停止"
                btnPlayPause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause, 0, 0, 0)
            }
        }

        // 前の曲・次の曲ボタン
        val btnPrev: Button = findViewById(R.id.btnPrev)
        val btnNext: Button = findViewById(R.id.btnNext)

        btnPrev.setOnClickListener {
            playPreviousSong()
        }

        btnNext.setOnClickListener {
            playNextSong()
        }

        seekBar = findViewById(R.id.seekBar)
        seekBar.max = 0 // 最初は 0 にしておく

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) { // ユーザーが操作した場合のみ
                    mediaPlayer?.seekTo(progress) // 指定位置に移動
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                handler.removeCallbacks(updateSeekBar) // ユーザーが操作中は更新を止める
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                handler.post(updateSeekBar) // 操作が終わったら更新を再開
            }
        })

        //権限チェック実行
        checkPermission()

        // 曲情報の取得
        Log.d("MusicList", "onCreate() started")
        val songs = getLocalMusic(this)

        // 曲情報をログに表示
        for (song in songs) {
            Log.d("MusicList", "Title: ${song.title}, Artist: ${song.artist}, Album: ${song.album}, Data: ${song.datapass}")
        }

        // Adapterをセット
        musicAdapter = MusicAdapter(musicList)
        recyclerView.adapter = musicAdapter

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
                getLocalMusic(this)
            } else {
                Log.e("Permission", "ストレージアクセス権限が拒否されました")
            }
        }
    }

    // 曲情報を取得するメソッド
    private fun getLocalMusic(context: Context): List<Song> {
        Log.d("MusicList", "getLocalMusic() is called")
        val songList = mutableListOf<Song>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,       // 曲名
            MediaStore.Audio.Media.ARTIST,      // アーティスト名
            MediaStore.Audio.Media.ALBUM,       // アルバム名
            MediaStore.Audio.Media.DATA     // データパス(ファイルパス)
        )

        // 取得したいフォルダのパス（末尾に "%" をつけることで「このフォルダ内」を指定）
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val cursor: Cursor? = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC" // 新しい順に並べる
        )

        if (cursor == null) {
            Log.e("MusicList", "Cursor is null") // ← これが出たら問題あり
        }

        Log.d("MusicList", "Cursor retrieved, count: ${cursor?.count}")

        if (cursor?.count == 0) {
            Log.w("MusicList", "No music found in the MediaStore.")
        }
        cursor?.use {
            val idColumn = it.getColumnIndex(MediaStore.Audio.Media._ID)
            val titleIndex = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistIndex = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val albumIndex = it.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val dataIndex = it.getColumnIndex(MediaStore.Audio.Media.DATA)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val title = it.getString(titleIndex) ?: "Unknown Title"
                val artist = it.getString(artistIndex) ?: "Unknown Artist"
                val album = it.getString(albumIndex) ?: "Unknown Album"
                val data = it.getString(dataIndex) ?: "Found not data"

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
                )

                songList.add(Song(title, artist, album, data, contentUri))
                Log.d("MusicInfo", "Title: $title, Artist: $artist, Album: $album, Data: $contentUri")
            }
            cursor.close()
            Log.d("MusicList", "getLocalMusic method finished")
        }

        return songList
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

    private fun playMusic(uri: Uri, albumId: Long) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(applicationContext, uri)
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            prepare()
            start()

            // シークバーの最大値を曲の長さに設定
            seekBar.max = duration
            seekBar.progress = 0

            // シークバーの更新開始
            handler.post(updateSeekBar)
            Log.d("SeekBar", "Max: ${seekBar.max}, Progress: ${seekBar.progress}")

            // アートワークの表示 & アニメーション適用
            val albumArt = getAlbumArt(albumId)
            if (albumArt != null) {
                artworkImage.setImageBitmap(albumArt)
                artworkImage.visibility = View.VISIBLE
            } else {
                artworkImage.setImageResource(R.drawable.default_album_art) // デフォルト画像
                artworkImage.visibility = View.VISIBLE
            }

            applyArtworkAnimation() // アートワークが変わるたびにアニメーションを適用

            // 曲が再生されている間は回転アニメーションを適用
            val rotate = AnimationUtils.loadAnimation(this@MainActivity, R.anim.rotate)
            artworkImage.startAnimation(rotate)
        }
        isPlaying = true
        btnPlayPause.text = "停止"
    }

    private fun pauseMusic() {
        mediaPlayer?.pause()
        isPlaying = false
        btnPlayPause.text = "再生"
    }

    private fun playNextSong() {
        if (musicList.isNotEmpty()) {
            currentIndex = (currentIndex + 1) % musicList.size
            playMusic(musicList[currentIndex].uri, musicList[currentIndex].albumId)
        }
    }

    private fun playPreviousSong() {
        if (musicList.isNotEmpty()) {
            currentIndex = if (currentIndex - 1 < 0) musicList.size - 1 else currentIndex - 1
            playMusic(musicList[currentIndex].uri, musicList[currentIndex].albumId)
        }
    }

    private val updateSeekBar = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                val currentPosition = it.currentPosition // 現在の再生位置
                seekBar.progress = currentPosition // シークバーを更新
                handler.postDelayed(this, 500) // 0.5秒ごとに更新
            }
        }
    }

    private fun applyArtworkAnimation() {
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        val rotate = AnimationUtils.loadAnimation(this, R.anim.rotate)

        artworkImage.startAnimation(fadeIn) // フェードイン適用
        artworkImage.startAnimation(zoomIn) // ズーム適用
    }

    private fun getAlbumArt(albumId: Long): Bitmap? {
        val uri = ContentUris.withAppendedId(
            Uri.parse("content://media/external/audio/albumart"), albumId
        )
        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            null // 🎵 画像が取得できなかった場合は `null` を返す
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}