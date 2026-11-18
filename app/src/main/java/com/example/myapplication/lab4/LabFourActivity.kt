package com.example.myapplication.lab4

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityLabFourBinding

/**
 * 实验四：翻牌游戏应用 —— 主Activity（启动页）
 * 显示当前玩家基本信息（姓名、性别、学号、电话、当前最高分），
 * 并提供"修改个人信息"与"翻牌游戏"两个入口。
 * 按实验要求，修改后的信息与最高分仅在本次运行期间通过 Intent 在内存传递，不保存到文件。
 */
class LabFourActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLabFourBinding

    private var name: String = ""
    private var gender: String = ""
    private var studentId: String = ""
    private var phone: String = ""
    private var bestScore: Int = 0

    // 接收信息修改页回传的玩家信息
    private val editLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data ?: return@registerForActivityResult
                name = data.getStringExtra(InfoEditActivity.EXTRA_NAME).orEmpty().ifEmpty { name }
                gender = data.getStringExtra(InfoEditActivity.EXTRA_GENDER).orEmpty().ifEmpty { gender }
                phone = data.getStringExtra(InfoEditActivity.EXTRA_PHONE).orEmpty().ifEmpty { phone }
                render()
            }
        }

    // 接收翻牌游戏页回传的本次得分，最高分在内存中更新
    private val gameLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val score = result.data?.getIntExtra(CardGameActivity.EXTRA_SCORE, 0) ?: 0
                if (score > bestScore) bestScore = score
                render()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLabFourBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = getString(R.string.lab4_app_title)

        // 横竖屏重建时恢复内存中的玩家状态
        if (savedInstanceState != null) {
            name = savedInstanceState.getString(KEY_NAME).orEmpty()
            gender = savedInstanceState.getString(KEY_GENDER).orEmpty()
            studentId = savedInstanceState.getString(KEY_STUDENT_ID).orEmpty()
            phone = savedInstanceState.getString(KEY_PHONE).orEmpty()
            bestScore = savedInstanceState.getInt(KEY_BEST_SCORE, 0)
        } else {
            initPlayerInfo()
        }
        render()

        binding.btnEditInfo.setOnClickListener {
            val intent = Intent(this, InfoEditActivity::class.java).apply {
                putExtra(InfoEditActivity.EXTRA_NAME, name)
                putExtra(InfoEditActivity.EXTRA_GENDER, gender)
                putExtra(InfoEditActivity.EXTRA_PHONE, phone)
            }
            editLauncher.launch(intent)
        }

        binding.btnPlayGame.setOnClickListener {
            val intent = Intent(this, CardGameActivity::class.java).apply {
                putExtra(EXTRA_NAME, name)
                putExtra(EXTRA_STUDENT_ID, studentId)
                putExtra(EXTRA_BEST_SCORE, bestScore)
            }
            gameLauncher.launch(intent)
        }

        // 附加：跳转到实验三作品
        binding.btnToLab3.setOnClickListener {
            startActivity(Intent(this, com.example.myapplication.lab3.LabThreeActivity::class.java))
        }
    }

    private fun initPlayerInfo() {
        name = getString(R.string.p_default_name)
        gender = getString(R.string.p_default_gender)
        studentId = getString(R.string.p_default_student_id)
        phone = getString(R.string.p_default_phone)
        bestScore = getString(R.string.p_best_score_initial).toIntOrNull() ?: 0
    }

    private fun render() {
        binding.tvName.text = getString(R.string.p_label_name) + name
        binding.tvGender.text = getString(R.string.p_label_gender) + gender
        binding.tvStudentId.text = getString(R.string.p_label_student_id) + studentId
        binding.tvPhone.text = getString(R.string.p_label_phone) + phone
        binding.tvBestScore.text = getString(R.string.p_label_best_score) + bestScore
        binding.tvFooter.text = name + getString(R.string.p_footer)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_NAME, name)
        outState.putString(KEY_GENDER, gender)
        outState.putString(KEY_STUDENT_ID, studentId)
        outState.putString(KEY_PHONE, phone)
        outState.putInt(KEY_BEST_SCORE, bestScore)
    }

    // ==================== 实验五：主Activity菜单与关于对话框 ====================
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.about_title)
                    .setMessage(R.string.about_message)
                    .setPositiveButton(R.string.about_ok, null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_STUDENT_ID = "extra_student_id"
        const val EXTRA_BEST_SCORE = "extra_best_score"
        private const val KEY_NAME = "key_name"
        private const val KEY_GENDER = "key_gender"
        private const val KEY_STUDENT_ID = "key_student_id"
        private const val KEY_PHONE = "key_phone"
        private const val KEY_BEST_SCORE = "key_best_score"
    }
}
