package com.example.myapplication.lab7

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.content.pm.ServiceInfo
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myapplication.R

/**
 * 实验七：音乐播放服务（所有与音乐播放相关的功能都在服务中实现）
 * - MediaPlayer 负责播放/暂停/停止/上一首/下一首
 * - Binder 供 Activity 绑定后获取歌曲名、播放进度等信息
 * - 后台工作线程定时采样播放进度，体现多线程编程
 * - 前台 Notification 显示当前正在播放的歌曲名
 */
class MusicService : Service() {

    /** 供 Activity 绑定获取服务信息与调用播放控制的 Binder */
    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    private val binder = MusicBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var playlist: List<Song> = emptyList()
    private var currentIndex = -1

    /** 后台线程实时采样的播放进度（毫秒），volatile 保证跨线程可见 */
    @Volatile
    private var currentPosition = 0

    @Volatile
    private var progressRunning = false
    private var progressThread: Thread? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaPlayer = MediaPlayer().apply {
            setOnCompletionListener { next() } // 一首播放结束自动下一首
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    // ==================== Binder 暴露给 Activity 的接口 ====================

    fun setPlaylist(list: List<Song>) { playlist = list }

    fun getPlaylist(): List<Song> = playlist

    fun getCurrentIndex(): Int = currentIndex

    fun getCurrentSongName(): String = playlist.getOrNull(currentIndex)?.title ?: ""

    fun getCurrentPosition(): Int = currentPosition

    fun getDuration(): Int = mediaPlayer?.duration ?: 0

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true

    // ==================== 播放控制（在服务中实现） ====================

    fun play(index: Int) {
        if (playlist.isEmpty()) return
        val target = if (index in playlist.indices) index else 0
        currentIndex = target
        val song = playlist[target]
        try {
            mediaPlayer?.apply {
                reset()
                setDataSource(song.data)
                prepare()
                start()
            }
            currentPosition = 0
            startProgressThread()
            showNotification(song.title)
            Log.d(TAG, "play: ${song.title}")
        } catch (e: Exception) {
            Log.e(TAG, "play failed: ${e.message}")
        }
    }

    fun playPause() {
        val player = mediaPlayer ?: return
        if (playlist.isEmpty()) return
        if (currentIndex < 0) { play(0); return }
        if (player.isPlaying) {
            player.pause()
        } else {
            try { player.start() } catch (e: Exception) { Log.e(TAG, "resume failed: ${e.message}") }
        }
    }

    fun stop() {
        mediaPlayer?.stop()
        currentPosition = 0
        stopProgressThread()
        // 停止后把通知更新为已停止状态，保持前台服务通知存在
        showNotification(getString(R.string.lab7_notify_stopped))
    }

    fun next() {
        if (playlist.isEmpty()) return
        play((currentIndex + 1) % playlist.size)
    }

    fun previous() {
        if (playlist.isEmpty()) return
        play((currentIndex - 1 + playlist.size) % playlist.size)
    }

    fun seekTo(ms: Int) {
        try {
            mediaPlayer?.seekTo(ms.coerceIn(0, (mediaPlayer?.duration ?: 0) - 1))
            currentPosition = ms
        } catch (e: Exception) {
            Log.e(TAG, "seekTo failed: ${e.message}")
        }
    }

    // ==================== 多线程：后台线程定时采样播放进度 ====================

    private fun startProgressThread() {
        stopProgressThread()
        progressRunning = true
        progressThread = Thread {
            while (progressRunning) {
                try {
                    val player = mediaPlayer
                    if (player != null && player.isPlaying) {
                        currentPosition = player.currentPosition
                    }
                    Thread.sleep(300)
                } catch (e: InterruptedException) {
                    break
                } catch (e: Exception) {
                    Log.e(TAG, "progress thread: ${e.message}")
                }
            }
        }.also { it.isDaemon = true; it.start() }
    }

    private fun stopProgressThread() {
        progressRunning = false
        progressThread?.interrupt()
        progressThread = null
    }

    // ==================== Notification：显示当前歌曲名 ====================

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.lab7_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = getString(R.string.lab7_channel_desc) }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun showNotification(songName: String) {
        val intent = Intent(this, LabSevenActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            this, 0, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            else android.app.PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(getString(R.string.lab7_notification_title))
            .setContentText(getString(R.string.lab7_notification_playing, songName))
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
        // Android 14+(API 34) 要求前台服务声明 mediaPlayback 类型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onDestroy() {
        stopProgressThread()
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MusicService"
        private const val CHANNEL_ID = "lab7_music_channel"
        private const val NOTIFICATION_ID = 7
    }
}
