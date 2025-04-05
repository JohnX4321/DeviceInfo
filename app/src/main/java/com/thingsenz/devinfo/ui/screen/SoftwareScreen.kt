package com.thingsenz.devinfo.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.thingsenz.devinfo.utils.TableCell
import com.thingsenz.devinfo.utils.Util

@Preview
@Composable
fun SoftwareScreen() {
    val list = Util.getSoftwareDetails(LocalContext.current)

    Column(modifier = Modifier.fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        for (i in list) {
            Row {
                TableCell(text = i.first, weight = 0.5f)
                TableCell(text = i.second, weight = 0.5f)
            }
        }
    }
}