package com.example.myapplication.lab3

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.LabTwoActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityLabThreeBinding

/**
 * 实验三：多Activity开发
 * 本 Activity 在本实验中承担“MainActivity”角色，提供 4 个按钮：
 *  1) 正向传参启动第1个二级Activity
 *  2) 启动第2个二级Activity并接收其返回的结果
 *  3) 启动第3个二级Activity（隐式Intent相机拍照）
 *  4) 启动第4个二级Activity（StopWatch 横竖屏连续计时）
 * 所有文案均来自字符串资源。
 */
class LabThreeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLabThreeBinding
    private var returnText: String? = null

    // 接收第2个二级Activity返回的结果（Activity Result API）
    private val returnLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val value = result.data?.getStringExtra(EXTRA_RETURN_VALUE)
                returnText = value
                binding.tvReturnResult.text = getString(R.string.lab3_return_format, value.orEmpty())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLabThreeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 提交要求：APP应用标题为“姓名+学号”
        title = getString(R.string.lab3_app_title)

        // 横竖屏重建时恢复返回的显示
        if (savedInstanceState != null) {
            returnText = savedInstanceState.getString(KEY_RETURN_TEXT)
        }
        binding.tvReturnResult.text = if (returnText.isNullOrEmpty()) {
            getString(R.string.lab3_return_hint)
        } else {
            getString(R.string.lab3_return_format, returnText)
        }

        // 1) 正向传参
        binding.btnForward.setOnClickListener {
            val intent = Intent(this, SecondForwardActivity::class.java).apply {
                putExtra(EXTRA_USER_NAME, getString(R.string.lab3_extra_value))
                putExtra(EXTRA_FROM, getString(R.string.lab3_title))
            }
            startActivity(intent)
        }

        // 2) 启动并接收返回结果
        binding.btnReturn.setOnClickListener {
            returnLauncher.launch(Intent(this, SecondReturnActivity::class.java))
        }

        // 3) 相机拍照
        binding.btnCamera.setOnClickListener {
            startActivity(Intent(this, SecondCameraActivity::class.java))
        }

        // 4) StopWatch
        binding.btnStopwatch.setOnClickListener {
            startActivity(Intent(this, SecondStopWatchActivity::class.java))
        }

        // 附加：跳转到实验二作品
        binding.btnToLab2.setOnClickListener {
            startActivity(Intent(this, LabTwoActivity::class.java))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_RETURN_TEXT, returnText)
    }

    companion object {
        const val EXTRA_USER_NAME = "extra_user_name"
        const val EXTRA_FROM = "extra_from"
        const val EXTRA_RETURN_VALUE = "extra_return_value"
        private const val KEY_RETURN_TEXT = "key_return_text"
    }
}
