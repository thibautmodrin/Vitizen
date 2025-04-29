package com.vitizen.app.presentation.screen.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitizen.app.R
import com.vitizen.app.presentation.screen.home.section.record.TreatmentViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.pager.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.ExperimentalFoundationApi
import com.vitizen.app.presentation.screen.home.section.settings.ParametresViewModel
import com.vitizen.app.presentation.screen.home.section.followup.SuiviViewModel
import androidx.navigation.NavController
import com.vitizen.app.presentation.navigation.NavigationRoutes
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import com.vitizen.app.data.local.preference.FirstConnectionManager
import com.vitizen.app.presentation.screen.home.section.settings.ParametresScreen
import com.vitizen.app.presentation.screen.home.section.followup.SuiviScreen
import com.vitizen.app.presentation.screen.home.section.record.TreatmentScreen
import com.vitizen.app.presentation.components.ChatFAB
import com.vitizen.app.presentation.screen.chat.ChatDialog


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomePage(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    treatmentViewModel: TreatmentViewModel = hiltViewModel(),
    suiviViewModel: SuiviViewModel = hiltViewModel(),
    parametresViewModel: ParametresViewModel,
    onNavigateToProfile: () -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var showProfileMenu by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showChatDialog by remember { mutableStateOf(false) }
    val user by homeViewModel.user.collectAsState()
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    
    LaunchedEffect(Unit) {
        val isFirst = FirstConnectionManager.isFirstConnection(context)
        Log.d("HomePage", "KEY_FIRST_CONNECTION value: $isFirst")
        if (isFirst) {
            Log.d("HomePage", "Showing first connection dialog")
            scope.launch { pagerState.animateScrollToPage(2) }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Déconnexion") },
            text = { Text("Êtes-vous sûr de vouloir vous déconnecter ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        showProfileMenu = false
                        homeViewModel.logout()
                        onSignOut()
                    }
                ) {
                    Text("Se déconnecter")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Annuler")
                }
            }
        )
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.vitizen_logo_v7),
                                contentDescription = "Logo Vitizen",
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(120.dp)
                            )
                        }
                    },
                    actions = {
                        Box {
                            IconButton(onClick = { showProfileMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profil"
                                )
                            }
                            DropdownMenu(
                                expanded = showProfileMenu,
                                onDismissRequest = { showProfileMenu = false }
                            ) {
                                if (user != null) {
                                    DropdownMenuItem(
                                        text = { Text(user!!.email) },
                                        onClick = { showProfileMenu = false }
                                    )
                                    HorizontalDivider()
                                }
                                DropdownMenuItem(
                                    text = { Text("Se déconnecter") },
                                    onClick = {
                                        showProfileMenu = false
                                        showLogoutDialog = true
                                    }
                                )
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            bottomBar = {
                if (!imeVisible) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        NavigationBarItem(
                            icon = { Icon(Icons.Outlined.Science, contentDescription = "Traitement") },
                            label = { Text("Traitement") },
                            selected = pagerState.currentPage == 0,
                            onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                            )
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Outlined.ShowChart, contentDescription = "Suivi") },
                            label = { Text("Suivi") },
                            selected = pagerState.currentPage == 1,
                            onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                            )
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Outlined.Settings, contentDescription = "Paramètres") },
                            label = { Text("Paramètres") },
                            selected = pagerState.currentPage == 2,
                            onClick = { scope.launch { pagerState.animateScrollToPage(2) } },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                            )
                        )
                    }
                }
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .then(if (imeVisible) Modifier.imePadding() else Modifier)
                ) { page ->
                    when (page) {
                        0 -> TreatmentScreen(treatmentViewModel, {})
                        1 -> SuiviScreen(suiviViewModel)
                        2 -> ParametresScreen(
                            viewModel = parametresViewModel,
                            onNavigateToForm = { route ->
                                when {
                                    route == "operateur" -> navController.navigate(NavigationRoutes.OPERATEUR_FORM)
                                    route.startsWith("operateur/") -> navController.navigate(
                                        "${NavigationRoutes.OPERATEUR_FORM}/${
                                            route.substringAfter(
                                                "operateur/"
                                            )
                                        }"
                                    )

                                    route == "generalInfo" -> navController.navigate(
                                        NavigationRoutes.GENERAL_INFO_FORM
                                    )

                                    route.startsWith("generalInfo/") -> navController.navigate(
                                        "${NavigationRoutes.GENERAL_INFO_FORM}/${
                                            route.substringAfter(
                                                "generalInfo/"
                                            )
                                        }"
                                    )

                                    route == "pulverisateur" -> navController.navigate(
                                        NavigationRoutes.PULVERISATEUR_FORM
                                    )

                                    route.startsWith("pulverisateur/") -> navController.navigate(
                                        "${NavigationRoutes.PULVERISATEUR_FORM}/${
                                            route.substringAfter(
                                                "pulverisateur/"
                                            )
                                        }"
                                    )

                                    route == "parcelle" -> navController.navigate(NavigationRoutes.PARCELLE_FORM)
                                    route.startsWith("parcelle/") -> navController.navigate(
                                        "${NavigationRoutes.PARCELLE_FORM}/${
                                            route.substringAfter(
                                                "parcelle/"
                                            )
                                        }"
                                    )

                                    else -> {}
                                }
                            }
                        )
                    }
                }

                // Chat FAB
                ChatFAB(
                    onClick = { showChatDialog = true },
                    modifier = Modifier.align(Alignment.BottomEnd)
                )

                // Chat Dialog
                if (showChatDialog) {
                    ChatDialog(
                        viewModel = hiltViewModel(),
                        onDismiss = { showChatDialog = false }
                    )
                }
            }
        }
    }
}

enum class TabItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    Traitement(
        "Traitement",
        Icons.Filled.Science,
        Icons.Outlined.Science
    ),
    Suivi(
        "Suivi",
        Icons.Filled.Timeline,
        Icons.Outlined.Timeline
    ),
    Parametres(
        "Paramètres",
        Icons.Filled.Settings,
        Icons.Outlined.Settings
    )
}

