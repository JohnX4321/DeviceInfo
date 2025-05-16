package com.thingsenz.devinfo.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.thingsenz.devinfo.utils.Prefs
import com.thingsenz.devinfo.utils.TableCell
import com.thingsenz.devinfo.utils.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Preview
@Composable
fun SocScreen() {
    val context = LocalContext.current
    val list = remember {
        Util.getSOCDetails(context)
    }

    var cpuCurrFreqMap = remember {
        mutableStateMapOf<String,String>()
    }

    var job: Job? = null

    if (Prefs.getShouldShowCoreFreq(context)) {
        LaunchedEffect(Unit) {
            job = launch(Dispatchers.IO) {
                while (isActive) {
                    Util.getCPUCurrFreqs(list["Cores"]?.toIntOrNull() ?: 0, cpuCurrFreqMap)
                    delay(2000L)
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        for (i in list) {
            Row(modifier = Modifier
                .fillMaxWidth()) {
                TableCell(text = i.key, weight = 0.5f)
                TableCell(text = i.value, weight = 0.5f)
            }
        }

        for (i in cpuCurrFreqMap.entries) {
            Row(modifier = Modifier
                .fillMaxWidth()) {
                TableCell(text = i.key, weight = 0.5f)
                TableCell(text = i.value, weight = 0.5f)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            job?.cancel()
        }
    }
}