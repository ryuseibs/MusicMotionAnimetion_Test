package com.example.music_animetion_test

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// クラス定義（曲情報）
data class MusicItem(val title: String, val artist: String, val uri: Uri, val albumId: Long)

// Adapterクラス
class MusicAdapter(private val musicList: List<MusicItem>) :
    RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    private var onItemClickListener: ((MusicItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (MusicItem) -> Unit) {
        onItemClickListener = listener
    }

    // ViewHolder（1つのリストアイテムを管理）
    class MusicViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val artistTextView: TextView = view.findViewById(R.id.artistTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_music, parent, false)
        return MusicViewHolder(view)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val item = musicList[position]
        Log.d("MusicDebug", "Binding ViewHolder: ${item.title} by ${item.artist} URI:${item.uri}" )  // 追加
        holder.titleTextView.text = item.title
        holder.artistTextView.text = item.artist

        // **リストの曲をタップしたら再生**
        holder.itemView.setOnClickListener {
            Log.d("MusicDebug", "🎵 Adapter でタップ: ${item.title} - ${item.uri}") // 確認用ログ
            onItemClickListener?.invoke(item)
        }
    }

    override fun getItemCount(): Int = musicList.size
}