package com.thingsenz.deviceinfo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.thingsenz.deviceinfo.utils.TableCell
import com.thingsenz.deviceinfo.utils.Util

@Preview
@Composable
fun SoftwareScreen() {
    val list = remember {
        Util.getSoftwareDetails()
    }
    Column(modifier = Modifier.fillMaxSize()) {
        for (i in list) {
            Row {
                TableCell(text = i.first, weight = 0.5f)
                TableCell(text = i.second, weight = 0.5f)
            }
        }
    }
}