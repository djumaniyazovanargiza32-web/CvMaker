package com.nargiza.cvmaker.data.repository

import com.nargiza.cvmaker.core.common.DispatchersProvider
import com.nargiza.cvmaker.core.database.dao.CvDao
import com.nargiza.cvmaker.core.database.mapper.CvEntityMapper
import com.nargiza.cvmaker.core.model.Cv
import com.nargiza.cvmaker.domain.repository.CvRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CvRepositoryImpl(
    private val cvDao: CvDao,
    private val dispatchers: DispatchersProvider
) : CvRepository {

    override fun observeCvs(): Flow<List<Cv>> {
        return cvDao.observeCvs().map { list ->
            list.map { CvEntityMapper.toDomain(it) }
        }.flowOn(dispatchers.io)
    }

    override fun observeCv(id: String): Flow<Cv?> {
        return cvDao.observeCv(id).map { entity ->
            entity?.let { CvEntityMapper.toDomain(it) }
        }.flowOn(dispatchers.io)
    }

    override fun searchCvs(query: String): Flow<List<Cv>> {
        return cvDao.searchCvs(query).map { list ->
            list.map { CvEntityMapper.toDomain(it) }
        }.flowOn(dispatchers.io)
    }

    override suspend fun getCv(id: String): Cv? = withContext(dispatchers.io) {
        cvDao.getCvById(id)?.let { CvEntityMapper.toDomain(it) }
    }

    override suspend fun saveCv(cv: Cv): Unit = withContext(dispatchers.io) {
        val updatedCv = cv.copy(updatedAt = System.currentTimeMillis())
        cvDao.insertCv(CvEntityMapper.toEntity(updatedCv))
    }

    override suspend fun deleteCv(id: String): Unit = withContext(dispatchers.io) {
        cvDao.deleteCv(id)
    }

    override suspend fun duplicateCv(id: String): Unit = withContext(dispatchers.io) {
        cvDao.duplicateCv(id)
    }

    override suspend fun deleteAllCvs(): Unit = withContext(dispatchers.io) {
        cvDao.deleteAll()
    }
}
