package com.example.music_animetion_test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AlbumAdapter(
    private val albumlist: List<AlbumItem>,
    private val onItemClick: (AlbumItem) -> Unit
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    class AlbumViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // TODO :val albumnameTextView: TextView = view.findViewById(R.id.albumNameTextView)
        // TODO :val artistNameTextView: TextView = view.findViewById(R.id.artistNameTextView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context)
        // TODO :   .inflate(R.layout.item_album, parent, false)
        // TODO :return AlbumViewHolder(view)
        return TODO("Provide the return value")
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albumlist[position]
        // TODO :holder.albumNameTextView.text = album.albumName
        // TODO :holder.artistNameTextView.text = album.artistName

        holder.itemView.setOnClickListener{ onItemClick(album) }
    }

    override fun getItemCount(): Int = albumlist.size
}