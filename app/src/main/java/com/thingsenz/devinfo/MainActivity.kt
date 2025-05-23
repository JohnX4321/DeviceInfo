package com.thingsenz.devinfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import com.thingsenz.devinfo.ui.components.TabItem
import com.thingsenz.devinfo.ui.nav.NavigationStack
import com.thingsenz.devinfo.ui.nav.Screen
import com.thingsenz.devinfo.ui.theme.DevInfoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            DevInfoTheme {
                NavigationStack()
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    Scaffold(modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.app_name),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = {
                        //launchSettings(context)
                        navController.navigate(Screen.Settings.route)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        Tabs(tabs = listOf(TabItem.Device,TabItem.Software,TabItem.SOC, TabItem.Storage,
            TabItem.Display), padding = innerPadding)
    }

}

@Composable
fun Tabs(tabs: List<TabItem>, padding: PaddingValues) {
    var tabIndex by remember {
        mutableIntStateOf(0)
    }
    Column(modifier = Modifier.fillMaxWidth()
        .padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding())) {
        ScrollableTabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, tabItem ->
                Tab(text = { Text(text = tabItem.title)},
                    onClick = {tabIndex = index},
                    selected = tabIndex==index,
                )
            }
        }
        tabs[tabIndex].layout.invoke()
    }
}
