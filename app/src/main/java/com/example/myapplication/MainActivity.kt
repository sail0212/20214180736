package com.example.myapplication

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * 实验：基于多语言版本的纯代码交互式 Hello World App
 * 界面完全用 Kotlin 代码构建，不使用任何 XML 布局文件。
 * 点击不同国旗按钮，切换对应语言版本的问候语。
 */
class MainActivity : AppCompatActivity() {

    // 三种语言版本：中文 / English / 日本語
    private val greetings = arrayOf("你好，世界！", "Hello, World!", "こんにちは、世界！")
    private val languageNames = arrayOf("中文", "English", "日本語")
    private val flagIds = intArrayOf(R.drawable.flag_cn, R.drawable.flag_gb, R.drawable.flag_jp)
    private val themeColors =
        intArrayOf(Color.parseColor("#D32F2F"), Color.parseColor("#1976D2"), Color.parseColor("#388E3C"))

    private var currentLanguage = 0

    private lateinit var greetingText: TextView
    private lateinit var languageText: TextView
    private lateinit var flagButtons: List<ImageButton>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(buildUserInterface())

        // 适配系统栏内边距（纯代码方式）
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        applyLanguage()
    }

    /** 用纯代码构建整个界面 */
    private fun buildUserInterface(): LinearLayout {
        val density = resources.displayMetrics.density
        fun dp(v: Int) = (v * density).toInt()

        // 根布局：垂直线性布局
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // 问候语文本
        greetingText = TextView(this).apply {
            textSize = 36f
            setTypeface(typeface, Typeface.BOLD)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        // 当前语言名称
        languageText = TextView(this).apply {
            textSize = 20f
            gravity = Gravity.CENTER
            setPadding(0, dp(12), 0, dp(24))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        // 国旗按钮行：水平线性布局
        val flagRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        flagButtons = flagIds.mapIndexed { index, resId ->
            ImageButton(this).apply {
                setImageResource(resId)
                scaleType = ImageView.ScaleType.FIT_CENTER
                background = null
                contentDescription = languageNames[index]
                setPadding(dp(4), dp(4), dp(4), dp(4))
                layoutParams = LinearLayout.LayoutParams(dp(110), dp(76)).apply {
                    marginStart = dp(8)
                    marginEnd = dp(8)
                }
                setOnClickListener {
                    currentLanguage = index
                    applyLanguage()
                }
            }
        }
        flagButtons.forEach(flagRow::addView)

        root.addView(greetingText)
        root.addView(languageText)
        root.addView(flagRow)
        return root
    }

    /** 根据当前选择刷新界面显示 */
    private fun applyLanguage() {
        greetingText.text = greetings[currentLanguage]
        languageText.text = languageNames[currentLanguage]
        languageText.setTextColor(themeColors[currentLanguage])
    }
}
