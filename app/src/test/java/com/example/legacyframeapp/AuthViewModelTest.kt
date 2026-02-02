package com.example.legacyframeapp

import android.app.Application
import com.example.legacyframeapp.data.local.storage.UserPreferences
import com.example.legacyframeapp.data.network.ExternalApiService
import com.example.legacyframeapp.data.network.model.IndicadorData
import com.example.legacyframeapp.data.network.model.IndicadoresResponse
import com.example.legacyframeapp.data.repository.*
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Mocks de dependencias
    private val app = mockk<Application>(relaxed = true)
    private val userRepo = mockk<UserRepository>()
    private val productRepo = mockk<ProductRepository>()
    private val cuadroRepo = mockk<CuadroRepository>()
    private val cartRepo = mockk<CartRepository>(relaxed = true)
    private val userPrefs = mockk<UserPreferences>(relaxed = true)
    private val orderRepo = mockk<OrderRepository>()
    private val contactRepo = mockk<ContactRepository>()
    private val externalServiceMock = mockk<ExternalApiService>(relaxed = true)

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        // 1. Mockear RetrofitClient (para la llamada del Dólar en el init)
        mockkObject(RetrofitClient)
        coEvery { RetrofitClient.externalService } returns externalServiceMock
        coEvery { externalServiceMock.getIndicadores() } returns Response.success(null)

        // 2. Mockear flujos de UserPreferences para evitar errores de inicialización
        coEvery { userPrefs.isLoggedIn } returns flowOf(false)
        coEvery { userPrefs.isDarkMode } returns flowOf(false)
        coEvery { userPrefs.themeMode } returns flowOf("system")
        coEvery { userPrefs.accentColor } returns flowOf("#FF8B5C2A")
        coEvery { userPrefs.fontScale } returns flowOf(1.0f)
        coEvery { userPrefs.notifOffers } returns flowOf(true)
        coEvery { userPrefs.notifTracking } returns flowOf(true)
        coEvery { userPrefs.notifCart } returns flowOf(true)
        coEvery { userPrefs.language } returns flowOf("es")
        coEvery { userPrefs.avatarType } returns flowOf("male")

        // 3. Mockear Repositorios llamados al iniciar
        coEvery { productRepo.getProducts() } returns emptyList()
        coEvery { cartRepo.items() } returns flowOf(emptyList())
        coEvery { cartRepo.total() } returns flowOf(0)
        coEvery { cartRepo.count() } returns flowOf(0)

        // --- CORRECCIÓN AQUÍ ---
        // Antes decía getAll() (que ya no existe), ahora usamos getMyOrders()
        coEvery { orderRepo.getMyOrders(any()) } returns emptyList()
        // -----------------------

        // Inicializar el ViewModel
        viewModel = AuthViewModel(
            app, userRepo, productRepo, cuadroRepo,
            cartRepo, userPrefs, orderRepo, contactRepo
        )
    }

    @After
    fun tearDown() {
        unmockkAll() // Limpiar mocks estáticos
    }

    // --- TEST 1: Login Exitoso ---
    @Test
    fun `submitLogin exitoso actualiza estado a success`() = runTest {
        // GIVEN
        val email = "test@duoc.cl"
        val pass = "123456"
        coEvery { userRepo.login(email, pass) } returns Result.success(true)

        // WHEN
        viewModel.onLoginEmailChange(email)
        viewModel.onLoginPassChange(pass)
        viewModel.submitLogin()

        // THEN
        assertTrue(viewModel.login.value.success)
        assertFalse(viewModel.login.value.isSubmitting)
        // Verificamos que el ViewModel intentó guardar la sesión
        coVerify { userPrefs.setLoggedIn(true) }
    }

    // --- TEST 2: Login Fallido ---
    @Test
    fun `submitLogin fallido actualiza estado con error`() = runTest {
        // GIVEN
        val email = "fail@duoc.cl"
        val pass = "wrong"
        coEvery { userRepo.login(email, pass) } returns Result.failure(Exception("Credenciales malas"))

        // WHEN
        viewModel.onLoginEmailChange(email)
        viewModel.onLoginPassChange(pass)
        viewModel.submitLogin()

        // THEN
        assertFalse(viewModel.login.value.success)
        assertEquals("Credenciales malas", viewModel.login.value.errorMsg)
    }

    // --- TEST 3: Registro Exitoso ---
    @Test
    fun `submitRegister exitoso actualiza estado`() = runTest {
        // GIVEN
        coEvery { userRepo.register(any()) } returns Result.success(true)

        // WHEN
        viewModel.onRegisterNombreChange("Juan")
        viewModel.onRegisterEmailChange("juan@test.com")
        viewModel.onRegisterPassChange("Pass123!")
        viewModel.onRegisterConfirmChange("Pass123!")
        viewModel.onRegisterRutChange("11111111")
        viewModel.onRegisterDvChange("1")
        viewModel.onRegisterPhoneChange("987654321")

        viewModel.submitRegister()

        // THEN
        assertTrue(viewModel.register.value.success)
        assertFalse(viewModel.register.value.isSubmitting)
    }

    // --- TEST 4: API Externa (Dólar) ---
    @Test
    fun `fetchDolarValue actualiza estado cuando API responde exito`() = runTest {
        // GIVEN: La API responde con valor 950.0
        val fakeResponse = IndicadoresResponse(
            dolar = IndicadorData(950.0, "2024-12-01")
        )
        coEvery { externalServiceMock.getIndicadores() } returns Response.success(fakeResponse)

        // WHEN: Creamos una nueva instancia para disparar el init de nuevo
        val newViewModel = AuthViewModel(
            app, userRepo, productRepo, cuadroRepo,
            cartRepo, userPrefs, orderRepo, contactRepo
        )

        // THEN: El estado dolarValue debe tener el valor 950.0
        assertEquals(950.0, newViewModel.dolarValue.value)
    }

    // --- TEST 5: API Contacto ---
    @Test
    fun `sendContactMessage llama al repositorio y retorna exito`() = runTest {
        // GIVEN
        coEvery { contactRepo.sendMessage(any(), any(), any()) } returns Result.success(true)

        // WHEN
        var resultFlag = false
        viewModel.sendContactMessage("Juan", "juan@test.com", "Hola") { success ->
            resultFlag = success
        }

        // THEN
        assertTrue(resultFlag)
        coVerify { contactRepo.sendMessage("Juan", "juan@test.com", "Hola") }
    }
}