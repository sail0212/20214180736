package com.example.myapplication.lab4

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import cn.edu.sicnu.cardgame.CardMatchingGame
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityCardGameBinding

/**
 * 实验五：翻牌游戏 Controller（控制器）
 * - Model：CardMatchingGame（新增 reset()，支持 2/3 张配对）
 * - View：activity_card_game.xml + item_card.xml，由 RecyclerView 呈现牌面
 * - Adapter：CardAdapter，负责视图创建、数据绑定和点击回调
 *
 * 竖屏 24 张（4 列），横屏 16 张（8 列），方向改变时按规则重新发牌。
 */
class CardGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardGameBinding
    private lateinit var game: CardMatchingGame
    private lateinit var adapter: CardAdapter

    private var matchCount = DEFAULT_MATCH_COUNT

    private val isLandscape: Boolean
        get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    private val cardCount: Int
        get() = if (isLandscape) CARD_COUNT_LANDSCAPE else CARD_COUNT_PORTRAIT

    private val spanCount: Int
        get() = if (isLandscape) SPAN_LANDSCAPE else SPAN_PORTRAIT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = getString(R.string.game_title)

        matchCount = savedInstanceState?.getInt(KEY_MATCH_COUNT, DEFAULT_MATCH_COUNT)
            ?: DEFAULT_MATCH_COUNT
        setupGame()

        binding.btnReset.setOnClickListener {
            game.reset()          // 实验五新增模型方法：保留当前局设置，重新发牌并清零
            updateUI()
        }
        binding.btnMode.setOnClickListener {
            matchCount = if (matchCount == 2) 3 else 2
            setupGame()
        }
        binding.btnExit.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_SCORE, game.score))
            finish()
        }
    }

    /** 按当前屏幕方向创建模型、适配器与布局管理器 */
    private fun setupGame() {
        game = CardMatchingGame(cardCount, matchCount)
        adapter = CardAdapter(game, spanCount, ::onCardClicked)
        binding.rvCards.layoutManager = GridLayoutManager(this, spanCount)
        binding.rvCards.adapter = adapter
        updateUI()
    }

    /** 控制器核心：点击事件 -> 模型处理 -> 刷新视图 */
    private fun onCardClicked(index: Int) {
        game.chooseCardAtIndex(index)
        updateUI()
    }

    private fun updateUI() {
        binding.tvScore.text = getString(R.string.game_score, game.score)
        binding.tvMatchMode.text = getString(R.string.game_match_mode, matchCount)
        binding.tvOrientation.text = getString(R.string.game_orientation_info, cardCount, spanCount)
        binding.btnMode.text = getString(
            if (matchCount == 2) R.string.game_btn_mode_3 else R.string.game_btn_mode_2
        )
        adapter.refreshAll()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_MATCH_COUNT, matchCount)
    }

    companion object {
        const val EXTRA_SCORE = "extra_score"
        private const val CARD_COUNT_PORTRAIT = 24
        private const val CARD_COUNT_LANDSCAPE = 16
        private const val SPAN_PORTRAIT = 4
        private const val SPAN_LANDSCAPE = 8
        private const val DEFAULT_MATCH_COUNT = 2
        private const val KEY_MATCH_COUNT = "key_match_count"
    }
}
