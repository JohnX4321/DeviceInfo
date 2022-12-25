package com.thingsenz.deviceinfo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LeadingIconTab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.thingsenz.deviceinfo.ui.theme.DeviceInfoTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeviceInfoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun Tabs(tabs: List<TabItem>,pagerState: PagerState) {
    val scope = rememberCoroutineScope()
    TabRow(selectedTabIndex = pagerState.currentPage, backgroundColor = MaterialTheme.colorScheme.primary, contentColor = Color.White, indicator = { tp ->
        TabRowDefaults.Indicator(Modifier.pagerTabIndicatorOffset(pagerState,tp))
    }) {
        tabs.forEachIndexed { index, tabItem ->
            LeadingIconTab(
                selected = pagerState.currentPage == index,
                onClick = {
                          scope.launch { pagerState.animateScrollToPage(index) }
                },
                text = {
                    Text(text = tabItem.title)
                },
            icon = {
                Icon(painter = painterResource(id = tabItem.icon), contentDescription = "")
            })
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DeviceInfoTheme {
        Greeting("Android")
    }
}