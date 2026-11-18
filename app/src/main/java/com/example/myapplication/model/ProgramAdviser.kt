package com.example.myapplication.model

import androidx.annotation.StringRes
import com.example.myapplication.R

/**
 * MVC —— 模型层（Model）
 *
 * 学习方向：用户在界面上选择的输入，由控制器读取后交给模型查询
 */
enum class DevGoal(@param:StringRes val labelRes: Int) {
    ANDROID(R.string.zone2_option_android),
    WEB(R.string.zone2_option_web),
    GAME(R.string.zone2_option_game),
    DATA(R.string.zone2_option_data),
    SCRIPT(R.string.zone2_option_script)
}

/**
 * MVC —— 模型层返回的学习建议
 *
 * 文案统一使用字符串资源 ID，由控制器解析后显示到视图，避免出现硬编码字符串
 */
data class Advice(
    @param:StringRes val languageRes: Int,
    @param:StringRes val reasonRes: Int,
    @param:StringRes val stepsRes: Int
)

/**
 * MVC —— 模型层：程序学习顾问（ProgramAdviser）
 *
 * 只包含数据与业务逻辑，不持有任何视图引用；
 * 控制器（Activity）在响应按钮事件时查询本模型，并返回相应结果。
 */
class ProgramAdviser {

    /** 根据用户选择的学习方向查询模型，返回对应的学习建议 */
    fun advise(goal: DevGoal): Advice = when (goal) {
        DevGoal.ANDROID -> Advice(
            languageRes = R.string.advice_android_language,
            reasonRes = R.string.advice_android_reason,
            stepsRes = R.string.advice_android_steps
        )
        DevGoal.WEB -> Advice(
            languageRes = R.string.advice_web_language,
            reasonRes = R.string.advice_web_reason,
            stepsRes = R.string.advice_web_steps
        )
        DevGoal.GAME -> Advice(
            languageRes = R.string.advice_game_language,
            reasonRes = R.string.advice_game_reason,
            stepsRes = R.string.advice_game_steps
        )
        DevGoal.DATA -> Advice(
            languageRes = R.string.advice_data_language,
            reasonRes = R.string.advice_data_reason,
            stepsRes = R.string.advice_data_steps
        )
        DevGoal.SCRIPT -> Advice(
            languageRes = R.string.advice_script_language,
            reasonRes = R.string.advice_script_reason,
            stepsRes = R.string.advice_script_steps
        )
    }
}
