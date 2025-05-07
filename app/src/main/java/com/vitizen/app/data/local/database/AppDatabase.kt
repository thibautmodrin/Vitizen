package com.vitizen.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vitizen.app.data.local.converter.DateConverter
import com.vitizen.app.data.local.converter.StringListConverter
import com.vitizen.app.data.local.dao.InformationsGeneralesDao
import com.vitizen.app.data.local.dao.OperateurDao
import com.vitizen.app.data.local.dao.ParcelleDao
import com.vitizen.app.data.local.dao.PulverisateurDao
import com.vitizen.app.data.local.dao.TreatmentDao
import com.vitizen.app.data.local.dao.UserDao
import com.vitizen.app.data.local.entity.InformationsGeneralesEntity
import com.vitizen.app.data.local.entity.OperateurEntity
import com.vitizen.app.data.local.entity.ParcelleEntity
import com.vitizen.app.data.local.entity.PulverisateurEntity
import com.vitizen.app.data.local.entity.TreatmentEntity
import com.vitizen.app.data.local.entity.UserEntity

@Database(
    entities = [
        InformationsGeneralesEntity::class,
        OperateurEntity::class,
        UserEntity::class,
        TreatmentEntity::class,
        PulverisateurEntity::class,
        ParcelleEntity::class
    ],
    version = 8,
    exportSchema = false
)
@TypeConverters(StringListConverter::class, DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun informationsGeneralesDao(): InformationsGeneralesDao
    abstract fun operateurDao(): OperateurDao
    abstract fun userDao(): UserDao
    abstract fun treatmentDao(): TreatmentDao
    abstract fun pulverisateurDao(): PulverisateurDao
    abstract fun parcelleDao(): ParcelleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vitizen_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}