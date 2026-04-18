package com.turistgo.app.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.turistgo.app.core.auth.AuthState
import com.turistgo.app.core.auth.SessionViewModel
import com.turistgo.app.features.auth.ForgotPasswordScreen
import com.turistgo.app.features.auth.LoginScreen
import com.turistgo.app.features.auth.RegisterScreen
import com.turistgo.app.features.auth.ResetPasswordScreen
import com.turistgo.app.features.home.HomeScreen
import com.turistgo.app.features.feed.FeedScreen
import com.turistgo.app.features.moderator.ModeratorDashboard
import com.turistgo.app.features.moderator.ModeratorProfileScreen
import com.turistgo.app.features.post.CreatePostScreen
import com.turistgo.app.features.post.PostDetailScreen
import com.turistgo.app.features.profile.ProfileScreen
import com.turistgo.app.features.profile.EditProfileScreen
import com.turistgo.app.features.profile.SettingsScreen
import com.turistgo.app.features.notifications.NotificationsScreen
import com.turistgo.app.features.trips.TripsScreen
import com.turistgo.app.core.locale.AppStrings
import com.turistgo.app.core.locale.LanguageState

@Composable
fun AppNavigation(
    sessionViewModel: SessionViewModel = hiltViewModel()
) {
    val authState by sessionViewModel.authState.collectAsState()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Mostrar splash/cargando mientras se determina el estado de sesión
    if (authState is AuthState.Loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val lang by LanguageState.current
    val s = AppStrings.get(lang)

    val bottomBarDestinations = listOf(
        BottomNavItem(s.navHome,    MainRoutes.Feed,          Icons.Default.Home),
        BottomNavItem(s.navTrips,   MainRoutes.Trips,         Icons.Default.Map),
        BottomNavItem(s.navCreate,  MainRoutes.Create,        Icons.Default.Add),
        BottomNavItem(s.navAlerts,  MainRoutes.Notifications, Icons.Default.Notifications),
        BottomNavItem(s.navProfile, MainRoutes.Profile,       Icons.Default.Person)
    )

    val showBottomBar = bottomBarDestinations.any { item ->
        currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
    }

    Scaffold(
        bottomBar = {
        if (showBottomBar) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        bottomBarDestinations.forEachIndexed { index, item ->
                            val isSelected = currentDestination?.hierarchy?.any {
                                it.hasRoute(item.route::class)
                            } == true

                            if (index == 2) {
                                // Center FAB-style circle button for "Crear"
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                        .clickable {
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            } else {
                                // Regular nav item
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable {
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.label,
                                        fontSize = 10.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainRoutes.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Unauthenticated Graph
            composable<MainRoutes.Home> {
                HomeScreen(
                    onNavigateToLogin = { navController.navigate(MainRoutes.Login) },
                    onNavigateToRegister = { navController.navigate(MainRoutes.Register) },
                    onNavigateToFeed = {
                        navController.navigate(MainRoutes.Feed) {
                            popUpTo(MainRoutes.Home) { inclusive = true }
                        }
                    }
                )
            }

            composable<MainRoutes.Login> {
                LoginScreen(
                    onNavigateToFeed = {
                        navController.navigate(MainRoutes.Feed) {
                            popUpTo(MainRoutes.Home) { inclusive = true }
                        }
                    },
                    onNavigateToDashboard = {
                        navController.navigate(MainRoutes.ModeratorDashboard) {
                            popUpTo(MainRoutes.Home) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(MainRoutes.Register) },
                    onNavigateToForgotPassword = { navController.navigate(MainRoutes.ForgotPassword) }
                )
            }

            composable<MainRoutes.Register> {
                RegisterScreen(
                    onNavigateToCompleteProfile = { userId ->
                        navController.navigate(MainRoutes.CompleteProfile(userId)) {
                            popUpTo(MainRoutes.Home) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable<MainRoutes.CompleteProfile> { backStackEntry ->
                val profileArgs = backStackEntry.toRoute<MainRoutes.CompleteProfile>()
                com.turistgo.app.features.auth.CompleteProfileScreen(
                    userId = profileArgs.userId,
                    onNavigateToFeed = {
                        navController.navigate(MainRoutes.Feed) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable<MainRoutes.ForgotPassword> {
                ForgotPasswordScreen(
                    onNavigateToResetPassword = { navController.navigate(MainRoutes.ResetPassword) },
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable<MainRoutes.ResetPassword> {
                ResetPasswordScreen(
                    onNavigateToLogin = {
                        navController.navigate(MainRoutes.Login) {
                            popUpTo(MainRoutes.ForgotPassword) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // Authenticated Graph
            composable<MainRoutes.Feed> {
                FeedScreen(
                    onNavigateToDetail = { postId ->
                        navController.navigate(MainRoutes.PostDetail(postId))
                    }
                )
            }

            composable<MainRoutes.ModeratorDashboard> {
                ModeratorDashboard(
                    onLogout = {
                        navController.navigate(MainRoutes.Home) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onReviewPost = { postId -> navController.navigate(MainRoutes.ReviewPost(postId)) }
                )
            }
            
            composable<MainRoutes.ModeratorProfile> {
                ModeratorProfileScreen(
                    onLogout = {
                        navController.navigate(MainRoutes.Home) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable<MainRoutes.Create> { backStackEntry ->
                val mapResult = backStackEntry.savedStateHandle.get<String>("selected_location")
                CreatePostScreen(
                    mapResult = mapResult,
                    onConsumeMapResult = { backStackEntry.savedStateHandle.remove<String>("selected_location") },
                    onNavigateToMapPicker = { navController.navigate(MainRoutes.MapPicker) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable<MainRoutes.Profile> {
                ProfileScreen(
                    onNavigateToSettings = { navController.navigate(MainRoutes.Settings) },
                    onNavigateToEditProfile = { navController.navigate(MainRoutes.EditProfile) },
                    onLogout = {
                        navController.navigate(MainRoutes.Home) { popUpTo(0) { inclusive = true } }
                    },
                    onNavigateToBadges = { navController.navigate(MainRoutes.Badges) },
                    onNavigateToStats = { navController.navigate(MainRoutes.Stats) },
                    onNavigateToEditPost = { postId -> navController.navigate(MainRoutes.EditPost(postId)) }
                )
            }

            composable<MainRoutes.EditProfile> {
                EditProfileScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable<MainRoutes.PostDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<MainRoutes.PostDetail>()
                PostDetailScreen(
                    destinationId = route.postId,
                    onBack = { navController.popBackStack() }
                )
            }

            // Placeholders para rutas secundarias
            composable<MainRoutes.Trips> {
                TripsScreen(
                    onNavigateToDetail = { postId ->
                        navController.navigate(MainRoutes.PostDetail(postId))
                    }
                )
            }
            composable<MainRoutes.Notifications> { NotificationsScreen() }
            composable<MainRoutes.UserManagement> { /* Placeholder */ }
            composable<MainRoutes.EditPost> { /* Placeholder */ }
            composable<MainRoutes.ReviewPost> { /* Placeholder */ }
            composable<MainRoutes.Stats> { /* Placeholder */ }
            composable<MainRoutes.Badges> { /* Placeholder */ }
            composable<MainRoutes.MapPicker> { /* Placeholder */ }
            composable<MainRoutes.Settings> {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        navController.navigate(MainRoutes.Home) { popUpTo(0) { inclusive = true } }
                    }
                )
            }
        }
    }
}

data class BottomNavItem(
    val label: String,
    val route: Any,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
