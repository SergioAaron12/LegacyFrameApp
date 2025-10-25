package com.example.legacyframeapp.navegation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// Tus imports de componentes y pantallas
import com.example.legacyframeapp.ui.components.AppTopBar
import com.example.legacyframeapp.ui.components.AppDrawer
import com.example.legacyframeapp.ui.components.loggedInDrawerItems
import com.example.legacyframeapp.ui.components.loggedOutDrawerItems // Asegúrate que esta función exista y esté actualizada
import com.example.legacyframeapp.ui.screen.HomeScreen
import com.example.legacyframeapp.ui.screen.LoginScreenVm
import com.example.legacyframeapp.ui.screen.RegisterScreenVm
import com.example.legacyframeapp.ui.screen.MoldurasScreenVm
import com.example.legacyframeapp.ui.screen.CuadrosScreenVm
import com.example.legacyframeapp.ui.screen.CartScreenVm
import com.example.legacyframeapp.ui.screen.ContactScreen
import com.example.legacyframeapp.ui.screen.AddProductScreenVm
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    // --- OBTENER ESTADOS DEL VIEWMODEL ---
    val session by authViewModel.session.collectAsStateWithLifecycle()
    val cartItemCount by authViewModel.cartItemCount.collectAsStateWithLifecycle()
    val products by authViewModel.products.collectAsStateWithLifecycle() // <-- AÑADIDO
    // ------------------------------------

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // --- Acciones de navegación (sin cambios) ---
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
    val goAddProduct: () -> Unit = {
        navController.navigate(Route.AddProduct.path) { launchSingleTop = true }
    }
    val goBack: () -> Unit = { navController.popBackStack() }
    val doLogout: () -> Unit = {
        authViewModel.logout()
        goLogin()
    }

    val openDrawer: () -> Unit = { scope.launch { drawerState.open() } }
    val closeDrawer: () -> Unit = { scope.launch { drawerState.close() } }
    // ------------------------------------------

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // --- Lógica del menú lateral (sin cambios) ---
            if (session.isLoggedIn) {
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
    ) {
        Scaffold(
            topBar = {
                // --- Llamada a AppTopBar (ya estaba correcta) ---
                AppTopBar(
                    onOpenDrawer = openDrawer,
                    onOpenCart = goCart,
                    cartItemCount = cartItemCount
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.Home.path,
                modifier = Modifier.padding(innerPadding)
            ) {
                // --- Tus Destinos de Navegación ---
                composable(Route.Home.path) {
                    // --- LLAMADA A HomeScreen (CORREGIDA) ---
                    HomeScreen(
                        onGoLogin = goLogin,
                        onGoRegister = goRegister,
                        onGoMolduras = goMolduras,
                        onGoCuadros = goCuadros,
                        onGoContact = goContact,
                        products = products // <-- PASAR LA LISTA
                    )
                    // ------------------------------------
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
                composable(Route.Molduras.path) {
                    MoldurasScreenVm(
                        vm = authViewModel,
                        onAddProduct = goAddProduct
                    )
                }
                composable(Route.Cuadros.path) {
                    CuadrosScreenVm(
                        vm = authViewModel,
                        onAddCuadro = { /* Acción para añadir cuadro si existe */ }
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
                        onNavigateBack = goBack
                    )
                }
            }
        }
    }
}