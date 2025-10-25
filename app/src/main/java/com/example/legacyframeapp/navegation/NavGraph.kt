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
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.legacyframeapp.ui.components.AppTopBar
import com.example.legacyframeapp.ui.components.AppBottomBar
import com.example.legacyframeapp.ui.components.AppDrawer
import com.example.legacyframeapp.ui.components.loggedInDrawerItems
import com.example.legacyframeapp.ui.components.loggedOutDrawerItems
import com.example.legacyframeapp.ui.screen.HomeScreen
import com.example.legacyframeapp.ui.screen.LoginScreenVm
import com.example.legacyframeapp.ui.screen.RegisterScreenVm
import com.example.legacyframeapp.ui.screen.MoldurasScreenVm
import com.example.legacyframeapp.ui.screen.CuadrosScreenVm
import com.example.legacyframeapp.ui.screen.CartScreenVm
import com.example.legacyframeapp.ui.screen.ContactScreen
import com.example.legacyframeapp.ui.screen.AddProductScreenVm
import com.example.legacyframeapp.ui.screen.AdminScreenVm
import com.example.legacyframeapp.ui.screen.ChangeProductImageScreenVm
import com.example.legacyframeapp.ui.screen.DeleteProductScreenVm
import com.example.legacyframeapp.ui.screen.ProfileScreenVm
import com.example.legacyframeapp.ui.screen.SettingsScreenVm
import com.example.legacyframeapp.ui.screen.PurchasesScreenVm
import com.example.legacyframeapp.ui.screen.TermsScreen
import com.example.legacyframeapp.ui.screen.AddCuadroScreenVm
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
    val goProfile: () -> Unit = {
        navController.navigate(Route.Profile.path) { launchSingleTop = true }
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
    val goAdmin: () -> Unit = {
        navController.navigate(Route.Admin.path) { launchSingleTop = true }
    }
    val goChangeProductImage: () -> Unit = {
        navController.navigate(Route.ChangeProductImage.path) { launchSingleTop = true }
    }
    val goDeleteProduct: () -> Unit = {
        navController.navigate(Route.DeleteProduct.path) { launchSingleTop = true }
    }
    // --- ACCIÓN PARA VOLVER ATRÁS ---
    val goBack: () -> Unit = {
        navController.popBackStack()
    }

    // --- ACCIÓN PARA IR A AÑADIR PRODUCTO ---
    val goAddProduct: () -> Unit = {
        navController.navigate(Route.AddProduct.path) { launchSingleTop = true }
    }
    val goAddCuadro: () -> Unit = {
        navController.navigate(Route.AddCuadro.path) { launchSingleTop = true }
    }
    val goSettings: () -> Unit = {
        navController.navigate(Route.Settings.path) { launchSingleTop = true }
    }
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
                        onAdmin = if (session.isAdmin) ({ closeDrawer(); goAdmin() }) else null,
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
                        onCart = { closeDrawer(); goCart() }
                    )
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val route = backStackEntry?.destination?.route
                if (route != Route.Splash.path) {
                    AppTopBar(
                        onOpenDrawer = openDrawer,
                        onOpenCart = goCart,
                        cartItemCount = cartItemCount
                    )
                }
            },
            bottomBar = {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val route = backStackEntry?.destination?.route
                // Ocultar en pantallas de flujo o detalle
                val hideOn = setOf(
                    Route.Splash.path,
                    Route.Cart.path,
                    Route.Login.path,
                    Route.Register.path,
                    Route.Admin.path,
                    Route.AddProduct.path,
                    Route.ChangeProductImage.path,
                    Route.DeleteProduct.path
                )
                if (route !in hideOn) {
                    AppBottomBar(
                        currentRoute = route,
                        onHome = goHome,
                        onProfile = goProfile,
                        onSettings = goSettings
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.Splash.path,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Route.Splash.path) {
                    com.example.legacyframeapp.ui.screen.SplashScreen(onFinished = goHome)
                }
                // --- Tus Destinos de Navegación ---
                composable(Route.Home.path) {
                    // --- LLAMADA A HomeScreen (CORREGIDA) ---
                    HomeScreen(
                        onGoLogin = goLogin,
                        onGoRegister = goRegister,
                        onGoMolduras = goMolduras,
                        onGoCuadros = goCuadros,
                        products = products
                    )
                    // ------------------------------------
                }
                composable(Route.Profile.path) {
                    ProfileScreenVm(
                        vm = authViewModel,
                        onGoLogin = goLogin,
                        onGoRegister = goRegister,
                        onGoSettings = { navController.navigate(Route.Settings.path) },
                        onLogout = doLogout
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
                composable(Route.Molduras.path) {
                    MoldurasScreenVm(
                        vm = authViewModel,
                        onAddProduct = goAddProduct
                    )
                }
                composable(Route.Cuadros.path) {
                    CuadrosScreenVm(
                        vm = authViewModel,
                        onAddCuadro = goAddCuadro
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
                composable(Route.Settings.path) {
                    SettingsScreenVm(
                        vm = authViewModel,
                        onGoPurchases = { navController.navigate(Route.Purchases.path) },
                        onGoTerms = { navController.navigate(Route.Terms.path) },
                        onGoContact = { navController.navigate(Route.Contact.path) }
                    )
                }
                composable(Route.Purchases.path) {
                    PurchasesScreenVm(vm = authViewModel)
                }
                composable(Route.Terms.path) {
                    TermsScreen()
                }
                composable(Route.AddProduct.path) {
                    AddProductScreenVm(
                        vm = authViewModel,
                        onNavigateBack = goBack
                    )
                }
                composable(Route.AddCuadro.path) {
                    AddCuadroScreenVm(
                        vm = authViewModel,
                        onNavigateBack = goBack
                    )
                }

                composable(Route.Admin.path) {
                    AdminScreenVm(
                        vm = authViewModel,
                        onGoAddProduct = goAddProduct,
                        onGoAddCuadro = goAddCuadro,
                        onGoChangeImage = goChangeProductImage,
                        onGoDeleteProduct = goDeleteProduct,
                        onBack = goBack
                    )
                }

                composable(Route.ChangeProductImage.path) {
                    ChangeProductImageScreenVm(
                        vm = authViewModel,
                        onNavigateBack = goBack
                    )
                }

                composable(Route.DeleteProduct.path) {
                    DeleteProductScreenVm(
                        vm = authViewModel,
                        onNavigateBack = goBack
                    )
                }
            }
        }
    }
}