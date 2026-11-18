package com.example.myapplication.lab3

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySecondReturnBinding

/** 第2个二级Activity：用户输入内容后，通过 setResult 把内容返回给主界面。 */
class SecondReturnActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondReturnBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondReturnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = getString(R.string.s2_title)

        binding.btnConfirm.setOnClickListener {
            val value = binding.etInput.text.toString().trim()
            if (value.isEmpty()) {
                Toast.makeText(this, R.string.s2_empty, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent().apply {
                putExtra(LabThreeActivity.EXTRA_RETURN_VALUE, value)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}
