package com.example.myapplication.lab4

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matchers.containsString
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 实验四：主界面 UI 测试（仪器化测试，运行在设备/模拟器上）
 * 验证主Activity启动后玩家信息、两个入口按钮是否正确显示。
 */
@RunWith(AndroidJUnit4::class)
class LabFourUiTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LabFourActivity::class.java)

    @Test
    fun playerInfo_isDisplayed() {
        // 姓名、性别、学号、电话、最高分标签均显示
        onView(withId(R.id.tvName)).check(matches(isDisplayed()))
        onView(withId(R.id.tvGender)).check(matches(isDisplayed()))
        onView(withId(R.id.tvStudentId)).check(matches(isDisplayed()))
        onView(withId(R.id.tvPhone)).check(matches(isDisplayed()))
        onView(withId(R.id.tvBestScore)).check(matches(isDisplayed()))
    }

    @Test
    fun entryButtons_areDisplayed() {
        onView(withId(R.id.btnEditInfo)).check(matches(isDisplayed()))
        onView(withId(R.id.btnPlayGame)).check(matches(isDisplayed()))
    }

    @Test
    fun defaultPlayerName_isShown() {
        // 姓名由"姓名："标签 + 玩家名拼接而成，用包含匹配验证默认值显示
        onView(withId(R.id.tvName)).check(matches(withText(containsString("向泊帆"))))
    }
}
