package cn.edu.sicnu.cardgame

import java.io.Serializable

/**
 * 实验五：在实验四模型基础上增加 reset() 与三张牌匹配规则。
 * 实验六：实现 Serializable，支持把整局游戏状态序列化到本地文件，退出重开可恢复。
 * @param totalCards 本局牌数
 * @param matchCount 每次配对需要的牌数，2 或 3
 */
class CardMatchingGame(
    private val totalCards: Int,
    private val matchCount: Int = 2
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }

    var score = 0
        private set

    private val cards: MutableList<Card> = mutableListOf()

    init {
        reset()
    }

    /** 实验五新增：重新发牌并清零分数，保持当前配对规则不变 */
    fun reset() {
        score = 0
        cards.clear()
        val deck = Deck()
        repeat(totalCards) {
            deck.drawRandomCard()?.let { cards.add(it) }
        }
    }

    fun cardAtIndex(index: Int): Card = cards[index]

    val cardCount: Int
        get() = cards.size

    val matchingCards: Int
        get() = matchCount

    val MISMATCH_PENALTY = 2
    val MATCH_BONUS = 4
    val COST_TO_CHOOSE = 1

    /**
     * 实验五修改：根据 matchCount 决定取几张已翻开的牌参与比较，
     * 具体匹配得分仍交给 Card.match 计算，模型其余部分不变。
     */
    fun chooseCardAtIndex(index: Int) {
        val card = cardAtIndex(index)
        if (card.isMatched) return

        if (card.isChosen) {
            card.isChosen = false
            return
        }

        val chosen = cards.filter { it.isChosen && !it.isMatched }
        if (chosen.size >= matchCount - 1) {
            val others = chosen.take(matchCount - 1).toTypedArray()
            val matchScore = card.match(others)
            if (matchScore > 0) {
                score += matchScore * MATCH_BONUS
                others.forEach {
                    it.isMatched = true
                    it.isChosen = false
                }
                card.isMatched = true
                card.isChosen = false
            } else {
                score -= MISMATCH_PENALTY
                others.forEach { it.isChosen = false }
                card.isChosen = true
            }
        } else {
            card.isChosen = true
        }
        score -= COST_TO_CHOOSE
    }
}
