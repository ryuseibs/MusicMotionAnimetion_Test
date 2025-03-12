package com.example.music_animetion_test

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.music_animetion_test.AlbumAdapter.AlbumViewHolder

class ArtistAdapter(
    private val artistList: List<ArtistItem>,
) : RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

    class ArtistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val artistNameTextView: TextView = view.findViewById(R.id.artistNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_artist, parent, false)
        return ArtistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        val artist = artistList[position]
        holder.artistNameTextView.text = artist.artistName

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, AlbumListActivity::class.java).apply {
                putExtra("artistId", artist.artistId)
                putExtra("artistName", artist.artistName)
            }
            holder.itemView.context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int = artistList.size
}