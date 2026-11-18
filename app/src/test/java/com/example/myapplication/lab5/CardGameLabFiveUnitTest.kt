package com.example.myapplication.lab5

import cn.edu.sicnu.cardgame.Card
import cn.edu.sicnu.cardgame.CardMatchingGame
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 实验五：模型层新增能力单元测试
 * 1) Card.match 支持三张牌匹配；
 * 2) CardMatchingGame 支持 3 张配对；
 * 3) CardMatchingGame.reset 重新发牌并清零分数。
 */
class CardGameLabFiveUnitTest {

    @Test
    fun card_match_threeSameRank_returnsTwelve() {
        val card = Card("♥", "A")
        assertEquals(12, card.match(arrayOf(Card("♦", "A"), Card("♠", "A"))))
    }

    @Test
    fun card_match_threeSameSuit_returnsThree() {
        val card = Card("♥", "A")
        assertEquals(3, card.match(arrayOf(Card("♥", "K"), Card("♥", "Q"))))
    }

    @Test
    fun card_match_threeNoMatch_returnsZero() {
        val card = Card("♥", "A")
        assertEquals(0, card.match(arrayOf(Card("♦", "K"), Card("♠", "Q"))))
    }

    /** 通过反射替换内部牌列表，构造确定性的三张同点数牌 */
    @Test
    fun game_threeCardMatch_scoresAndMarksMatched() {
        val game = CardMatchingGame(3, 3)
        replaceCards(game, listOf(Card("♥", "A"), Card("♦", "A"), Card("♠", "A")))

        game.chooseCardAtIndex(0)   // -1
        game.chooseCardAtIndex(1)   // -2
        game.chooseCardAtIndex(2)   // 三张同点数：+12*4 -1 = 45

        assertEquals(45, game.score)
        assertTrue(game.cardAtIndex(0).isMatched)
        assertTrue(game.cardAtIndex(1).isMatched)
        assertTrue(game.cardAtIndex(2).isMatched)
    }

    @Test
    fun game_reset_clearsScoreAndCards() {
        val game = CardMatchingGame(24)
        assertEquals(24, game.cardCount)
        game.chooseCardAtIndex(0)
        assertTrue(game.score != 0)

        game.reset()

        assertEquals(24, game.cardCount)
        assertEquals(0, game.score)
        assertFalse(game.cardAtIndex(0).isChosen)
        assertFalse(game.cardAtIndex(0).isMatched)
    }

    @Suppress("UNCHECKED_CAST")
    private fun replaceCards(game: CardMatchingGame, newCards: List<Card>) {
        val field = CardMatchingGame::class.java.getDeclaredField("cards")
        field.isAccessible = true
        val cards = field.get(game) as MutableList<Card>
        cards.clear()
        cards.addAll(newCards)
    }
}
