package com.thingsenz.deviceinfo

import android.content.Intent
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LeadingIconTab
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.thingsenz.deviceinfo.ui.theme.DeviceInfoTheme
import com.thingsenz.deviceinfo.utils.Prefs
import com.thingsenz.deviceinfo.utils.Util
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkGPUDetails()
        setContent {
            DeviceInfoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Tabs(tabs = listOf(TabItem.Device,TabItem.Software,TabItem.SOC))
                }
            }
        }

    }

    private fun checkGPUDetails(){
        if (Prefs.isFirstRun(this)) {
            Prefs.setFirstRun(this,true)
            startActivity(Intent(this,DummyActivity::class.java))
        }
    }

}


@Composable
fun Tabs(tabs: List<TabItem>) {
    var tabIndex by remember {
        mutableStateOf(0)
    }
    Column(modifier = Modifier.fillMaxWidth()) {
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