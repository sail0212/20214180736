package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.ActivityLabTwoBinding
import com.example.myapplication.model.Advice
import com.example.myapplication.model.DevGoal
import com.example.myapplication.model.ProgramAdviser

/**
 * 实验二：UI 控件代码生成以及 MVC
 *
 * MVC 角色划分：
 *  - 模型（Model）：[ProgramAdviser]，只提供数据与业务逻辑，见 model 包；
 *  - 视图（View）：activity_lab_two.xml，通过 ViewBinding 绑定；
 *  - 控制器（Controller）：本 Activity，负责响应按钮事件、查询模型层，
 *    并把模型返回的结果刷新到视图上。
 */
class LabTwoActivity : AppCompatActivity() {

    // 视图：开启 ViewBinding 后自动生成的绑定类
    private lateinit var binding: ActivityLabTwoBinding

    // 模型层
    private val adviser = ProgramAdviser()

    // 功能区1：已动态添加的 TextView 数量
    private var dynamicCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 开启 ViewBinding 后自动生成 ActivityLabTwoBinding
        binding = ActivityLabTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 适配系统栏
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        setupZone1()
        setupZone2()
    }

    /**
     * 功能区1：通过代码动态添加 TextView
     * 采用 ScrollView 嵌套 LinearLayout 实现滚动显示
     */
    private fun setupZone1() {
        binding.btnAdd.setOnClickListener {
            val content = binding.etInput.text.toString().trim()
            if (content.isEmpty()) {
                Toast.makeText(this, R.string.zone1_toast_empty, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 代码动态生成 TextView（不在布局文件中声明）
            val item = TextView(this).apply {
                text = getString(R.string.zone1_item_format, dynamicCount + 1, content)
                textSize = 16f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = dp(6) }
                setPadding(dp(12), dp(8), dp(12), dp(8))
                setBackgroundResource(R.drawable.bg_dynamic_item)
            }
            binding.llDynamicContainer.addView(item)
            dynamicCount++
            refreshCount()
            // 自动滚动到底部，查看最新添加的 TextView
            binding.scrollZone1.post { binding.scrollZone1.fullScroll(View.FOCUS_DOWN) }
        }

        binding.btnClear.setOnClickListener {
            binding.llDynamicContainer.removeAllViews()
            dynamicCount = 0
            refreshCount()
        }

        refreshCount()
    }

    /**
     * 功能区2：MVC 交互式 ProgramAdviser
     * 按钮事件响应 -> 查询模型层 -> 返回结果并刷新视图
     */
    private fun setupZone2() {
        binding.btnAdvise.setOnClickListener {
            val checkedId = binding.rgGoal.checkedRadioButtonId
            if (checkedId == -1) {
                Toast.makeText(this, R.string.zone2_toast_no_selection, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 1. 视图：读取用户选择的学习方向
            val goal = goalOf(checkedId)
            // 2. 控制器：查询模型层，返回学习建议
            val advice = adviser.advise(goal)
            // 3. 视图：刷新显示结果
            binding.tvAdvice.text = render(advice, goal)
        }

        // 跳转到实验一作品
        binding.btnLab1.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    /** 把模型层返回的建议渲染成界面文本（文案均取自字符串资源） */
    private fun render(advice: Advice, goal: DevGoal): String = buildString {
        appendLine(getString(R.string.advice_target, getString(goal.labelRes)))
        appendLine(getString(R.string.advice_language, getString(advice.languageRes)))
        appendLine(getString(R.string.advice_reason, getString(advice.reasonRes)))
        append(getString(R.string.advice_steps, getString(advice.stepsRes)))
    }

    private fun goalOf(checkedId: Int): DevGoal = when (checkedId) {
        R.id.rbAndroid -> DevGoal.ANDROID
        R.id.rbWeb -> DevGoal.WEB
        R.id.rbGame -> DevGoal.GAME
        R.id.rbData -> DevGoal.DATA
        R.id.rbScript -> DevGoal.SCRIPT
        else -> DevGoal.ANDROID
    }

    private fun refreshCount() {
        binding.tvCount.text = if (dynamicCount == 0) {
            getString(R.string.zone1_count_empty)
        } else {
            getString(R.string.zone1_count, dynamicCount)
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
