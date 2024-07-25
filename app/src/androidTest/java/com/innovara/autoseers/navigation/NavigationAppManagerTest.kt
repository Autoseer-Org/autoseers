package com.innovara.autoseers.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.innovara.autoseers.AuthState
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.innovara.autoseers.R
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationAppManagerTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var navController: TestNavHostController
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setup() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavigationAppManager(
                navController = navController,
                authState = AuthState.NotAuthenticated
            )
        }
    }

    @Test
    fun when_app_starts_then_initial_screen_should_be_Login() {
        composeTestRule
            .onNodeWithText(context.getString(R.string.title_app_name))
            .assertExists()
    }
}