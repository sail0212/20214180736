package com.example.myapplication.lab4

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityInfoEditBinding

/**
 * 实验四：信息修改Activity
 * 提供姓名、性别、电话的编辑，点击保存后通过 Intent 把结果回传给主Activity。
 * 按实验要求，修改后的信息无需保存到文件，仅在内存中传递。
 */
class InfoEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = getString(R.string.edit_title)

        // 用主界面传入的当前值预填
        binding.etName.setText(intent.getStringExtra(EXTRA_NAME))
        binding.etPhone.setText(intent.getStringExtra(EXTRA_PHONE))
        val gender = intent.getStringExtra(EXTRA_GENDER)
        if (gender == getString(R.string.edit_female)) {
            binding.rbFemale.isChecked = true
        } else {
            binding.rbMale.isChecked = true
        }

        binding.btnSave.setOnClickListener {
            val newName = binding.etName.text.toString().trim()
            val newPhone = binding.etPhone.text.toString().trim()
            if (newName.isEmpty()) {
                Toast.makeText(this, R.string.edit_hint_name, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val newGender = if (binding.rbFemale.isChecked) {
                getString(R.string.edit_female)
            } else {
                getString(R.string.edit_male)
            }
            val intent = Intent().apply {
                putExtra(EXTRA_NAME, newName)
                putExtra(EXTRA_GENDER, newGender)
                putExtra(EXTRA_PHONE, newPhone)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        binding.btnCancel.setOnClickListener { finish() }
    }

    companion object {
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_GENDER = "extra_gender"
        const val EXTRA_PHONE = "extra_phone"
    }
}
