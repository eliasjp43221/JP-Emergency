package com.example

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.database.JamalpurDatabase
import com.example.repositories.JamalpurRepository
import com.example.utils.IntentActionHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var db: JamalpurDatabase
    private lateinit var repository: JamalpurRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, JamalpurDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = JamalpurRepository(
            serviceDao = db.serviceDao(),
            bloodDonorDao = db.bloodDonorDao(),
            issueReportDao = db.issueReportDao()
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `app name resource is Bengali Jamalpur Sheba`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("জামালপুর সেবা", appName)
    }

    @Test
    fun `offline room database populates seed data and persists favorites`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        repository.ensureSeedDataPopulated(context)

        val allServices = repository.allServices.first()
        assertTrue("Expected offline services to be populated", allServices.size >= 25)

        val target = allServices.first { it.id == "hosp_jamalpur_250_general" }
        repository.toggleFavorite(target.id, target.isFavorite)

        val favorites = repository.favoriteServices.first()
        assertEquals(1, favorites.size)
        assertEquals("hosp_jamalpur_250_general", favorites.first().id)
    }

    @Test
    fun `calling and map intents are created accurately`() = runBlocking {
        val dialIntent999 = IntentActionHelper.createDialIntent("999")
        assertNotNull(dialIntent999)
        assertEquals(Intent.ACTION_DIAL, dialIntent999?.action)
        assertEquals("tel:999", dialIntent999?.dataString)

        val dialIntentBn = IntentActionHelper.createDialIntent("০১৩২০-১০৫১৭৯")
        assertNotNull(dialIntentBn)
        assertEquals("tel:01320105179", dialIntentBn?.dataString)

        val invalidDial = IntentActionHelper.createDialIntent("")
        assertNull(invalidDial)

        repository.ensureSeedDataPopulated()
        val entry = repository.getServiceById("hosp_jamalpur_250_general")!!
        val mapIntent = IntentActionHelper.createMapIntent(entry)
        assertEquals(Intent.ACTION_VIEW, mapIntent.action)
        assertTrue(mapIntent.dataString.orEmpty().startsWith("geo:"))
    }

    @Test
    fun `home screen renders Bengali UI, search bar, emergency actions, and navigation`() {
        composeTestRule.waitForIdle()

        // Verify Header & Search bar
        composeTestRule.onNodeWithText("জামালপুর সেবা").assertIsDisplayed()
        composeTestRule.onNodeWithTag("home_search_input").assertIsDisplayed()

        composeTestRule.onNodeWithTag("home_screen_list")
            .performScrollToNode(hasTestTag("quick_call_999"))
        composeTestRule.onNodeWithTag("quick_call_999").assertIsDisplayed()
        composeTestRule.onNodeWithTag("quick_call_102").assertIsDisplayed()

        // Scroll back to search bar and perform live search on Home Screen
        composeTestRule.onNodeWithTag("home_screen_list")
            .performScrollToNode(hasTestTag("home_search_input"))
        composeTestRule.onNodeWithTag("home_search_input")
            .performTextInput("মেলান্দহ থানা")

        // Wait for async Room seed population + flow emission
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("reset_home_search_button")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Scroll to the matching result card inside LazyColumn and verify it is displayed
        composeTestRule.onNodeWithTag("home_screen_list")
            .performScrollToNode(hasTestTag("service_card_pol_melandaha_thana"))
        composeTestRule.onNodeWithTag("service_card_pol_melandaha_thana").assertIsDisplayed()

        // Clear search
        composeTestRule.onNodeWithTag("clear_search_button").performClick()
        composeTestRule.waitForIdle()

        // Navigate to Services Directory tab
        composeTestRule.onNodeWithTag("nav_tab_services").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("সকল সেবা ডিরেক্টরি").assertIsDisplayed()

        // Navigate to Favorites tab
        composeTestRule.onNodeWithTag("nav_tab_favorites").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("প্রিয় ও সংরক্ষিত সেবাসমূহ").assertIsDisplayed()

        // Navigate to Settings tab
        composeTestRule.onNodeWithTag("nav_tab_settings").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("সেটিংস, ডাটা আপডেট ও পরিচিতি").assertIsDisplayed()
    }
}
