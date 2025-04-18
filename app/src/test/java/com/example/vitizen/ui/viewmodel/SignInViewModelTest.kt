package com.example.vitizen.ui.viewmodel

import com.example.vitizen.domain.model.User
import com.example.vitizen.domain.usecase.auth.SignInUseCase
import com.example.vitizen.services.Result
import com.example.vitizen.services.SessionManager
import com.example.vitizen.services.SecureCredentialsManager
import com.example.vitizen.services.Logger
import com.example.vitizen.ui.state.AuthState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class SignInViewModelTest {

    @Mock
    private lateinit var signInUseCase: SignInUseCase

    @Mock
    private lateinit var sessionManager: SessionManager

    @Mock
    private lateinit var secureCredentialsManager: SecureCredentialsManager

    @Mock
    private lateinit var logger: Logger

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: SignInViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = SignInViewModel(
            signInUseCase = signInUseCase,
            sessionManager = sessionManager,
            logger = logger,
            secureCredentialsManager = secureCredentialsManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `signIn with saved credentials and no internet should succeed`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val savedUser = User(
            uid = "123",
            name = "Test User",
            email = email,
            isEmailVerified = true
        )

        `when`(secureCredentialsManager.getCredentials()).thenReturn(Pair(email, password))
        `when`(signInUseCase(email, password)).thenReturn(flowOf(Result.Success(savedUser)))
        `when`(sessionManager.saveSession(savedUser)).thenReturn(Unit)

        // When
        viewModel.signIn(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.authState.value is AuthState.Authenticated)
        assertEquals(savedUser, (viewModel.authState.value as AuthState.Authenticated).user)
    }

    @Test
    fun `signIn with no saved credentials and no internet should fail`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val errorMessage = "Erreur de connexion réseau. Veuillez vérifier votre connexion internet"

        `when`(secureCredentialsManager.getCredentials()).thenReturn(Pair("", ""))
        `when`(signInUseCase(email, password)).thenReturn(flowOf(Result.Error(Exception(errorMessage))))

        // When
        viewModel.signIn(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.authState.value is AuthState.Error)
        assertEquals(errorMessage, (viewModel.authState.value as AuthState.Error).message)
    }

    @Test
    fun `signIn with saved credentials but different password should fail`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val savedPassword = "different-password"
        val errorMessage = "Le mot de passe est incorrect"

        `when`(secureCredentialsManager.getCredentials()).thenReturn(Pair(email, savedPassword))
        `when`(signInUseCase(email, password)).thenReturn(flowOf(Result.Error(Exception(errorMessage))))

        // When
        viewModel.signIn(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.authState.value is AuthState.Error)
        assertEquals(errorMessage, (viewModel.authState.value as AuthState.Error).message)
    }
} 