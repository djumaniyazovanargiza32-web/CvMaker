package com.nargiza.cvmaker.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nargiza.cvmaker.core.designsystem.CvEmptyState
import com.nargiza.cvmaker.core.designsystem.CvPrimaryButton
import com.nargiza.cvmaker.core.designsystem.CvTemplateCard
import com.nargiza.cvmaker.core.designsystem.CvTextField
import com.nargiza.cvmaker.core.designsystem.CvmakerTheme
import com.nargiza.cvmaker.core.model.TemplateType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CvComponentsUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun primaryButtonDisplaysTextAndRespondsToClick() {
        var clicked = false

        composeTestRule.setContent {
            CvmakerTheme {
                CvPrimaryButton(
                    text = "Save and Continue",
                    onClick = { clicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Save and Continue")
            .assertIsDisplayed()
            .performClick()

        assertTrue(clicked)
    }

    @Test
    fun textFieldUpdatesTextAndDisplaysError() {
        composeTestRule.setContent {
            var text by remember { mutableStateOf("") }
            CvmakerTheme {
                CvTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = "Full Name",
                    error = if (text.isEmpty()) "Name is required" else null
                )
            }
        }

        composeTestRule.onNodeWithText("Full Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Name is required").assertIsDisplayed()

        composeTestRule.onNodeWithText("Full Name").performTextInput("John Doe")
        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
    }

    @Test
    fun emptyStateRendersTitleMessageAndAction() {
        var actionTriggered = false

        composeTestRule.setContent {
            CvmakerTheme {
                CvEmptyState(
                    title = "No CVs Found",
                    description = "Create a new CV to get started.",
                    primaryActionText = "Create Now",
                    onPrimaryAction = { actionTriggered = true }
                )
            }
        }

        composeTestRule.onNodeWithText("No CVs Found").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create a new CV to get started.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create Now")
            .assertIsDisplayed()
            .performClick()

        assertTrue(actionTriggered)
    }

    @Test
    fun templateCardSelectionWorks() {
        var selectedType: TemplateType? = null

        composeTestRule.setContent {
            CvmakerTheme {
                CvTemplateCard(
                    template = TemplateType.MODERN,
                    isSelected = selectedType == TemplateType.MODERN,
                    onSelect = { selectedType = TemplateType.MODERN }
                )
            }
        }

        composeTestRule.onNodeWithText(TemplateType.MODERN.title)
            .assertIsDisplayed()
            .performClick()

        assertEquals(TemplateType.MODERN, selectedType)
    }
}
