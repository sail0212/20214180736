package cn.edu.sicnu.cardgame

import java.io.Serializable

/** 实验六：实现 Serializable，使牌面对象可随游戏状态一起写入本地文件 */
class Card(
    private var suit: String,
    private var rank: String,
    var isChosen: Boolean = false,
    var isMatched: Boolean = false
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
        val rankStrings = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
        val validSuits = arrayOf("♥", "♦", "♠", "♣")
    }

    override fun toString(): String {
        return suit + rank
    }

    /**
     * 实验五：扩展为同时支持 2 张牌和 3 张牌的匹配判断。
     * otherCards.size == 1 时保持实验四规则：同点数 4 分、同花色 1 分；
     * otherCards.size == 2 时用于三张牌游戏：三张同点数 12 分、三张同花色 3 分，否则 0 分。
     */
    fun match(otherCards: Array<Card>): Int {
        return when (otherCards.size) {
            1 -> {
                val otherCard = otherCards[0]
                if (otherCard.rank == rank) {
                    4
                } else if (otherCard.suit == suit) {
                    1
                } else {
                    0
                }
            }
            2 -> {
                val first = otherCards[0]
                val second = otherCards[1]
                if (first.rank == rank && second.rank == rank) {
                    12
                } else if (first.suit == suit && second.suit == suit) {
                    3
                } else {
                    0
                }
            }
            else -> 0
        }
    }
}
