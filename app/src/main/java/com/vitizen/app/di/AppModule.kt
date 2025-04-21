package com.vitizen.app.di

import android.content.Context
import androidx.room.Room
import com.vitizen.app.data.datasource.RoomDataSource
import com.vitizen.app.data.datasource.UserPreferencesManager
import com.vitizen.app.data.database.AppDatabase
import com.vitizen.app.data.local.dao.UserDao
import com.vitizen.app.data.repository.AuthRepositoryImpl
import com.vitizen.app.domain.repository.AuthRepository
import com.vitizen.app.domain.repository.FirebaseAuthService
import com.vitizen.app.services.*
import com.vitizen.app.ui.navigation.NavigationManager
import com.vitizen.app.ui.navigation.NavigationManagerImpl
import com.google.firebase.auth.FirebaseAuth
import com.vitizen.app.data.dao.InformationsGeneralesDao
import com.vitizen.app.data.dao.OperateurDao
import com.vitizen.app.data.dao.TreatmentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuthService: FirebaseAuthService,
        roomDataSource: RoomDataSource,
        userPreferencesManager: UserPreferencesManager,
        logger: Logger,
        emailVerifier: EmailVerifier,
        sessionManager: SessionManager
    ): AuthRepository {
        return AuthRepositoryImpl(
            firebaseAuthService,
            roomDataSource,
            userPreferencesManager,
            logger,
            emailVerifier,
            sessionManager
        )
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "vitizen_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideRoomDataSource(userDao: UserDao): RoomDataSource {
        return RoomDataSource(userDao)
    }

    @Provides
    @Singleton
    fun provideUserPreferencesManager(
        @ApplicationContext context: Context,
        roomDataSource: RoomDataSource
    ): UserPreferencesManager {
        return UserPreferencesManager(context, roomDataSource)
    }

    @Provides
    @Singleton
    fun provideLogger(): Logger {
        return Logger()
    }

    @Provides
    @Singleton
    fun provideEmailVerifier(): EmailVerifier {
        return EmailVerifier()
    }

    @Provides
    @Singleton
    fun provideSessionManager(
        userPreferencesManager: UserPreferencesManager,
        roomDataSource: RoomDataSource,
        connectivityChecker: ConnectivityChecker
    ): SessionManager {
        return SessionManager(userPreferencesManager, roomDataSource, connectivityChecker)
    }

    @Provides
    @Singleton
    fun provideConnectivityChecker(
        @ApplicationContext context: Context
    ): ConnectivityChecker {
        return ConnectivityChecker(context)
    }

    @Provides
    @Singleton
    fun provideGoogleAuthService(
        @ApplicationContext context: Context,
        firebaseAuth: FirebaseAuth
    ): GoogleAuthService {
        return GoogleAuthService(context, firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideNavigationManager(): NavigationManager {
        return NavigationManagerImpl()
    }

    @Provides
    @Singleton
    fun provideSecureCredentialsManager(
        @ApplicationContext context: Context
    ): SecureCredentialsManager {
        return SecureCredentialsManager(context)
    }

    @Provides
    @Singleton
    fun provideInformationsGeneralesDao(database: AppDatabase): InformationsGeneralesDao {
        return database.informationsGeneralesDao()
    }

    @Provides
    @Singleton
    fun provideOperateurDao(database: AppDatabase): OperateurDao {
        return database.operateurDao()
    }

    @Provides
    @Singleton
    fun provideTreatmentDao(database: AppDatabase): TreatmentDao {
        return database.treatmentDao()
    }
} 