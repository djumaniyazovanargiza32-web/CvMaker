package com.nargiza.cvmaker.domain.repository

import com.nargiza.cvmaker.core.model.Cv
import kotlinx.coroutines.flow.Flow

interface CvRepository {
    fun observeCvs(): Flow<List<Cv>>
    fun observeCv(id: String): Flow<Cv?>
    fun searchCvs(query: String): Flow<List<Cv>>
    suspend fun getCv(id: String): Cv?
    suspend fun saveCv(cv: Cv)
    suspend fun deleteCv(id: String)
    suspend fun duplicateCv(id: String)
    suspend fun deleteAllCvs()
}
