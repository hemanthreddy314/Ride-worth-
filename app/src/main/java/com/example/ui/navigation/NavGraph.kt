package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fuelcalculator.ui.screens.FuelCalculatorScreen
import com.example.fuelcalculator.ui.viewmodel.FuelCalculatorViewModel
import com.example.maintenance.ui.screens.MaintenanceScreen
import com.example.maintenance.ui.viewmodel.MaintenanceViewModel
import com.example.ui.screens.comparison.VehicleCompareScreen
import com.example.ui.screens.history.HistoryScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.screens.valuation.VehicleValuationScreen
import com.example.ui.screens.valuation.result.ValuationResultScreen
import com.example.ui.theme.RideWorthTheme
import com.example.ui.viewmodel.HistoryViewModel
import com.example.ui.viewmodel.HomeViewModel
import com.example.ui.viewmodel.ValuationViewModel
import com.example.ui.viewmodel.VehicleCompareViewModel

import com.example.garage.ui.screens.GarageDetailScreen
import com.example.garage.ui.viewmodel.GarageViewModel
import com.example.history.ui.screens.UnifiedHistoryScreen
import com.example.history.ui.viewmodel.UnifiedHistoryViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.navArgument

import com.example.export.ui.ReportsManagerScreen
import com.example.export.viewmodel.PdfExportViewModel

object NavRoutes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val VALUATION = "valuation"
    const val VALUATION_RESULT = "valuation_result"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val COMPARE = "compare"
    const val FUEL_CALCULATOR = "fuel_calculator"
    const val MAINTENANCE_PLANNER = "maintenance_planner"
    const val GARAGE_DETAIL = "garage_detail/{vehicleId}"
    const val REPORTS_MANAGER = "reports_manager"
}

@Composable
fun AppNavGraph(
    homeViewModel: HomeViewModel,
    historyViewModel: HistoryViewModel,
    garageViewModel: GarageViewModel = viewModel(),
    unifiedHistoryViewModel: UnifiedHistoryViewModel = viewModel(),
    valuationViewModel: ValuationViewModel = viewModel(),
    compareViewModel: VehicleCompareViewModel = viewModel(),
    fuelCalculatorViewModel: FuelCalculatorViewModel = viewModel(),
    maintenanceViewModel: MaintenanceViewModel = viewModel(),
    pdfExportViewModel: PdfExportViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavRoutes.SPLASH
    val homeUiState by homeViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        garageViewModel.init(context)
        unifiedHistoryViewModel.init(context)
    }

    RideWorthTheme(vehicleType = homeUiState.selectedVehicleType) {
        Scaffold(
            bottomBar = {
                if (currentRoute != NavRoutes.SPLASH && currentRoute != NavRoutes.VALUATION && currentRoute != NavRoutes.VALUATION_RESULT && currentRoute != NavRoutes.COMPARE && currentRoute != NavRoutes.MAINTENANCE_PLANNER) {
                    LuxuryBottomBar(
                        currentRoute = currentRoute,
                        onNavigateToRoute = { route ->
                            navController.navigate(route) {
                                popUpTo(NavRoutes.HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = NavRoutes.SPLASH,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(NavRoutes.SPLASH) {
                    SplashScreen(
                        onSplashFinished = {
                            navController.navigate(NavRoutes.HOME) {
                                popUpTo(NavRoutes.SPLASH) { inclusive = true }
                            }
                        }
                    )
                }

                composable(NavRoutes.HOME) {
                    HomeScreen(
                        viewModel = homeViewModel,
                        garageViewModel = garageViewModel,
                        onNavigateToHistory = {
                            navController.navigate(NavRoutes.HISTORY)
                        },
                        onNavigateToSettings = {
                            navController.navigate(NavRoutes.SETTINGS)
                        },
                        onNavigateToValuation = {
                            valuationViewModel.updateVehicleType(homeUiState.selectedVehicleType)
                            navController.navigate(NavRoutes.VALUATION)
                        },
                        onNavigateToCompare = {
                            compareViewModel.updateVehicleType(homeUiState.selectedVehicleType)
                            navController.navigate(NavRoutes.COMPARE)
                        },
                        onNavigateToFuelCalculator = {
                            navController.navigate(NavRoutes.FUEL_CALCULATOR)
                        },
                        onNavigateToMaintenancePlanner = {
                            maintenanceViewModel.updateVehicleType(homeUiState.selectedVehicleType)
                            navController.navigate(NavRoutes.MAINTENANCE_PLANNER)
                        },
                        onNavigateToGarageDetail = { vehicleId ->
                            navController.navigate("garage_detail/$vehicleId")
                        }
                    )
                }

                composable(NavRoutes.GARAGE_DETAIL, arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })) { backStackEntry ->
                    val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
                    GarageDetailScreen(
                        vehicleId = vehicleId,
                        viewModel = garageViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onStartValuation = {
                            valuationViewModel.updateVehicleType(it.vehicleType)
                            navController.navigate(NavRoutes.VALUATION)
                        },
                        onStartFuelCalculator = {
                            navController.navigate(NavRoutes.FUEL_CALCULATOR)
                        },
                        onStartMaintenancePlanner = {
                            maintenanceViewModel.loadVehiclePreset(
                                manufacturer = it.manufacturer,
                                model = it.model,
                                variant = it.variant,
                                fuelType = it.fuelType,
                                year = it.year,
                                vehicleType = it.vehicleType,
                                transmission = it.transmission,
                                engineCc = 1200
                            )
                            navController.navigate(NavRoutes.MAINTENANCE_PLANNER)
                        },
                        onStartCompare = {
                            navController.navigate(NavRoutes.COMPARE)
                        }
                    )
                }

                composable(NavRoutes.VALUATION) {
                    val formState by valuationViewModel.formState.collectAsState()
                    VehicleValuationScreen(
                        viewModel = valuationViewModel,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onContinueToAiValuation = {
                            navController.navigate(NavRoutes.VALUATION_RESULT)
                        }
                    )
                }

                composable(NavRoutes.VALUATION_RESULT) {
                    val formState by valuationViewModel.formState.collectAsState()
                    ValuationResultScreen(
                        formState = formState,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onValueAnotherClick = {
                            navController.popBackStack(NavRoutes.VALUATION, inclusive = false)
                        },
                        onNavigateToCompare = {
                            compareViewModel.setInitialVehicleA(formState)
                            navController.navigate(NavRoutes.COMPARE)
                        },
                        onNavigateToMaintenance = {
                            maintenanceViewModel.loadVehiclePreset(
                                manufacturer = formState.brand,
                                model = formState.model,
                                variant = formState.variant,
                                fuelType = formState.fuelType,
                                year = formState.registrationYear,
                                vehicleType = homeUiState.selectedVehicleType,
                                transmission = formState.transmission,
                                engineCc = 1200
                            )
                            navController.navigate(NavRoutes.MAINTENANCE_PLANNER)
                        }
                    )
                }

                composable(NavRoutes.HISTORY) {
                    UnifiedHistoryScreen(
                        viewModel = unifiedHistoryViewModel,
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(NavRoutes.COMPARE) {
                    VehicleCompareScreen(
                        viewModel = compareViewModel,
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(NavRoutes.SETTINGS) {
                    SettingsScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(NavRoutes.FUEL_CALCULATOR) {
                    FuelCalculatorScreen(
                        viewModel = fuelCalculatorViewModel,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onNavigateToMaintenance = {
                            navController.navigate(NavRoutes.MAINTENANCE_PLANNER)
                        }
                    )
                }

                composable(NavRoutes.MAINTENANCE_PLANNER) {
                    MaintenanceScreen(
                        viewModel = maintenanceViewModel,
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(NavRoutes.REPORTS_MANAGER) {
                    ReportsManagerScreen(
                        viewModel = pdfExportViewModel,
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
