package com.example.myapplication.lab7

import android.content.Context
import android.provider.MediaStore

/**
 * 实验七：音乐文件仓库
 * 通过 ContentResolver 查询 MediaStore 中的音频文件列表（实验要求 1）。
 */
class MusicRepository(private val context: Context) {

    /**
     * 使用 ContentResolver 从媒体库查询音乐文件列表。
     * SDK 13+(API 33) 需要 READ_MEDIA_AUDIO 权限，低版本需要 READ_EXTERNAL_STORAGE。
     */
    fun queryMusic(): List<Song> {
        val result = mutableListOf<Song>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )
        // 只查询音乐文件且时长大于 1 秒，过滤铃声等短音频
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} > ?"
        val selectionArgs = arrayOf("1000")
        val sortOrder = "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"

        val cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
        cursor?.use { c ->
            val idIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            while (c.moveToNext()) {
                result.add(
                    Song(
                        id = c.getLong(idIndex),
                        title = c.getString(titleIndex) ?: "<unknown>",
                        artist = c.getString(artistIndex) ?: "<unknown>",
                        album = c.getString(albumIndex) ?: "<unknown>",
                        duration = c.getInt(durationIndex),
                        data = c.getString(dataIndex) ?: ""
                    )
                )
            }
        }
        return result
    }
}
