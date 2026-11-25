package dev.fnc314.aijobtracker.frontend.features.aiapply.module

import dev.fnc314.aijobtracker.frontend.core.data.database.DatabaseHolder
import dev.fnc314.aijobtracker.frontend.core.data.source.DataResult
import dev.fnc314.aijobtracker.frontend.core.data.source.LocalDataSource
import dev.fnc314.aijobtracker.frontend.core.data.source.RemoteDataSource
import dev.fnc314.aijobtracker.frontend.core.data.source.RemoteRecord
import dev.fnc314.aijobtracker.frontend.features.aiapply.domain.repository.ApplyAssistantRepository
import dev.fnc314.aijobtracker.frontend.features.aiapply.domain.usecase.EvaluateJobPostingUseCase
import dev.fnc314.aijobtracker.frontend.features.aiapply.ui.ApplyAssistantViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull

internal class AiApplyModuleTest {

    @AfterTest
    fun tearDown(): Unit {
        stopKoin()
    }

    @Test
    fun testAiApplyModule_resolvesDependenciesWithDatabaseHolder(): Unit {
        val testCoreDataModule = module {
            single { DatabaseHolder(null) }
            single<LocalDataSource> {
                object : LocalDataSource {
                    override fun getString(key: String): String? = null
                    override fun putString(key: String, value: String) {}
                    override fun remove(key: String) {}
                    override fun clear() {}
                }
            }
            single<RemoteDataSource> {
                object : RemoteDataSource {
                    override fun observeCollection(collectionPath: String): Flow<List<RemoteRecord>> = emptyFlow()
                    override suspend fun getCollection(collectionPath: String): List<RemoteRecord> = emptyList()
                    override suspend fun getDocument(collectionPath: String, documentId: String): RemoteRecord? = null
                    override suspend fun setDocument(collectionPath: String, documentId: String, record: RemoteRecord): DataResult = DataResult.Success
                    override suspend fun updateDocument(collectionPath: String, documentId: String, record: RemoteRecord): DataResult = DataResult.Success
                }
            }
        }

        val app = startKoin {
            modules(testCoreDataModule, aiApplyModule)
        }

        assertNotNull(app.koin.getOrNull<ApplyAssistantRepository>(), "ApplyAssistantRepository must resolve")
        assertNotNull(app.koin.getOrNull<EvaluateJobPostingUseCase>(), "EvaluateJobPostingUseCase must resolve")
        assertNotNull(app.koin.getOrNull<ApplyAssistantViewModel>(), "ApplyAssistantViewModel must resolve")
    }
}
