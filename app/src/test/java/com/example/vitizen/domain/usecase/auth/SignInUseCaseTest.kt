package com.example.vitizen.domain.usecase.auth

import com.example.vitizen.domain.model.User
import com.example.vitizen.domain.repository.AuthRepository
import com.example.vitizen.domain.usecase.validation.ValidateEmailUseCase
import com.example.vitizen.domain.usecase.validation.ValidatePasswordUseCase
import com.example.vitizen.domain.usecase.validation.ValidationResult
import com.example.vitizen.services.ConnectivityChecker
import com.example.vitizen.services.Result
import com.example.vitizen.services.SessionManager
import com.example.vitizen.services.SecureCredentialsManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class SignInUseCaseTest {

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var sessionManager: SessionManager

    @Mock
    private lateinit var validateEmail: ValidateEmailUseCase

    @Mock
    private lateinit var validatePassword: ValidatePasswordUseCase

    @Mock
    private lateinit var connectivityChecker: ConnectivityChecker

    @Mock
    private lateinit var secureCredentialsManager: SecureCredentialsManager

    private lateinit var signInUseCase: SignInUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        signInUseCase = SignInUseCase(
            authRepository,
            sessionManager,
            validateEmail,
            validatePassword,
            connectivityChecker,
            secureCredentialsManager
        )
    }

    @Test
    fun `signIn with saved credentials and no internet should succeed`() = runBlocking {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val savedUser = User(
            uid = "123",
            name = "Test User",
            email = email,
            isEmailVerified = true
        )

        `when`(validateEmail(email)).thenReturn(ValidationResult.Success)
        `when`(secureCredentialsManager.getCredentials()).thenReturn(Pair(email, password))
        `when`(sessionManager.getCurrentUser()).thenReturn(savedUser)
        `when`(connectivityChecker.isConnected()).thenReturn(false)

        // When
        val result = signInUseCase(email, password).first()

        // Then
        assertTrue(result is Result.Success)
        assertEquals(savedUser, (result as Result.Success).data)
    }

    @Test
    fun `signIn with no saved credentials and no internet should fail`() = runBlocking {
        // Given
        val email = "test@example.com"
        val password = "password123"

        `when`(validateEmail(email)).thenReturn(ValidationResult.Success)
        `when`(secureCredentialsManager.getCredentials()).thenReturn(Pair("", ""))
        `when`(connectivityChecker.isConnected()).thenReturn(false)

        // When
        val result = signInUseCase(email, password).first()

        // Then
        assertTrue(result is Result.Error)
        assertEquals("Pas de connexion internet", (result as Result.Error).exception.message)
    }

    @Test
    fun `signIn with internet connection and unverified email should fail`() = runBlocking {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val unverifiedUser = User(
            uid = "123",
            name = "Test User",
            email = email,
            isEmailVerified = false
        )

        `when`(validateEmail(email)).thenReturn(ValidationResult.Success)
        `when`(secureCredentialsManager.getCredentials()).thenReturn(Pair("", ""))
        `when`(connectivityChecker.isConnected()).thenReturn(true)
        `when`(authRepository.signIn(email, password)).thenReturn(flowOf(Result.Success(unverifiedUser)))

        // When
        val result = signInUseCase(email, password).first()

        // Then
        assertTrue(result is Result.Error)
        assertEquals("L'email n'est pas vérifié", (result as Result.Error).exception.message)
    }

    @Test
    fun `signIn with internet connection and verified email should succeed`() = runBlocking {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val verifiedUser = User(
            uid = "123",
            name = "Test User",
            email = email,
            isEmailVerified = true
        )

        `when`(validateEmail(email)).thenReturn(ValidationResult.Success)
        `when`(secureCredentialsManager.getCredentials()).thenReturn(Pair("", ""))
        `when`(connectivityChecker.isConnected()).thenReturn(true)
        `when`(authRepository.signIn(email, password)).thenReturn(flowOf(Result.Success(verifiedUser)))

        // When
        val result = signInUseCase(email, password).first()

        // Then
        assertTrue(result is Result.Success)
        assertEquals(verifiedUser, (result as Result.Success).data)
    }

    @Test
    fun `signIn with invalid email should fail`() = runBlocking {
        // Given
        val email = "invalid-email"
        val password = "password123"

        `when`(validateEmail(email)).thenReturn(ValidationResult.Error("Email invalide"))

        // When
        val result = signInUseCase(email, password).first()

        // Then
        assertTrue(result is Result.Error)
        assertEquals("Email invalide", (result as Result.Error).exception.message)
    }

    @Test
    fun `signIn with saved credentials but different password should fail`() = runBlocking {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val savedPassword = "different-password"

        `when`(validateEmail(email)).thenReturn(ValidationResult.Success)
        `when`(secureCredentialsManager.getCredentials()).thenReturn(Pair(email, savedPassword))
        `when`(connectivityChecker.isConnected()).thenReturn(false)

        // When
        val result = signInUseCase(email, password).first()

        // Then
        assertTrue(result is Result.Error)
        assertEquals("Pas de connexion internet", (result as Result.Error).exception.message)
    }

    @Test
    fun `signIn with repository error should fail`() = runBlocking {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val errorMessage = "Erreur de connexion"

        `when`(validateEmail(email)).thenReturn(ValidationResult.Success)
        `when`(secureCredentialsManager.getCredentials()).thenReturn(Pair("", ""))
        `when`(connectivityChecker.isConnected()).thenReturn(true)
        `when`(authRepository.signIn(email, password)).thenReturn(flowOf(Result.Error(Exception(errorMessage))))

        // When
        val result = signInUseCase(email, password).first()

        // Then
        assertTrue(result is Result.Error)
        assertEquals(errorMessage, (result as Result.Error).exception.message)
    }
} 