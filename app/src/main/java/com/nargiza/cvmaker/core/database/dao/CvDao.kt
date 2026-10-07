package com.nargiza.cvmaker.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.nargiza.cvmaker.core.database.entity.CvEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface CvDao {

    @Query("SELECT * FROM cvs ORDER BY updatedAt DESC")
    fun observeCvs(): Flow<List<CvEntity>>

    @Query("SELECT * FROM cvs WHERE id = :id")
    fun observeCv(id: String): Flow<CvEntity?>

    @Query("SELECT * FROM cvs WHERE id = :id LIMIT 1")
    suspend fun getCvById(id: String): CvEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCv(cv: CvEntity)

    @Update
    suspend fun updateCv(cv: CvEntity)

    @Query("DELETE FROM cvs WHERE id = :id")
    suspend fun deleteCv(id: String)

    @Query("DELETE FROM cvs")
    suspend fun deleteAll()

    @Transaction
    suspend fun duplicateCv(id: String) {
        val existing = getCvById(id) ?: return
        val newId = UUID.randomUUID().toString()
        val copyTitle = "${existing.title} Copy"
        val duplicated = existing.copy(
            id = newId,
            title = copyTitle,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        insertCv(duplicated)
    }

    @Query("""
        SELECT * FROM cvs 
        WHERE title LIKE '%' || :query || '%' 
           OR fullName LIKE '%' || :query || '%' 
           OR professionalTitle LIKE '%' || :query || '%'
        ORDER BY updatedAt DESC
    """)
    fun searchCvs(query: String): Flow<List<CvEntity>>
}
