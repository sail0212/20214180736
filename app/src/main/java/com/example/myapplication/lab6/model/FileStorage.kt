package com.example.myapplication.lab6.model

import android.content.Context
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * 实验六：对象序列化 + 内部文件存储工具类。
 * 写文件用 openFileOutput，读文件用 openFileInput；
 * 写对象用 ObjectOutputStream，读对象用 ObjectInputStream，
 * 被读写对象均需实现 Serializable。
 */
object FileStorage {

    /** 把 Serializable 对象写入应用私有目录文件 */
    fun save(context: Context, fileName: String, obj: Serializable) {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { fos ->
            ObjectOutputStream(fos).use { it.writeObject(obj) }
        }
    }

    /** 从应用私有目录文件读回对象；文件不存在或损坏时返回 null */
    fun <T : Serializable> load(context: Context, fileName: String, clazz: Class<T>): T? {
        return try {
            context.openFileInput(fileName).use { fis ->
                ObjectInputStream(fis).use { ois -> clazz.cast(ois.readObject()) }
            }
        } catch (e: Exception) {
            null
        }
    }
}
