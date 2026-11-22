package com.example.myapplication.lab6

import cn.edu.sicnu.cardgame.CardMatchingGame
import com.example.myapplication.lab6.model.Profile
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * 实验六：对象序列化单元测试。
 * 使用内存字节流模拟 ObjectOutputStream/ObjectInputStream 的读写往返，
 * 验证 Profile 与 CardMatchingGame 实现 Serializable 后可正确保存与恢复。
 */
class LabSixSerializationTest {

    /** 对象 -> 字节 -> 对象 的通用往返 */
    private fun <T : java.io.Serializable> roundTrip(obj: T): T {
        val bos = ByteArrayOutputStream()
        ObjectOutputStream(bos).use { it.writeObject(obj) }
        ObjectInputStream(ByteArrayInputStream(bos.toByteArray())).use {
            @Suppress("UNCHECKED_CAST")
            return it.readObject() as T
        }
    }

    @Test
    fun profile_serializable_roundTripKeepsFields() {
        val original = Profile("向泊帆", "男", "2024180736", "13800000000", 12, 45)
        val restored = roundTrip(original)
        assertEquals(original, restored)
        assertEquals("向泊帆", restored.name)
        assertEquals(12, restored.bestTwoScore)
        assertEquals(45, restored.bestThreeScore)
    }

    @Test
    fun game_serializable_roundTripKeepsScoreAndCards() {
        val game = CardMatchingGame(24, 2)
        game.chooseCardAtIndex(0)
        val originalScore = game.score
        val restored = roundTrip(game)
        assertEquals(originalScore, restored.score)
        assertEquals(24, restored.cardCount)
        assertEquals(2, restored.matchingCards)
    }

    @Test
    fun game_threeCardMode_serializableKeepsMatchCount() {
        val game = CardMatchingGame(24, 3)
        val restored = roundTrip(game)
        assertEquals(3, restored.matchingCards)
        assertEquals(24, restored.cardCount)
    }
}
