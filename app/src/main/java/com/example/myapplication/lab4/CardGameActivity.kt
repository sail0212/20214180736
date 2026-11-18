package com.example.myapplication.lab4

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sicnu.cardgame.CardMatchingGame
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityCardGameBinding

/**
 * 实验四：翻牌游戏Activity —— MVC 中的 Controller（控制器）
 * - Model：cn.edu.sicnu.cardgame.CardMatchingGame（附件 model.zip 原样代码）
 * - View：activity_card_game.xml 布局 + 代码动态生成的卡牌按钮（GridLayout）
 * - Controller：本 Activity，接受用户点击、调用模型处理、同步刷新界面，并可重置一局新游戏
 */
class CardGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardGameBinding

    private lateinit var game: CardMatchingGame      // 模型层
    private val cardButtons = mutableListOf<Button>() // 视图层的卡牌按钮
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = getString(R.string.game_title)

        buildCardGrid()     // 构建 View
        startNewGame()      // 初始化 Model 并同步 View

        binding.btnReset.setOnClickListener { startNewGame() }
        binding.btnExit.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_SCORE, score))
            finish()
        }
    }

    /** 以代码方式在 GridLayout 中生成 16 张卡牌按钮（4 列 × 4 行） */
    private fun buildCardGrid() {
        val density = resources.displayMetrics.density
        val size = (76 * density).toInt()
        for (i in 0 until CARD_COUNT) {
            val button = Button(this).apply {
                text = ""
                textSize = 20f
                gravity = Gravity.CENTER
                layoutParams = GridLayout.LayoutParams().apply {
                    width = size
                    height = size
                    setMargins((4 * density).toInt(), (4 * density).toInt(),
                        (4 * density).toInt(), (4 * density).toInt())
                }
                setOnClickListener { onCardClicked(i) }
            }
            cardButtons.add(button)
            binding.gridCards.addView(button)
        }
    }

    /** 开始新的一局：新建模型、重置得分为 0、刷新所有卡牌为背面 */
    private fun startNewGame() {
        game = CardMatchingGame(CARD_COUNT)
        score = 0
        updateUI()
    }

    /** 控制器核心：把用户点击交给模型处理，再依据模型状态同步界面 */
    private fun onCardClicked(index: Int) {
        game.chooseCardAtIndex(index)   // 委托模型处理业务规则
        score = game.score
        updateUI()                      // 同步界面
    }

    /** 根据模型状态刷新得分和每张牌的显示 */
    private fun updateUI() {
        binding.tvScore.text = getString(R.string.game_score, score)
        for (i in 0 until CARD_COUNT) {
            val card = game.cardAtIndex(i)
            val button = cardButtons[i]
            if (card.isMatched) {
                button.text = card.toString()
                button.setBackgroundColor(Color.parseColor("#C8E6C9")) // 配对成功：浅绿
                button.isEnabled = false
            } else if (card.isChosen) {
                button.text = card.toString()
                button.setBackgroundColor(Color.parseColor("#FFF9C4")) // 已翻开：浅黄
                button.isEnabled = true
            } else {
                button.text = ""
                button.setBackgroundResource(R.drawable.bg_card_back)    // 牌背
                button.isEnabled = true
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SCORE, score)
    }

    companion object {
        const val EXTRA_SCORE = "extra_score"
        private const val CARD_COUNT = 16
        private const val KEY_SCORE = "key_score"
    }
}
