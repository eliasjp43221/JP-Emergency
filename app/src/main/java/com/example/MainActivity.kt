package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.components.EmergencyHelpDialog
import com.example.database.JamalpurDatabase
import com.example.navigation.AppRoutes
import com.example.repositories.JamalpurRepository
import com.example.repositories.ThemeMode
import com.example.repositories.UserPreferencesRepository
import com.example.screens.BloodDonorScreen
import com.example.screens.FavoritesScreen
import com.example.screens.HomeScreen
import com.example.screens.ServiceDetailsScreen
import com.example.screens.ServicesDirectoryScreen
import com.example.screens.SettingsAndDataScreen
import com.example.ui.theme.JamalpurShebaTheme
import com.example.utils.IntentActionHelper
import com.example.viewmodels.JamalpurViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = JamalpurDatabase.getDatabase(applicationContext)
        val repository = JamalpurRepository(
            serviceDao = database.serviceDao(),
            bloodDonorDao = database.bloodDonorDao(),
            issueReportDao = database.issueReportDao()
        )
        val preferencesRepository = UserPreferencesRepository(applicationContext)

        setContent {
            val viewModel: JamalpurViewModel = viewModel(
                factory = JamalpurViewModel.provideFactory(
                    repository = repository,
                    preferencesRepository = preferencesRepository,
                    appContext = applicationContext
                )
            )
            JamalpurShebaApp(viewModel = viewModel)
        }
    }
}

private data class BottomNavDestination(
    val route: String,
    val labelBn: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val bottomNavItems = listOf(
    BottomNavDestination(
        route = AppRoutes.ROUTE_HOME,
        labelBn = "হোম",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "nav_tab_home"
    ),
    BottomNavDestination(
        route = AppRoutes.ROUTE_SERVICES,
        labelBn = "সেবাসমূহ",
        selectedIcon = Icons.Filled.GridView,
        unselectedIcon = Icons.Outlined.GridView,
        testTag = "nav_tab_services"
    ),
    BottomNavDestination(
        route = AppRoutes.ROUTE_FAVORITES,
        labelBn = "প্রিয়",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder,
        testTag = "nav_tab_favorites"
    ),
    BottomNavDestination(
        route = AppRoutes.ROUTE_SETTINGS,
        labelBn = "সেটিংস",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        testTag = "nav_tab_settings"
    )
)

@Composable
fun JamalpurShebaApp(
    viewModel: JamalpurViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    var showEmergencyHelpDialog by remember { mutableStateOf(false) }

    val isDarkTheme = when (uiState.preferences.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    LaunchedEffect(uiState.snackbarMessageBn) {
        val message = uiState.snackbarMessageBn
        if (!message.isNullOrBlank()) {
            snackbarHostState.showSnackbar(message)
            viewModel.dismissSnackbar()
        }
    }

    val handleDial: (String) -> Unit = { phone ->
        val launched = IntentActionHelper.launchDialer(context, phone)
        if (!launched) {
            viewModel.showSnackbarMessage("দুঃখিত, এই নম্বরটিতে কল করার জন্য ডায়ালার খোলা যায়নি।")
        }
    }

    JamalpurShebaTheme(darkTheme = isDarkTheme) {
        if (showEmergencyHelpDialog) {
            EmergencyHelpDialog(
                onDismiss = { showEmergencyHelpDialog = false },
                onDialNumber = { phone ->
                    showEmergencyHelpDialog = false
                    handleDial(phone)
                }
            )
        }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val showBottomBar = bottomNavItems.any { it.route == currentRoute }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        modifier = Modifier.testTag("bottom_navigation_bar")
                    ) {
                        for (item in bottomNavItems) {
                            val selected = currentRoute == item.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    if (currentRoute != item.route) {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.labelBn
                                    )
                                },
                                label = { Text(item.labelBn) },
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AppRoutes.ROUTE_HOME,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(AppRoutes.ROUTE_HOME) {
                    HomeScreen(
                        uiState = uiState,
                        onSearchQueryChange = viewModel::updateSearchQuery,
                        onClearSearch = viewModel::clearSearchQuery,
                        onSelectUpazila = viewModel::selectUpazila,
                        onCategoryClick = { category ->
                            viewModel.selectCategory(category)
                            navController.navigate(AppRoutes.ROUTE_SERVICES) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onServiceClick = { entry ->
                            viewModel.markServiceViewed(entry.id)
                            navController.navigate(AppRoutes.serviceDetailsRoute(entry.id))
                        },
                        onCallNumber = handleDial,
                        onToggleFavorite = viewModel::toggleFavorite,
                        onOpenBloodDonors = {
                            navController.navigate(AppRoutes.ROUTE_BLOOD_DONORS)
                        },
                        onOpenEmergencyHelp = { showEmergencyHelpDialog = true },
                        onOpenSettings = {
                            navController.navigate(AppRoutes.ROUTE_SETTINGS) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }

                composable(AppRoutes.ROUTE_SERVICES) {
                    ServicesDirectoryScreen(
                        uiState = uiState,
                        onSearchQueryChange = viewModel::updateSearchQuery,
                        onClearSearch = viewModel::clearSearchQuery,
                        onSelectCategory = viewModel::selectCategory,
                        onSelectUpazila = viewModel::selectUpazila,
                        onResetFilters = viewModel::resetFilters,
                        onServiceClick = { entry ->
                            viewModel.markServiceViewed(entry.id)
                            navController.navigate(AppRoutes.serviceDetailsRoute(entry.id))
                        },
                        onCallNumber = handleDial,
                        onToggleFavorite = viewModel::toggleFavorite,
                        onLocationReceived = viewModel::updateUserLocation,
                        onDisableNearbySort = viewModel::disableNearbySort,
                        onLocationDeniedMessage = viewModel::showSnackbarMessage,
                        onOpenBloodDonors = {
                            navController.navigate(AppRoutes.ROUTE_BLOOD_DONORS)
                        },
                        onOpenDataUpdate = {
                            navController.navigate(AppRoutes.ROUTE_SETTINGS)
                        }
                    )
                }

                composable(AppRoutes.ROUTE_FAVORITES) {
                    FavoritesScreen(
                        uiState = uiState,
                        onServiceClick = { entry ->
                            viewModel.markServiceViewed(entry.id)
                            navController.navigate(AppRoutes.serviceDetailsRoute(entry.id))
                        },
                        onCallNumber = handleDial,
                        onToggleFavorite = viewModel::toggleFavorite,
                        onClearRecentHistory = viewModel::clearRecentHistory,
                        onExploreServicesClick = {
                            navController.navigate(AppRoutes.ROUTE_SERVICES) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(AppRoutes.ROUTE_SETTINGS) {
                    SettingsAndDataScreen(
                        uiState = uiState,
                        onSetThemeMode = viewModel::setThemeMode,
                        onSetDefaultUpazila = viewModel::setDefaultUpazila,
                        onSetShowSampleDonors = viewModel::setShowSampleDonors,
                        onImportJson = viewModel::importJsonData,
                        onSyncDefaultData = { viewModel.syncDefaultVerifiedDataset(context) },
                        onShareIssueReport = { report ->
                            val chooser = IntentActionHelper.createReportShareIntent(report)
                            context.startActivity(chooser)
                        },
                        onDeleteIssueReport = viewModel::deleteIssueReport
                    )
                }

                composable(
                    route = AppRoutes.ROUTE_SERVICE_DETAILS,
                    arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val serviceId = backStackEntry.arguments?.getString("serviceId").orEmpty()
                    val entry = uiState.allServices.find { it.id == serviceId }
                    ServiceDetailsScreen(
                        entry = entry,
                        onBackClick = { navController.popBackStack() },
                        onCallNumber = handleDial,
                        onOpenMap = { service ->
                            val ok = IntentActionHelper.launchMap(context, service)
                            if (!ok) {
                                viewModel.showSnackbarMessage("ম্যাপ অ্যাপ্লিকেশন খোলা যায়নি।")
                            }
                        },
                        onShareService = { service ->
                            IntentActionHelper.launchShare(context, service)
                        },
                        onToggleFavorite = viewModel::toggleFavorite,
                        onSubmitIssueReport = viewModel::submitIssueReport,
                        onShareSavedReport = { report ->
                            val chooser = IntentActionHelper.createReportShareIntent(report)
                            context.startActivity(chooser)
                        }
                    )
                }

                composable(AppRoutes.ROUTE_BLOOD_DONORS) {
                    BloodDonorScreen(
                        uiState = uiState,
                        onBackClick = { navController.popBackStack() },
                        onSelectBloodGroup = viewModel::selectBloodGroup,
                        onSelectUpazila = viewModel::selectUpazila,
                        onToggleSampleMode = viewModel::setShowSampleDonors,
                        onAddVoluntaryDonor = viewModel::addVoluntaryBloodDonor,
                        onRemoveDonor = viewModel::removeBloodDonor,
                        onCallNumber = handleDial
                    )
                }
            }
        }
    }
}
