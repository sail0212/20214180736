package com.example.myapplication.lab6

import com.example.myapplication.R
import com.example.myapplication.lab6.model.Profile

/** 实验六第 3 个 Fragment：3 张牌翻牌游戏 */
class ThreeCardGameFragment : CardGameFragment() {
    override val matchCount: Int = 3
    override val saveFileName: String = "game_three_cards.dat"
    override val titleRes: Int = R.string.lab6_three_card_title

    override fun applyBestScore(profile: Profile, score: Int) {
        if (score > profile.bestThreeScore) profile.bestThreeScore = score
    }
}
