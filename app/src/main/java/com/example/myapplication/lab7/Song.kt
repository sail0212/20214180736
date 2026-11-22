package com.example.myapplication.lab7

import java.util.Locale

/**
 * 实验七：音乐信息数据模型
 * 对应 MediaStore.Audio.Media 中的一条记录，作为 Service 与 Activity 之间传递的数据载体。
 */
data class Song(
    val id: Long,           // MediaStore 中的音乐 ID
    val title: String,      // 歌曲名
    val artist: String,     // 歌手
    val album: String,      // 专辑
    val duration: Int,      // 时长（毫秒）
    val data: String        // 文件路径（备用显示）
) {
    /** 时长格式化为 mm:ss */
    val durationText: String get() = formatDuration(duration)

    companion object {
        @JvmStatic
        fun formatDuration(ms: Int): String {
            if (ms <= 0) return "00:00"
            val totalSec = ms / 1000
            return String.format(Locale.getDefault(), "%02d:%02d", totalSec / 60, totalSec % 60)
        }
    }
}
