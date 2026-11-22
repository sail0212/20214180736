package com.example.myapplication.lab6

import com.example.myapplication.R
import com.example.myapplication.lab6.model.Profile

/** 实验六第 2 个 Fragment：2 张牌翻牌游戏 */
class TwoCardGameFragment : CardGameFragment() {
    override val matchCount: Int = 2
    override val saveFileName: String = "game_two_cards.dat"
    override val titleRes: Int = R.string.lab6_two_card_title

    override fun applyBestScore(profile: Profile, score: Int) {
        if (score > profile.bestTwoScore) profile.bestTwoScore = score
    }
}
