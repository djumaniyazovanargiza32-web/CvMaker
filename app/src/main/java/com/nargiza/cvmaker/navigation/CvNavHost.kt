package com.nargiza.cvmaker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nargiza.cvmaker.domain.repository.AiRepository
import com.nargiza.cvmaker.domain.repository.CvRepository
import com.nargiza.cvmaker.domain.repository.SettingsRepository
import com.nargiza.cvmaker.domain.usecase.GeneratePdfUseCase
import com.nargiza.cvmaker.domain.usecase.GetCvUseCase
import com.nargiza.cvmaker.domain.usecase.UpdateCvUseCase
import com.nargiza.cvmaker.feature.ai.AiAssistantScreen
import com.nargiza.cvmaker.feature.cveditor.CvEditorScreen
import com.nargiza.cvmaker.feature.cveditor.CvEditorViewModel
import com.nargiza.cvmaker.feature.home.HomeScreen
import com.nargiza.cvmaker.feature.home.HomeViewModel
import com.nargiza.cvmaker.feature.joboptimizer.JobOptimizerScreen
import com.nargiza.cvmaker.feature.onboarding.OnboardingScreen
import com.nargiza.cvmaker.feature.preview.PreviewScreen
import com.nargiza.cvmaker.feature.settings.SettingsScreen
import com.nargiza.cvmaker.feature.templates.TemplatesScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

object NavRoutes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val EDITOR = "editor/{cvId}?step={step}"
    const val PREVIEW = "preview/{cvId}"
    const val AI_ASSISTANT = "ai_assistant?cvId={cvId}"
    const val JOB_OPTIMIZER = "job_optimizer?cvId={cvId}"
    const val TEMPLATES = "templates"
    const val SETTINGS = "settings"

    fun editor(cvId: String, step: Int = 1) = "editor/$cvId?step=$step"
    fun preview(cvId: String) = "preview/$cvId"
    fun aiAssistant(cvId: String? = null) = if (cvId != null) "ai_assistant?cvId=$cvId" else "ai_assistant"
    fun jobOptimizer(cvId: String? = null) = if (cvId != null) "job_optimizer?cvId=$cvId" else "job_optimizer"
}

@Composable
fun CvNavHost(
    isOnboardingCompleted: Boolean,
    settingsRepository: SettingsRepository = koinInject()
) {
    val navController = rememberNavController()
    val startDestination = if (isOnboardingCompleted) NavRoutes.HOME else NavRoutes.ONBOARDING

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavRoutes.ONBOARDING) {
            OnboardingScreen(
                onFinish = {
                    CoroutineScope(Dispatchers.IO).launch {
                        settingsRepository.setOnboardingCompleted(true)
                    }
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.HOME) {
            val homeViewModel: HomeViewModel = koinViewModel()
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToEditor = { cvId, step -> navController.navigate(NavRoutes.editor(cvId, step)) },
                onNavigateToPreview = { cvId -> navController.navigate(NavRoutes.preview(cvId)) },
                onNavigateToAiAssistant = { navController.navigate(NavRoutes.aiAssistant()) },
                onNavigateToJobOptimizer = { cvId -> navController.navigate(NavRoutes.jobOptimizer(cvId)) },
                onNavigateToTemplates = { navController.navigate(NavRoutes.TEMPLATES) },
                onNavigateToSettings = { navController.navigate(NavRoutes.SETTINGS) }
            )
        }

        composable(
            route = NavRoutes.EDITOR,
            arguments = listOf(
                navArgument("cvId") { type = NavType.StringType },
                navArgument("step") {
                    type = NavType.IntType
                    defaultValue = 1
                }
            )
        ) { backStackEntry ->
            val cvId = backStackEntry.arguments?.getString("cvId").orEmpty()
            val step = backStackEntry.arguments?.getInt("step") ?: 1
            val editorViewModel: CvEditorViewModel = koinViewModel()

            CvEditorScreen(
                cvId = cvId,
                initialStep = step,
                viewModel = editorViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPreview = { id -> navController.navigate(NavRoutes.preview(id)) }
            )
        }

        composable(
            route = NavRoutes.PREVIEW,
            arguments = listOf(navArgument("cvId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cvId = backStackEntry.arguments?.getString("cvId").orEmpty()
            val getCvUseCase: GetCvUseCase = koinInject()
            val updateCvUseCase: UpdateCvUseCase = koinInject()
            val generatePdfUseCase: GeneratePdfUseCase = koinInject()

            PreviewScreen(
                cvId = cvId,
                getCvUseCase = getCvUseCase,
                updateCvUseCase = updateCvUseCase,
                generatePdfUseCase = generatePdfUseCase,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id -> navController.navigate(NavRoutes.editor(id)) },
                onNavigateToAi = { id -> navController.navigate(NavRoutes.aiAssistant(id)) }
            )
        }

        composable(
            route = NavRoutes.AI_ASSISTANT,
            arguments = listOf(
                navArgument("cvId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val cvId = backStackEntry.arguments?.getString("cvId")
            val cvRepository: CvRepository = koinInject()
            val aiRepository: AiRepository = koinInject()

            AiAssistantScreen(
                cvRepository = cvRepository,
                aiRepository = aiRepository,
                initialCvId = cvId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditor = { id -> navController.navigate(NavRoutes.editor(id)) }
            )
        }

        composable(
            route = NavRoutes.JOB_OPTIMIZER,
            arguments = listOf(
                navArgument("cvId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val cvId = backStackEntry.arguments?.getString("cvId")
            val cvRepository: CvRepository = koinInject()
            val aiRepository: AiRepository = koinInject()

            JobOptimizerScreen(
                cvRepository = cvRepository,
                aiRepository = aiRepository,
                initialCvId = cvId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TEMPLATES) {
            val settingsRepo: SettingsRepository = koinInject()
            TemplatesScreen(
                settingsRepository = settingsRepo,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.SETTINGS) {
            val settingsRepo: SettingsRepository = koinInject()
            SettingsScreen(
                settingsRepository = settingsRepo,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTemplates = { navController.navigate(NavRoutes.TEMPLATES) }
            )
        }
    }
}
