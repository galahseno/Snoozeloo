package id.dev.snoozeloo.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import androidx.navigation.toRoute
import id.dev.snoozeloo.alarm.presentation.list.AlarmListScreenRoot
import id.dev.snoozeloo.alarm.presentation.settings.AlarSettingsScreenRoot
import id.dev.snoozeloo.alarm.presentation.settings.AlarmSettingsViewModel
import id.dev.snoozeloo.alarm.presentation.settings.RingtoneSettingsScreenRoot
import id.dev.snoozeloo.core.presentation.ui.theme.backgroundColor
import id.dev.snoozeloo.core.presentation.ui.theme.gradientBackground
import org.koin.androidx.compose.navigation.koinNavViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun NavigationRoot(
    navController: NavHostController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.hierarchy

    Scaffold(
        floatingActionButton = {
            if (currentRoute?.any { it.hasRoute(Screen.AlarmHome.AlarmList::class) } == true) {
                IconButton(
                    onClick = {
                        navController.navigate(Screen.AlarmSettingsRoute.AlarmSettings(-1))
                    },
                    modifier = Modifier
                        .background(
                            brush = gradientBackground,
                            shape = CircleShape
                        )
                        .size(60.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = backgroundColor
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.AlarmHome,
            modifier = Modifier.padding(innerPadding)
        ) {
            homeGraph(navController)
            alarmSettingsGraph(navController)
        }
    }
}

private fun NavGraphBuilder.homeGraph(navController: NavHostController) {
    navigation<Screen.AlarmHome>(
        startDestination = Screen.AlarmHome.AlarmList
    ) {
        composable<Screen.AlarmHome.AlarmList> {
            AlarmListScreenRoot(
                onAlarmClick = {
                    it?.let {
                        navController.navigate(Screen.AlarmSettingsRoute.AlarmSettings(it))
                    }
                }
            )
        }
    }
}

private fun NavGraphBuilder.alarmSettingsGraph(navController: NavHostController) {
    navigation<Screen.AlarmSettingsRoute>(
        startDestination = Screen.AlarmSettingsRoute.AlarmSettings::class
    ) {
        composable<Screen.AlarmSettingsRoute.AlarmSettings> {
            val backStackEntry =
                remember { navController.getBackStackEntry(Screen.AlarmSettingsRoute) }
            val route = it.toRoute<Screen.AlarmSettingsRoute.AlarmSettings>()

            val viewModel: AlarmSettingsViewModel =
                koinNavViewModel(viewModelStoreOwner = backStackEntry, parameters = { parametersOf(route.id) })

            AlarSettingsScreenRoot(
                onBackClick = {
                    navController.navigate(Screen.AlarmHome.AlarmList) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                },
                onRingtoneClick = {
                    navController.navigate(Screen.AlarmSettingsRoute.RingtoneSettings)
                },
                onSuccessCreateOrUpdateAlarm = {
                    navController.navigateUp()
                },
                viewModel = viewModel
            )
        }
        composable<Screen.AlarmSettingsRoute.RingtoneSettings> {
            val backStackEntry =
                remember { navController.getBackStackEntry(Screen.AlarmSettingsRoute) }
            val viewModel: AlarmSettingsViewModel =
                koinNavViewModel(viewModelStoreOwner = backStackEntry)

            RingtoneSettingsScreenRoot(
                onBackClick = {
                    navController.navigateUp()
                },
                viewModel = viewModel
            )
        }
    }
}