package com.example.music_animetion_test

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// クラス定義（曲情報）
data class MusicItem(val title: String, val artist: String)

// Adapterクラス
class MusicAdapter(private val musicList: List<MusicItem>) :
    RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

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
        Log.d("MusicDebug", "Binding ViewHolder: ${item.title} by ${item.artist}")  // 追加
        holder.titleTextView.text = item.title
        holder.artistTextView.text = item.artist
    }

    override fun getItemCount(): Int = musicList.size
}