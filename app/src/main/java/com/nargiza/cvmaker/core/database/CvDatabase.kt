package com.nargiza.cvmaker.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nargiza.cvmaker.core.database.dao.CvDao
import com.nargiza.cvmaker.core.database.entity.AchievementEntity
import com.nargiza.cvmaker.core.database.entity.CertificationEntity
import com.nargiza.cvmaker.core.database.entity.CvEntity
import com.nargiza.cvmaker.core.database.entity.EducationEntity
import com.nargiza.cvmaker.core.database.entity.ExperienceEntity
import com.nargiza.cvmaker.core.database.entity.LanguageEntity
import com.nargiza.cvmaker.core.database.entity.ProjectEntity
import com.nargiza.cvmaker.core.database.entity.SkillEntity

@Database(
    entities = [
        CvEntity::class,
        ExperienceEntity::class,
        EducationEntity::class,
        SkillEntity::class,
        ProjectEntity::class,
        CertificationEntity::class,
        LanguageEntity::class,
        AchievementEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CvDatabase : RoomDatabase() {
    abstract fun cvDao(): CvDao
}
