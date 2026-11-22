package com.example.myapplication.lab6.model

import java.io.Serializable

/**
 * 实验六：个人信息数据模型。
 * 实现 Serializable，可通过 ObjectOutputStream/ObjectInputStream
 * 配合 openFileOutput/openFileInput 序列化到本地文件，退出程序后再次打开可恢复。
 */
data class Profile(
    var name: String = "",
    var gender: String = "",
    var studentId: String = "",
    var phone: String = "",
    var bestTwoScore: Int = 0,
    var bestThreeScore: Int = 0
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
