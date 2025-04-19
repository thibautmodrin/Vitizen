package com.vitizen.app.ui.screen

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
import com.vitizen.app.domain.model.User
import com.vitizen.app.ui.viewmodel.HomeViewModel
import com.vitizen.app.ui.viewmodel.TreatmentViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.pager.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.HorizontalDivider
import com.vitizen.app.ui.viewmodel.ParametresViewModel
import com.vitizen.app.ui.viewmodel.SuiviViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomePage(
    homeViewModel: HomeViewModel = hiltViewModel(),
    treatmentViewModel: TreatmentViewModel = hiltViewModel(),
    suiviViewModel: SuiviViewModel = hiltViewModel(),
    parametresViewModel: ParametresViewModel = hiltViewModel(),
    onNavigateToProfile: () -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var showProfileMenu by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val user by homeViewModel.user.collectAsState()

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
//                                DropdownMenuItem(
//                                    text = { Text(user!!.name) },
//                                    onClick = { showProfileMenu = false }
//                                )
                                DropdownMenuItem(
                                    text = { Text(user!!.email) },
                                    onClick = { showProfileMenu = false }
                                )
                                Divider()
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
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                TabItem.values().forEachIndexed { index, tabItem ->
                    NavigationBarItem(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (pagerState.currentPage == index) 
                                    tabItem.selectedIcon 
                                else 
                                    tabItem.unselectedIcon,
                                contentDescription = tabItem.title
                            )
                        },
                        label = { 
                            Text(
                                text = tabItem.title,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
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
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            when (page) {
                0 -> TreatmentScreen(treatmentViewModel, {})
                1 -> SuiviScreen(suiviViewModel)
                2 -> ParametresScreen(parametresViewModel)
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

