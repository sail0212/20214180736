package com.example.myapplication.lab3

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySecondStopwatchBinding

/**
 * 第4个二级Activity：StopWatch 秒表。
 * 采用 SystemClock.elapsedRealtime() 作为计时基准（不受系统时间调整影响），
 * 并在 onSaveInstanceState 中保存“已累计时长 + 是否运行中”，
 * 横竖屏重建时据此恢复基准，保证计时连续不中断。
 */
class SecondStopWatchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondStopwatchBinding
    private val handler = Handler(Looper.getMainLooper())

    private var running = false          // 是否正在计时
    private var elapsedBefore = 0L       // 本段开始前的累计时长(ms)
    private var startTime = 0L           // 本段开始的 elapsedRealtime 基准

    private val tickRunnable = object : Runnable {
        override fun run() {
            updateTime()
            handler.postDelayed(this, TICK_INTERVAL)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondStopwatchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = getString(R.string.s4_title)

        if (savedInstanceState != null) {
            running = savedInstanceState.getBoolean(KEY_RUNNING, false)
            elapsedBefore = savedInstanceState.getLong(KEY_ELAPSED, 0L)
            if (running) {
                // 重建时把基准重新对齐到“当前时刻 - 已累计时长”，计时无缝继续
                startTime = SystemClock.elapsedRealtime() - elapsedBefore
            }
        }
        updateStatus()
        updateTime()
        if (running) handler.post(tickRunnable)

        binding.btnStart.setOnClickListener {
            if (!running) {
                startTime = SystemClock.elapsedRealtime() - elapsedBefore
                running = true
                handler.post(tickRunnable)
                updateStatus()
            }
        }

        binding.btnPause.setOnClickListener {
            if (running) {
                elapsedBefore = SystemClock.elapsedRealtime() - startTime
                running = false
                handler.removeCallbacks(tickRunnable)
                updateTime()
                updateStatus()
            }
        }

        binding.btnReset.setOnClickListener {
            running = false
            elapsedBefore = 0L
            handler.removeCallbacks(tickRunnable)
            updateTime()
            updateStatus()
        }
    }

    private fun currentElapsed(): Long =
        if (running) SystemClock.elapsedRealtime() - startTime else elapsedBefore

    private fun updateTime() {
        val total = currentElapsed()
        val minutes = total / 60_000
        val seconds = (total / 1_000) % 60
        val millis = total % 1_000 / 10
        binding.tvTime.text = String.format("%02d:%02d.%02d", minutes, seconds, millis)
    }

    private fun updateStatus() {
        binding.tvStatus.text =
            getString(if (running) R.string.s4_status_running else R.string.s4_status_paused)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 保存前先把当前段的时长结算进累计值
        elapsedBefore = currentElapsed()
        outState.putBoolean(KEY_RUNNING, running)
        outState.putLong(KEY_ELAPSED, elapsedBefore)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(tickRunnable)
    }

    companion object {
        private const val TICK_INTERVAL = 50L
        private const val KEY_RUNNING = "key_running"
        private const val KEY_ELAPSED = "key_elapsed"
    }
}
