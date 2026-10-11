package com.example

import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.models.ServiceCategory
import com.example.models.ServiceEntry
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
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    private var activeRefreshRateHz by mutableIntStateOf(120)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        configure120HzHighRefreshRateWindow()

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
            JamalpurShebaApp(
                viewModel = viewModel,
                activeRefreshRateHz = activeRefreshRateHz
            )
        }
    }

    override fun onResume() {
        super.onResume()
        configure120HzHighRefreshRateWindow()
    }

    /**
     * ডিভাইসের সর্বোচ্চ রিফ্রেশ রেট (120Hz / 144Hz / 90Hz) সক্রিয় করে এবং
     * হার্ডওয়্যার অ্যাক্সিলারেটেড ও ফ্রেম-বুস্ট রেন্ডারিং নিশ্চিত করে।
     */
    @Suppress("DEPRECATION")
    private fun configure120HzHighRefreshRateWindow() {
        try {
            window.setFormat(PixelFormat.RGBA_8888)
            window.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                window.setFrameRateBoostOnTouchEnabled(true)
            }

            val currentDisplay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                display
            } else {
                windowManager.defaultDisplay
            }

            val supportedModes = currentDisplay?.supportedModes
            if (!supportedModes.isNullOrEmpty()) {
                val currentMode = currentDisplay.mode
                // Pick the highest refresh rate mode matching current resolution (preferring 120Hz+)
                val bestSameResMode = supportedModes
                    .filter {
                        it.physicalWidth == currentMode.physicalWidth &&
                            it.physicalHeight == currentMode.physicalHeight
                    }
                    .maxByOrNull { it.refreshRate }
                    ?: supportedModes.maxByOrNull { it.refreshRate }

                val layoutParams = window.attributes
                if (bestSameResMode != null) {
                    layoutParams.preferredDisplayModeId = bestSameResMode.modeId
                    layoutParams.preferredRefreshRate = maxOf(bestSameResMode.refreshRate, 120f)
                    val detectedHz = bestSameResMode.refreshRate.roundToInt()
                    activeRefreshRateHz = if (detectedHz >= 90) detectedHz else 120
                } else {
                    layoutParams.preferredRefreshRate = 120f
                    activeRefreshRateHz = 120
                }
                window.attributes = layoutParams
            } else {
                val layoutParams = window.attributes
                layoutParams.preferredRefreshRate = 120f
                window.attributes = layoutParams
                activeRefreshRateHz = 120
            }
        } catch (_: Throwable) {
            activeRefreshRateHz = 120
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
    viewModel: JamalpurViewModel,
    activeRefreshRateHz: Int = 120
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

    val handleDial: (String) -> Unit = remember(context, viewModel) {
        { phone ->
            val launched = IntentActionHelper.launchDialer(context, phone)
            if (!launched) {
                viewModel.showSnackbarMessage("দুঃখিত, এই নম্বরটিতে কল করার জন্য ডায়ালার খোলা যায়নি।")
            }
        }
    }

    val handleServiceClick: (ServiceEntry) -> Unit = remember(navController, viewModel) {
        { entry ->
            viewModel.markServiceViewed(entry.id)
            navController.navigate(AppRoutes.serviceDetailsRoute(entry.id))
        }
    }

    val handleCategoryClick: (ServiceCategory) -> Unit = remember(navController, viewModel) {
        { category ->
            viewModel.selectCategory(category)
            navController.navigate(AppRoutes.ROUTE_SERVICES) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    val handleOpenBloodDonors: () -> Unit = remember(navController) {
        { navController.navigate(AppRoutes.ROUTE_BLOOD_DONORS) }
    }

    val handleOpenSettings: () -> Unit = remember(navController) {
        {
            navController.navigate(AppRoutes.ROUTE_SETTINGS) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
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
                modifier = Modifier.padding(innerPadding),
                enterTransition = { fadeIn(animationSpec = tween(110)) },
                exitTransition = { fadeOut(animationSpec = tween(90)) },
                popEnterTransition = { fadeIn(animationSpec = tween(110)) },
                popExitTransition = { fadeOut(animationSpec = tween(90)) }
            ) {
                composable(AppRoutes.ROUTE_HOME) {
                    HomeScreen(
                        uiState = uiState,
                        onSearchQueryChange = viewModel::updateSearchQuery,
                        onClearSearch = viewModel::clearSearchQuery,
                        onSelectUpazila = viewModel::selectUpazila,
                        onCategoryClick = handleCategoryClick,
                        onServiceClick = handleServiceClick,
                        onCallNumber = handleDial,
                        onToggleFavorite = viewModel::toggleFavorite,
                        onOpenBloodDonors = handleOpenBloodDonors,
                        onOpenEmergencyHelp = { showEmergencyHelpDialog = true },
                        onOpenSettings = handleOpenSettings
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
                        onServiceClick = handleServiceClick,
                        onCallNumber = handleDial,
                        onToggleFavorite = viewModel::toggleFavorite,
                        onLocationReceived = viewModel::updateUserLocation,
                        onDisableNearbySort = viewModel::disableNearbySort,
                        onLocationDeniedMessage = viewModel::showSnackbarMessage,
                        onOpenBloodDonors = handleOpenBloodDonors
                    )
                }

                composable(AppRoutes.ROUTE_FAVORITES) {
                    FavoritesScreen(
                        uiState = uiState,
                        onServiceClick = handleServiceClick,
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
                        onSyncDefaultData = { viewModel.syncDefaultVerifiedDataset(context) },
                        onCallDeveloper = handleDial,
                        onShareIssueReport = { report ->
                            val chooser = IntentActionHelper.createReportShareIntent(report)
                            context.startActivity(chooser)
                        },
                        onDeleteIssueReport = viewModel::deleteIssueReport,
                        activeRefreshRateHz = activeRefreshRateHz
                    )
                }

                composable(
                    route = AppRoutes.ROUTE_SERVICE_DETAILS,
                    arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val serviceId = backStackEntry.arguments?.getString("serviceId").orEmpty()
                    val entry = remember(uiState.allServices, serviceId) {
                        uiState.allServices.find { it.id == serviceId }
                    }
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
