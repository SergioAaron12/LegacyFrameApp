package com.example.legacyframeapp.navegation
import androidx.compose.foundation.layout.padding // Para aplicar innerPadding
import androidx.compose.material3.Scaffold // Estructura base con slots
import androidx.compose.runtime.Composable // Marcador composable
import androidx.compose.ui.Modifier // Modificador
import androidx.navigation.NavHostController // Controlador de navegación
import androidx.navigation.compose.NavHost // Contenedor de destinos
import androidx.navigation.compose.composable // Declarar cada destino
import kotlinx.coroutines.launch // Para abrir/cerrar drawer con corrutinas

import androidx.compose.material3.ModalNavigationDrawer // Drawer lateral modal
import androidx.compose.material3.rememberDrawerState // Estado del drawer
import androidx.compose.material3.DrawerValue // Valores (Opened/Closed)
import androidx.compose.runtime.rememberCoroutineScope // Alcance de corrutina

import androidx.compose.runtime.getValue // Para 'by'
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Para el estado
import com.example.legacyframeapp.ui.components.loggedInDrawerItems // NUEVO
import com.example.legacyframeapp.ui.components.loggedOutDrawerItems // NUEVO
import com.example.legacyframeapp.ui.screen.MoldurasScreenVm // NUEVO
import com.example.legacyframeapp.ui.screen.CuadrosScreenVm // NUEVO

import com.example.legacyframeapp.ui.components.AppTopBar // Barra superior
import com.example.legacyframeapp.ui.components.AppDrawer // Drawer composable
import com.example.legacyframeapp.ui.screen.HomeScreen // Pantalla Home
import com.example.legacyframeapp.ui.screen.LoginScreenVm // Pantalla Login
import com.example.legacyframeapp.ui.screen.RegisterScreenVm // Pantalla Registro
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import com.example.legacyframeapp.ui.screen.AddProductScreenVm
import com.example.legacyframeapp.ui.screen.CartScreenVm
import com.example.legacyframeapp.ui.screen.ContactScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    // --- ESTADO DE SESIÓN (CLAVE PARA LA UI DINÁMICA) ---
    val session by authViewModel.session.collectAsStateWithLifecycle()
    // ----------------------------------------------------

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // --- ACCIONES DE NAVEGACIÓN ---
    val goHome: () -> Unit = {
        navController.navigate(Route.Home.path) {
            popUpTo(navController.graph.startDestinationId) { saveState = true }
            launchSingleTop = true
        }
    }
    val goLogin: () -> Unit = {
        navController.navigate(Route.Login.path) { launchSingleTop = true }
    }
    val goRegister: () -> Unit = {
        navController.navigate(Route.Register.path) { launchSingleTop = true }
    }
    // --- NUEVAS ACCIONES ---
    val goMolduras: () -> Unit = {
        navController.navigate(Route.Molduras.path) { launchSingleTop = true }
    }
    val goCuadros: () -> Unit = {
        navController.navigate(Route.Cuadros.path) { launchSingleTop = true }
    }
    val goCart: () -> Unit = {
        navController.navigate(Route.Cart.path) { launchSingleTop = true }
    }
    val goContact: () -> Unit = {
        navController.navigate(Route.Contact.path) { launchSingleTop = true }
    }
    // --- ACCIÓN PARA VOLVER ATRÁS ---
    val goBack: () -> Unit = {
        navController.popBackStack()
    }

    // --- ACCIÓN PARA IR A AÑADIR PRODUCTO ---
    val goAddProduct: () -> Unit = {
        navController.navigate(Route.AddProduct.path) {
            launchSingleTop = true
        }
    }

    val doLogout: () -> Unit = {
        authViewModel.logout() // Llama al ViewModel
        goLogin() // Envía al usuario a Login
    }
    // ----------------------

    // Función para abrir el menú
    val openDrawer: () -> Unit = {
        scope.launch { drawerState.open() }
    }
    // Función para cerrar el menú
    val closeDrawer: () -> Unit = {
        scope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        // --- CONTENIDO DINÁMICO DEL MENÚ ---
        drawerContent = {
            if (session.isLoggedIn) {
                // MENÚ LOGUEADO
                AppDrawer(
                    currentRoute = null,
                    items = loggedInDrawerItems(
                        onHome = { closeDrawer(); goHome() },
                        onMolduras = { closeDrawer(); goMolduras() },
                        onCuadros = { closeDrawer(); goCuadros() },
                        onCart = { closeDrawer(); goCart() },
                        onContact = { closeDrawer(); goContact() },
                        onLogout = { closeDrawer(); doLogout() }
                    )
                )
            } else {
                // MENÚ NO LOGUEADO
                AppDrawer(
                    currentRoute = null,
                    items = loggedOutDrawerItems(
                        onHome = { closeDrawer(); goHome() },
                        onLogin = { closeDrawer(); goLogin() },
                        onRegister = { closeDrawer(); goRegister() },
                        onCuadros = { closeDrawer(); goCuadros() },
                        onMolduras = { closeDrawer(); goMolduras() },
                        onCart = { closeDrawer(); goCart() },
                        onContact = { closeDrawer(); goContact() }
                    )
                )
            }
        }
        // -----------------------------------
    ) {
        Scaffold(
            // --- TOP BAR SIMPLIFICADA ---
            topBar = {
                val cartCount by authViewModel.cartCount.collectAsStateWithLifecycle()
                AppTopBar(
                    onOpenDrawer = openDrawer,
                    cartCount = cartCount,
                    onOpenCart = goCart
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.Home.path, // (Puedes cambiar a Login.path si prefieres)
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Route.Home.path) {
                    val products by authViewModel.products.collectAsStateWithLifecycle()
                    HomeScreen(
                        onGoLogin = goLogin,
                        onGoRegister = goRegister,
                        onGoMolduras = goMolduras,
                        onGoCuadros = goCuadros,
                        onGoContact = goContact,
                        products = products
                    )
                }
                composable(Route.Login.path) {
                    LoginScreenVm(
                        vm = authViewModel,
                        onLoginOkNavigateHome = goHome,
                        onGoRegister = goRegister
                    )
                }
                composable(Route.Register.path) {
                    RegisterScreenVm(
                        vm = authViewModel,
                        onRegisteredNavigateLogin = goLogin,
                        onGoLogin = goLogin
                    )
                }
                // --- AÑADIR LA NUEVA RUTA ---
                composable(Route.Molduras.path) {
                    MoldurasScreenVm(
                        vm = authViewModel,
                        onAddProduct = goAddProduct // <--- PASAR LA ACCIÓN
                    )
                }

                composable(Route.Cuadros.path) {
                    CuadrosScreenVm(
                        vm = authViewModel,
                        onAddCuadro = { /* TODO: crear pantalla de añadir cuadro */ }
                    )
                }

                composable(Route.Cart.path) {
                    CartScreenVm(
                        vm = authViewModel,
                        onNavigateBack = goBack
                    )
                }

                composable(Route.Contact.path) {
                    ContactScreen()
                }

                composable(Route.AddProduct.path) {
                    AddProductScreenVm(
                        vm = authViewModel,
                        onNavigateBack = goBack // <--- PASAR ACCIÓN "VOLVER"
                    )
                }
            }
        }
    }
}