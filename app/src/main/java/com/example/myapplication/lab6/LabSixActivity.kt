package com.example.myapplication.lab6

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityLabSixBinding

/**
 * 实验六：多 Fragment 应用主容器（本次作业启动页）。
 * 底部三个标签分别切换「个人信息 / 2 张牌翻牌 / 3 张牌翻牌」三个 Fragment，
 * 各 Fragment 通过序列化把历史状态写入私有文件，退出程序重开可恢复。
 */
class LabSixActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLabSixBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLabSixBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(R.string.app_name)

        if (savedInstanceState == null) {
            switchFragment(ProfileFragment())
        }

        binding.btnTabProfile.setOnClickListener { switchFragment(ProfileFragment()) }
        binding.btnTabTwo.setOnClickListener { switchFragment(TwoCardGameFragment()) }
        binding.btnTabThree.setOnClickListener { switchFragment(ThreeCardGameFragment()) }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // ==================== 关于对话框（沿用实验五） ====================
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.about_title)
                    .setMessage(R.string.lab6_about_message)
                    .setPositiveButton(R.string.about_ok, null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val PROFILE_FILE = "lab6_profile.dat"
    }
}
