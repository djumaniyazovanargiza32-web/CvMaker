package com.nargiza.cvmaker.di

import androidx.room.Room
import com.nargiza.cvmaker.core.common.AndroidAppLogger
import com.nargiza.cvmaker.core.common.AppLogger
import com.nargiza.cvmaker.core.common.DefaultDispatchersProvider
import com.nargiza.cvmaker.core.common.DispatchersProvider
import com.nargiza.cvmaker.core.database.CvDatabase
import com.nargiza.cvmaker.core.database.preferences.UserPreferencesDataStore
import com.nargiza.cvmaker.core.network.KtorAiClient
import com.nargiza.cvmaker.core.pdf.PdfExporter
import com.nargiza.cvmaker.core.pdf.PdfGenerator
import com.nargiza.cvmaker.data.repository.AiRepositoryImpl
import com.nargiza.cvmaker.data.repository.CvRepositoryImpl
import com.nargiza.cvmaker.data.repository.SettingsRepositoryImpl
import com.nargiza.cvmaker.domain.repository.AiRepository
import com.nargiza.cvmaker.domain.repository.CvRepository
import com.nargiza.cvmaker.domain.repository.SettingsRepository
import com.nargiza.cvmaker.domain.usecase.AnalyzeJobDescriptionUseCase
import com.nargiza.cvmaker.domain.usecase.CreateCvUseCase
import com.nargiza.cvmaker.domain.usecase.DeleteCvUseCase
import com.nargiza.cvmaker.domain.usecase.DuplicateCvUseCase
import com.nargiza.cvmaker.domain.usecase.GenerateAchievementUseCase
import com.nargiza.cvmaker.domain.usecase.GenerateFullCvUseCase
import com.nargiza.cvmaker.domain.usecase.GeneratePdfUseCase
import com.nargiza.cvmaker.domain.usecase.GenerateSummaryUseCase
import com.nargiza.cvmaker.domain.usecase.GetCvUseCase
import com.nargiza.cvmaker.domain.usecase.ImproveExperienceUseCase
import com.nargiza.cvmaker.domain.usecase.ImproveProjectUseCase
import com.nargiza.cvmaker.domain.usecase.ObserveCvUseCase
import com.nargiza.cvmaker.domain.usecase.ObserveCvsUseCase
import com.nargiza.cvmaker.domain.usecase.SearchCvsUseCase
import com.nargiza.cvmaker.domain.usecase.UpdateCvUseCase
import com.nargiza.cvmaker.feature.cveditor.CvEditorViewModel
import com.nargiza.cvmaker.feature.home.HomeViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            CvDatabase::class.java,
            "cv_maker.db"
        ).fallbackToDestructiveMigration()
            .build()
    }
    single { get<CvDatabase>().cvDao() }
    single { UserPreferencesDataStore(androidContext()) }
}

val networkModule = module {
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        encodeDefaults = true
                    }
                )
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 60_000
                connectTimeoutMillis = 30_000
                socketTimeoutMillis = 60_000
            }
            install(Logging) {
                level = LogLevel.INFO
            }
        }
    }
    single { KtorAiClient(httpClient = get(), preferencesDataStore = get(), logger = get()) }
}

val pdfModule = module {
    single { PdfGenerator(androidContext()) }
    single { PdfExporter(androidContext()) }
}

val appModule = module {
    single<DispatchersProvider> { DefaultDispatchersProvider() }
    single<AppLogger> { AndroidAppLogger() }
}

val repositoryModule = module {
    single<CvRepository> { CvRepositoryImpl(cvDao = get(), dispatchers = get()) }
    single<AiRepository> { AiRepositoryImpl(client = get(), dispatchers = get()) }
    single<SettingsRepository> {
        SettingsRepositoryImpl(
            dataStore = get(),
            cvDao = get(),
            dispatchers = get()
        )
    }
}

val useCaseModule = module {
    single { CreateCvUseCase(repository = get()) }
    single { GetCvUseCase(repository = get()) }
    single { ObserveCvsUseCase(repository = get()) }
    single { ObserveCvUseCase(repository = get()) }
    single { SearchCvsUseCase(repository = get()) }
    single { UpdateCvUseCase(repository = get()) }
    single { DeleteCvUseCase(repository = get()) }
    single { DuplicateCvUseCase(repository = get()) }
    single { GenerateSummaryUseCase(aiRepository = get()) }
    single { ImproveExperienceUseCase(aiRepository = get()) }
    single { GenerateAchievementUseCase(aiRepository = get()) }
    single { ImproveProjectUseCase(aiRepository = get()) }
    single { GenerateFullCvUseCase(aiRepository = get()) }
    single { AnalyzeJobDescriptionUseCase(aiRepository = get()) }
    single {
        GeneratePdfUseCase(
            pdfGenerator = get(),
            pdfExporter = get(),
            dispatchers = get()
        )
    }
}

val viewModelModule = module {
    viewModel {
        HomeViewModel(
            observeCvsUseCase = get(),
            createCvUseCase = get(),
            duplicateCvUseCase = get(),
            deleteCvUseCase = get(),
            updateCvUseCase = get(),
            generatePdfUseCase = get()
        )
    }
    viewModel {
        CvEditorViewModel(
            getCvUseCase = get(),
            updateCvUseCase = get(),
            generateSummaryUseCase = get(),
            improveExperienceUseCase = get(),
            improveProjectUseCase = get(),
            generatePdfUseCase = get(),
            settingsRepository = get()
        )
    }
}

val appModules = listOf(
    appModule,
    databaseModule,
    networkModule,
    pdfModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)
