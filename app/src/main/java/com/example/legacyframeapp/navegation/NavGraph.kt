package com.example.legacyframeapp.navegation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.legacyframeapp.ui.screen.*
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

// Items de la barra inferior (Solo usuario normal)
sealed class Screen(val route: String, val label: String, val iconFilled: ImageVector, val iconOutlined: ImageVector) {
    object Home : Screen("home", "Inicio", Icons.Filled.Home, Icons.Outlined.Home)
    object Cart : Screen("cart", "Carrito", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart)
    object Profile : Screen("profile", "Perfil", Icons.Filled.Person, Icons.Outlined.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val session by authViewModel.session.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    // Color Dinámico
    val accentHex by authViewModel.accentColor.collectAsStateWithLifecycle()
    val dynamicColor = try { Color(android.graphics.Color.parseColor(accentHex)) } catch (e: Exception) { Color(0xFF8B5C2A) }

    // Ocultar barras en pantallas de "trámite"
    val showBars = currentRoute !in listOf("login", "register", "add_product", "add_cuadro", "delete_product", "delete_cuadro")

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showBars,
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    // Cabecera
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp).padding(16.dp), contentAlignment = Alignment.CenterStart) {
                        Column {
                            Text("Legacy Frames", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = dynamicColor)
                            if (session.isLoggedIn) Text("Hola, ${session.currentUser?.nombre}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    HorizontalDivider()

                    // --- MENÚ PRINCIPAL ---
                    DrawerItem(Icons.Default.Home, "Inicio", currentRoute == "home") { navController.navigate("home"); scope.launch { drawerState.close() } }
                    DrawerItem(Icons.Default.ViewCarousel, "Molduras", currentRoute == "molduras") { navController.navigate("molduras"); scope.launch { drawerState.close() } }
                    DrawerItem(Icons.Default.Photo, "Cuadros", currentRoute == "cuadros") { navController.navigate("cuadros"); scope.launch { drawerState.close() } }
                    DrawerItem(Icons.Default.ShoppingCart, "Carrito", currentRoute == "cart") { navController.navigate("cart"); scope.launch { drawerState.close() } }

                    if (session.isLoggedIn) {
                        DrawerItem(Icons.Default.ShoppingBag, "Mis Compras", currentRoute == "purchases") { navController.navigate("purchases"); scope.launch { drawerState.close() } }
                    }

                    // --- ELIMINADO: DrawerItem de "Contacto" (Ahora solo está en Configuración) ---

                    DrawerItem(Icons.Default.Description, "Términos", currentRoute == "terms") { navController.navigate("terms"); scope.launch { drawerState.close() } }

                    // --- SECCIÓN ADMIN ---
                    if (session.isAdmin) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text("Administración", modifier = Modifier.padding(start = 28.dp, bottom = 8.dp), style = MaterialTheme.typography.labelSmall, color = Color.Gray)

                        DrawerItem(Icons.Default.AdminPanelSettings, "Panel Admin", currentRoute == "admin_panel") {
                            navController.navigate("admin_panel")
                            scope.launch { drawerState.close() }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    DrawerItem(Icons.Default.Settings, "Configuración", currentRoute == "settings") { navController.navigate("settings"); scope.launch { drawerState.close() } }

                    if (!session.isLoggedIn) {
                        DrawerItem(Icons.Default.Login, "Iniciar sesión", currentRoute == "login") { navController.navigate("login"); scope.launch { drawerState.close() } }
                        DrawerItem(Icons.Default.PersonAdd, "Registro", currentRoute == "register") { navController.navigate("register"); scope.launch { drawerState.close() } }
                    } else {
                        DrawerItem(Icons.Default.ExitToApp, "Cerrar Sesión", false) { authViewModel.logout(); navController.navigate("login"); scope.launch { drawerState.close() } }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (showBars) {
                    CenterAlignedTopAppBar(
                        title = { Text("Legacy Frames", fontWeight = FontWeight.Bold, color = Color.White) },
                        navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, null, tint = Color.White) } },
                        actions = { IconButton(onClick = { navController.navigate("cart") }) { Icon(Icons.Default.ShoppingCart, null, tint = Color.White) } },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = dynamicColor)
                    )
                }
            },
            bottomBar = {
                if (showBars) {
                    NavigationBar {
                        listOf(Screen.Home, Screen.Cart, Screen.Profile).forEach { screen ->
                            NavigationBarItem(
                                icon = { Icon(if (currentRoute == screen.route) screen.iconFilled else screen.iconOutlined, null) },
                                label = { Text(screen.label) },
                                selected = currentRoute == screen.route,
                                onClick = { navController.navigate(screen.route) },
                                colors = NavigationBarItemDefaults.colors(selectedIconColor = dynamicColor, indicatorColor = dynamicColor.copy(alpha = 0.2f))
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(navController, startDestination = "home", modifier = Modifier.padding(innerPadding)) {
                // Rutas
                composable("home") {
                    HomeScreen(
                        vm = authViewModel,
                        onNavigateToMolduras = { navController.navigate("molduras") },
                        onNavigateToCuadros = { navController.navigate("cuadros") }
                    )
                }

                composable("molduras") { MoldurasScreenVm(vm = authViewModel, onAddProduct = {}) }
                composable("cuadros") { CuadrosScreenVm(vm = authViewModel, onAddCuadro = {}) }
                composable("cart") { CartScreen(vm = authViewModel, onGoToPay = { authViewModel.recordOrder(authViewModel.cartItems.value, authViewModel.cartTotal.value); navController.navigate("home") }) }
                composable("profile") { ProfileScreen(vm = authViewModel, onLogout = { authViewModel.logout(); navController.navigate("login") }) }
                composable("settings") { SettingsScreen(vm = authViewModel, onBack = { navController.popBackStack() }) }
                composable("purchases") { PurchasesScreen(vm = authViewModel) }
                composable("contact") { ContactScreen(vm = authViewModel) }
                composable("terms") { TermsScreen(onBack = { navController.popBackStack() }) }
                composable("login") { LoginScreen(vm = authViewModel, onLoginSuccess = { navController.navigate("home") }, onGoRegister = { navController.navigate("register") }) }
                composable("register") { RegisterScreen(vm = authViewModel, onRegisterSuccess = { navController.navigate("login") }, onGoLogin = { navController.navigate("login") }) }

                // Admin
                composable("admin_panel") {
                    AdminScreen(
                        onGoAddProduct = { navController.navigate("add_product") },
                        onGoAddCuadro = { navController.navigate("add_cuadro") },
                        onGoDeleteProduct = { navController.navigate("delete_product") },
                        onGoDeleteCuadro = { navController.navigate("delete_cuadro") }
                    )
                }
                composable("add_product") { AddProductScreen(vm = authViewModel, onBack = { navController.popBackStack() }) }
                composable("add_cuadro") { AddCuadroScreen(vm = authViewModel, onBack = { navController.popBackStack() }) }
                composable("delete_product") { DeleteProductScreen(vm = authViewModel, onBack = { navController.popBackStack() }) }
                composable("delete_cuadro") { DeleteCuadroScreen(vm = authViewModel, onBack = { navController.popBackStack() }) }
            }
        }
    }
}

@Composable
fun DrawerItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    NavigationDrawerItem(icon = { Icon(icon, null) }, label = { Text(label) }, selected = selected, onClick = onClick, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
}