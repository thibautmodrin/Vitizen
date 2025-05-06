package com.vitizen.app.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.vitizen.app.data.local.dao.InformationsGeneralesDao
import com.vitizen.app.data.local.dao.OperateurDao
import com.vitizen.app.data.local.dao.ParcelleDao
import com.vitizen.app.data.local.dao.PulverisateurDao
import com.vitizen.app.data.local.dao.TreatmentDao
import com.vitizen.app.data.local.database.AppDatabase
import com.vitizen.app.data.local.datasource.RoomDataSource
import com.vitizen.app.data.local.datasource.UserPreferencesManager
import com.vitizen.app.data.local.dao.UserDao
import com.vitizen.app.data.repository.AuthRepositoryImpl
import com.vitizen.app.domain.repository.AuthRepository
import com.vitizen.app.domain.repository.FirebaseAuthService
import com.vitizen.app.data.util.network.ConnectivityChecker
import com.vitizen.app.data.util.validation.EmailVerifier
import com.vitizen.app.data.remote.service.GoogleAuthService
import com.vitizen.app.data.util.logging.Logger
import com.vitizen.app.data.local.security.SecureCredentialsManager
import com.vitizen.app.data.remote.service.ChatService
import com.vitizen.app.data.repository.ChatRepositoryImpl
import com.vitizen.app.domain.repository.IChatRepository
import com.vitizen.app.presentation.session.SessionManager
import com.vitizen.app.presentation.navigation.manager.NavigationManager
import com.vitizen.app.presentation.navigation.manager.NavigationManagerImpl
import com.vitizen.app.data.repository.OperateurRepository
import com.vitizen.app.domain.repository.IOperateurRepository
import com.vitizen.app.data.repository.PulverisateurRepository
import com.vitizen.app.domain.repository.IPulverisateurRepository
import com.vitizen.app.data.repository.ParcelleRepository
import com.vitizen.app.domain.repository.IParcelleRepository
import com.vitizen.app.data.repository.InformationsGeneralesRepository
import com.vitizen.app.domain.repository.IInformationsGeneralesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
        return AppDatabase.Companion.getDatabase(context)
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
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
    fun provideTreatmentDao(database: AppDatabase): TreatmentDao {
        return database.treatmentDao()
    }

    @Provides
    fun provideParcelleDao(database: AppDatabase): ParcelleDao {
        return database.parcelleDao()
    }

    @Provides
    fun provideInformationsGeneralesDao(database: AppDatabase): InformationsGeneralesDao {
        return database.informationsGeneralesDao()
    }

    @Provides
    fun providePulverisateurDao(database: AppDatabase): PulverisateurDao {
        return database.pulverisateurDao()
    }

    @Provides
    fun provideOperateurDao(database: AppDatabase): OperateurDao {
        return database.operateurDao()
    }

    @Provides
    @Singleton
    fun provideOperateurRepository(operateurDao: OperateurDao): IOperateurRepository {
        return OperateurRepository(operateurDao)
    }

    @Provides
    @Singleton
    fun providePulverisateurRepository(pulverisateurDao: PulverisateurDao): IPulverisateurRepository {
        return PulverisateurRepository(pulverisateurDao)
    }

    @Provides
    @Singleton
    fun provideParcelleRepository(parcelleDao: ParcelleDao): IParcelleRepository {
        return ParcelleRepository(parcelleDao)
    }

    @Provides
    @Singleton
    fun provideInformationsGeneralesRepository(informationsGeneralesDao: InformationsGeneralesDao): IInformationsGeneralesRepository {
        return InformationsGeneralesRepository(informationsGeneralesDao)
    }

}