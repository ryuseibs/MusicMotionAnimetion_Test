package com.example.music_animetion_test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlbumAdapter(
    private val albumlist: List<AlbumItem>,
    private val onItemClick: (AlbumItem) -> Unit
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    class AlbumViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val albumnameTextView: TextView = view.findViewById(R.id.albumNameTextView)
        val artistNameTextView: TextView = view.findViewById(R.id.artistNameTextView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context)
           .inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albumlist[position]
        holder.albumnameTextView.text = album.albumName
        holder.artistNameTextView.text = album.artistName

        holder.itemView.setOnClickListener{ onItemClick(album) }
    }

    override fun getItemCount(): Int = albumlist.size
}