package com.example.myapplication.lab3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySecondForwardBinding

/** 第1个二级Activity：显示由主界面通过 Intent 正向传入的参数。 */
class SecondForwardActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondForwardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondForwardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = getString(R.string.s1_title)

        // 取出正向传参并显示（缺省时给出字符串资源中的默认值）
        val name = intent.getStringExtra(LabThreeActivity.EXTRA_USER_NAME)
            ?: getString(R.string.lab3_return_hint)
        val from = intent.getStringExtra(LabThreeActivity.EXTRA_FROM).orEmpty()
        binding.tvReceived.text = if (from.isEmpty()) name else "$name\n$from"

        binding.btnFinish.setOnClickListener { finish() }
    }
}
