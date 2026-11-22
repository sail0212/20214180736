package com.example.myapplication.lab7

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityLabSevenBinding

/**
 * 实验七：基于 Service 的音乐播放应用主界面
 * - 申请读音频权限，使用 ContentResolver 查询音乐列表
 * - 绑定 MusicService，通过 Binder 调用播放控制并获取歌曲名/播放进度
 * - 主线程 Handler 定时读取服务进度，实时刷新 SeekBar（进度采样在服务的工作线程完成）
 * - 拖动 SeekBar 调整播放进度
 */
class LabSevenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLabSevenBinding
    private var musicService: MusicService? = null
    private var bound = false
    private var adapter: MusicAdapter? = null
    private var songs: List<Song> = emptyList()

    /** 是否正在拖动 SeekBar（拖动时不参与自动刷新，避免抖动） */
    private var seeking = false

    private val handler = Handler(Looper.getMainLooper())

    /** 权限申请回调：同意后加载音乐列表 */
    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.all { it }) {
            loadMusic()
        } else {
            Toast.makeText(this, R.string.lab7_permission_denied, Toast.LENGTH_LONG).show()
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            bound = true
            musicService?.setPlaylist(songs)
            refreshUI()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
            musicService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLabSevenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = getString(R.string.lab7_app_title)

        adapter = MusicAdapter(songs) { index -> playAt(index) }
        binding.recyclerMusic.layoutManager = LinearLayoutManager(this)
        binding.recyclerMusic.adapter = adapter

        setupButtons()
        setupSeekBar()
        requestPermissionsIfNeeded()
        bindMusicService()
        startProgressLoop()
    }

    /** 根据系统版本申请读音频权限（API 33+ 为 READ_MEDIA_AUDIO） */
    private fun requestPermissionsIfNeeded() {
        val permissions = mutableListOf<String>()
        val readAudio = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_AUDIO
        else Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, readAudio) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(readAudio)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (permissions.isNotEmpty()) requestPermission.launch(permissions.toTypedArray())
        else loadMusic()
    }

    /** 使用 ContentResolver 查询媒体库中的音乐文件 */
    private fun loadMusic() {
        songs = MusicRepository(this).queryMusic()
        binding.tvSongCount.text = getString(R.string.lab7_song_count, songs.size)
        adapter = MusicAdapter(songs) { index -> playAt(index) }
        binding.recyclerMusic.adapter = adapter
        musicService?.setPlaylist(songs)
        if (songs.isEmpty()) {
            Toast.makeText(this, R.string.lab7_no_music, Toast.LENGTH_LONG).show()
        }
    }

    private fun bindMusicService() {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun setupButtons() {
        binding.btnPlayPause.setOnClickListener {
            val service = musicService
            if (service == null || songs.isEmpty()) {
                Toast.makeText(this, R.string.lab7_no_music, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            service.playPause()
            refreshUI()
        }
        binding.btnNext.setOnClickListener { musicService?.next(); refreshUI() }
        binding.btnPrevious.setOnClickListener { musicService?.previous(); refreshUI() }
        binding.btnStop.setOnClickListener {
            musicService?.stop()
            binding.tvCurrentSong.text = getString(R.string.lab7_no_song)
        }
    }

    /** SeekBar：拖动时调整当前播放进度（Binder 调用服务 seekTo） */
    private fun setupSeekBar() {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) binding.tvProgress.text = Song.formatDuration(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) { seeking = true }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seeking = false
                musicService?.seekTo(seekBar?.progress ?: 0)
            }
        })
    }

    private fun playAt(index: Int) {
        val service = musicService
        if (service == null) {
            Toast.makeText(this, R.string.lab7_service_not_ready, Toast.LENGTH_SHORT).show()
            return
        }
        service.play(index)
        adapter?.setCurrentIndex(index)
        refreshUI()
    }

    /** 主线程定时从服务读取播放进度与状态，实时刷新 SeekBar（多线程协作） */
    private fun startProgressLoop() {
        val tick = object : Runnable {
            override fun run() {
                val service = musicService
                if (service != null) {
                    val duration = service.getDuration()
                    val position = service.getCurrentPosition()
                    if (!seeking && duration > 0) {
                        binding.seekBar.max = duration
                        binding.seekBar.progress = position.coerceIn(0, duration)
                    }
                    binding.tvProgress.text = Song.formatDuration(position)
                    binding.tvDuration.text = Song.formatDuration(duration)
                    binding.btnPlayPause.text = getString(
                        if (service.isPlaying()) R.string.lab7_btn_pause else R.string.lab7_btn_play
                    )
                    val name = service.getCurrentSongName()
                    if (name.isNotEmpty()) binding.tvCurrentSong.text = name
                    val idx = service.getCurrentIndex()
                    if (idx != adapter?.getCurrentIndexValue()) adapter?.setCurrentIndex(idx)
                }
                handler.postDelayed(this, 300)
            }
        }
        handler.post(tick)
    }

    private fun refreshUI() {
        val service = musicService ?: return
        val name = service.getCurrentSongName()
        binding.tvCurrentSong.text = if (name.isEmpty()) getString(R.string.lab7_no_song) else name
        binding.btnPlayPause.text = getString(
            if (service.isPlaying()) R.string.lab7_btn_pause else R.string.lab7_btn_play
        )
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        if (bound) unbindService(connection)
        bound = false
        super.onDestroy()
    }
}

/** 扩展：读取适配器当前高亮位置 */
private fun MusicAdapter.getCurrentIndexValue(): Int = currentIndexCompat
