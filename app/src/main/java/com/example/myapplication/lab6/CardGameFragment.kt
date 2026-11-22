package com.example.myapplication.lab6

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import cn.edu.sicnu.cardgame.CardMatchingGame
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentCardGameBinding
import com.example.myapplication.lab4.CardAdapter
import com.example.myapplication.lab6.model.FileStorage
import com.example.myapplication.lab6.model.Profile

/**
 * 实验六：翻牌游戏 Fragment 基类（第 2、3 个 Fragment 共用）。
 * - Model：CardMatchingGame（实现 Serializable）
 * - View：fragment_card_game.xml + item_card.xml，RecyclerView 呈现
 * - 序列化：整局游戏对象经 ObjectOutputStream 写入私有文件，
 *   下次进入（或退出程序重开）时用 ObjectInputStream 恢复历史状态。
 */
abstract class CardGameFragment : Fragment() {

    /** 每次配对需要的牌数：2 或 3 */
    protected abstract val matchCount: Int
    /** 本 Fragment 对应的存档文件名 */
    protected abstract val saveFileName: String
    /** 标题字符串资源 */
    protected abstract val titleRes: Int
    /** 更新到 Profile 中最高分的字段选择 */
    protected abstract fun applyBestScore(profile: Profile, score: Int)

    private var _binding: FragmentCardGameBinding? = null
    private val binding get() = _binding!!

    private lateinit var game: CardMatchingGame
    private lateinit var adapter: CardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvGameTitle.setText(titleRes)

        // 序列化恢复：若本地存档存在且规则一致则恢复，否则新建一局
        val restored = FileStorage.load(requireContext(), saveFileName, CardMatchingGame::class.java)
        game = if (restored != null && restored.matchingCards == matchCount && restored.cardCount > 0) {
            restored
        } else {
            CardMatchingGame(CARD_COUNT, matchCount)
        }

        adapter = CardAdapter(game, SPAN_COUNT) { index ->
            game.chooseCardAtIndex(index)
            updateUI()
            saveGame()
        }
        binding.rvCards.layoutManager = GridLayoutManager(requireContext(), SPAN_COUNT)
        binding.rvCards.adapter = adapter

        binding.btnReset.setOnClickListener {
            game.reset()
            updateUI()
            saveGame()
        }
        updateUI()
    }

    /** 点击/重置后刷新视图，并把更高分写回个人信息存档 */
    private fun updateUI() {
        binding.tvScore.text = getString(R.string.game_score, game.score)
        binding.tvModeHint.text = getString(R.string.lab6_mode_hint, matchCount)
        val profile = FileStorage.load(requireContext(), LabSixActivity.PROFILE_FILE, Profile::class.java)
            ?: Profile()
        applyBestScore(profile, game.score)
        FileStorage.save(requireContext(), LabSixActivity.PROFILE_FILE, profile)
        adapter.refreshAll()
    }

    /** 把当前整局游戏对象序列化到本地文件 */
    private fun saveGame() {
        FileStorage.save(requireContext(), saveFileName, game)
    }

    /** Fragment 被切走/销毁时保存一次，确保历史状态不丢 */
    override fun onDestroyView() {
        if (::game.isInitialized) saveGame()
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val CARD_COUNT = 24
        private const val SPAN_COUNT = 4
    }
}
