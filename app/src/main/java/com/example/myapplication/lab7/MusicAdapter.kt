package com.example.myapplication.lab7

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemSongBinding

/**
 * 实验七：音乐列表适配器（RecyclerView）
 */
class MusicAdapter(
    private val songs: List<Song>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<MusicAdapter.SongViewHolder>() {

    private var currentIndex = -1

    class SongViewHolder(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.binding.tvSongTitle.text = song.title
        holder.binding.tvSongArtist.text = holder.itemView.context.getString(
            R.string.lab7_song_artist, song.artist, song.durationText
        )
        val playing = position == currentIndex
        holder.binding.root.setCardBackgroundColor(
            ContextCompat.getColor(
                holder.itemView.context,
                if (playing) R.color.lab7_playing_bg else R.color.lab7_item_bg
            )
        )
        holder.itemView.setOnClickListener { onItemClick(position) }
    }

    override fun getItemCount(): Int = songs.size

    /** 高亮当前正在播放的歌曲 */
    fun setCurrentIndex(index: Int) {
        val old = currentIndex
        currentIndex = index
        if (old in songs.indices) notifyItemChanged(old)
        if (index in songs.indices) notifyItemChanged(index)
    }

    /** 当前高亮位置（供 Activity 轮询比对，避免重复刷新） */
    val currentIndexCompat: Int get() = currentIndex
}
