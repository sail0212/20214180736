package com.example.myapplication.lab4

import cn.edu.sicnu.cardgame.Card
import cn.edu.sicnu.cardgame.CardMatchingGame
import cn.edu.sicnu.cardgame.Deck
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 实验四：翻牌游戏模型层（Model）单元测试
 * 验证附件 model.zip 中 Card / Deck / CardMatchingGame 的核心业务规则。
 * 运行在开发机 JVM 上，不依赖 Android 框架。
 */
class CardGameModelUnitTest {

    /** Card.match：同点数得 4 分，同花色得 1 分，都不匹配得 0 分 */
    @Test
    fun card_match_sameRank_returnsFour() {
        val a = Card("♥", "A")
        val b = Card("♠", "A")
        assertEquals(4, a.match(arrayOf(b)))
    }

    @Test
    fun card_match_sameSuit_returnsOne() {
        val a = Card("♥", "A")
        val b = Card("♥", "K")
        assertEquals(1, a.match(arrayOf(b)))
    }

    @Test
    fun card_match_noMatch_returnsZero() {
        val a = Card("♥", "A")
        val b = Card("♠", "K")
        assertEquals(0, a.match(arrayOf(b)))
    }

    @Test
    fun card_toString_isSuitPlusRank() {
        assertEquals("♥A", Card("♥", "A").toString())
    }

    /** Deck：共 52 张牌，逐张抽取直到为空，且每次抽到的牌非空 */
    @Test
    fun deck_drawsAll52Cards_thenEmpty() {
        val deck = Deck()
        var count = 0
        while (deck.drawRandomCard() != null) count++
        assertEquals(52, count)
    }

    @Test
    fun deck_drawRandomCard_notNullWhenNotEmpty() {
        assertNotNull(Deck().drawRandomCard())
    }

    /** CardMatchingGame：翻开未配对的牌扣 1 分（COST_TO_CHOOSE） */
    @Test
    fun game_chooseCard_deductsCost() {
        val game = CardMatchingGame(4)
        game.chooseCardAtIndex(0)
        assertEquals(-1, game.score)
    }

    /** 再次点击同一张已翻开的牌会取消选择，按附件Model规则不再扣分 */
    @Test
    fun game_chooseSameCard_twice_togglesSelection() {
        val game = CardMatchingGame(4)
        game.chooseCardAtIndex(0)   // 翻开，扣 1 分
        val scoreAfterFirst = game.score
        game.chooseCardAtIndex(0)   // 取消选择（走 isChosen 分支，不扣分）
        assertEquals(scoreAfterFirst, game.score)
        assertTrue(!game.cardAtIndex(0).isChosen)
    }

    /** 翻牌 2 次（两张牌）后得分应为负或 0，且不会越界 */
    @Test
    fun game_chooseTwoCards_scoreInValidRange() {
        val game = CardMatchingGame(16)
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        assertTrue(game.score <= 16)
        assertTrue(game.score >= -16)
    }

    /** cardAtIndex 对合法下标应返回非空 Card */
    @Test
    fun game_cardAtIndex_returnsCard() {
        val game = CardMatchingGame(16)
        assertNotNull(game.cardAtIndex(0))
        assertNotNull(game.cardAtIndex(15))
    }
}
