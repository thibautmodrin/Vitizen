package com.vitizen.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vitizen.app.data.local.dao.UserDao
import com.vitizen.app.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Créer une nouvelle table temporaire sans la colonne name
                db.execSQL("""
                    CREATE TABLE users_temp (
                        uid TEXT PRIMARY KEY NOT NULL,
                        email TEXT NOT NULL,
                        role TEXT NOT NULL,
                        isEmailVerified INTEGER NOT NULL
                    )
                """)

                // Copier les données de l'ancienne table vers la nouvelle
                db.execSQL("""
                    INSERT INTO users_temp (uid, email, role, isEmailVerified)
                    SELECT uid, email, role, isEmailVerified FROM users
                """)

                // Supprimer l'ancienne table
                db.execSQL("DROP TABLE users")

                // Renommer la nouvelle table
                db.execSQL("ALTER TABLE users_temp RENAME TO users")
            }
        }
    }
} 