package com.vitizen.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vitizen.app.data.converter.StringListConverter
import com.vitizen.app.data.dao.InformationsGeneralesDao
import com.vitizen.app.data.dao.OperateurDao
import com.vitizen.app.data.dao.TreatmentDao
import com.vitizen.app.data.entity.InformationsGeneralesEntity
import com.vitizen.app.data.entity.OperateurEntity
import com.vitizen.app.data.entity.TreatmentEntity
import com.vitizen.app.data.local.dao.UserDao
import com.vitizen.app.data.local.entity.UserEntity
import com.vitizen.app.data.util.DateConverter

@Database(
    entities = [
        InformationsGeneralesEntity::class,
        OperateurEntity::class,
        UserEntity::class,
        TreatmentEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(StringListConverter::class, DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun informationsGeneralesDao(): InformationsGeneralesDao
    abstract fun operateurDao(): OperateurDao
    abstract fun userDao(): UserDao
    abstract fun treatmentDao(): TreatmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vitizen_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 