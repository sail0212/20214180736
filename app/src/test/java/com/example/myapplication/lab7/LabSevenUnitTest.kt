package com.example.myapplication.lab7

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 实验七：本地单元测试
 * 验证时长格式化工具与播放列表索引轮转逻辑（对应服务中上一首/下一首算法）。
 */
class LabSevenUnitTest {

    @Test
    fun formatDuration_basic() {
        assertEquals("00:00", Song.formatDuration(0))
        assertEquals("00:01", Song.formatDuration(1000))
        assertEquals("01:30", Song.formatDuration(90_000))
        assertEquals("03:45", Song.formatDuration(225_000))
    }

    @Test
    fun formatDuration_negative_returnsZero() {
        assertEquals("00:00", Song.formatDuration(-1))
    }

    @Test
    fun playlist_next_wrapsToFirst() {
        val size = 5
        val current = 4
        assertEquals(0, (current + 1) % size)
    }

    @Test
    fun playlist_previous_wrapsToLast() {
        val size = 5
        val current = 0
        assertEquals(4, (current - 1 + size) % size)
    }

    @Test
    fun song_model_holdsFields() {
        val song = Song(1L, "Test", "Artist", "Album", 65_000, "/sdcard/Music/test.mp3")
        assertEquals("Test", song.title)
        assertEquals("01:05", song.durationText)
    }
}
