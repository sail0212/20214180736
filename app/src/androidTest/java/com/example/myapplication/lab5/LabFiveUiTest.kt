package com.example.myapplication.lab5

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.R
import com.example.myapplication.lab4.CardGameActivity
import org.hamcrest.Matchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 实验五：RecyclerView 翻牌游戏 UI 测试（仪器化测试）
 * 验证游戏页面、规则提示和横竖屏牌数提示正确显示。
 */
@RunWith(AndroidJUnit4::class)
class LabFiveUiTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(CardGameActivity::class.java)

    @Test
    fun gameScreen_isDisplayed() {
        onView(withId(R.id.rvCards)).check(matches(isDisplayed()))
        onView(withId(R.id.btnMode)).check(matches(isDisplayed()))
        onView(withId(R.id.btnReset)).check(matches(isDisplayed()))
    }

    @Test
    fun matchModeHint_isShown() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val expected = context.getString(R.string.game_match_mode, 2)
        onView(withId(R.id.tvMatchMode)).check(matches(withText(expected)))
        onView(withId(R.id.tvOrientation)).check(matches(withText(containsString("24"))))
    }
}
