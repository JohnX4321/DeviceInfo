package com.thingsenz.deviceinfo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.thingsenz.deviceinfo.utils.TableCell
import com.thingsenz.deviceinfo.utils.Util

@Preview
@Composable
fun DeviceScreen() {
    val list = remember {
        Util.getAllDeviceDetails()
    }
    Column(modifier = Modifier.fillMaxSize()) {
        for (i in list) {
            Row(modifier = Modifier
                .fillMaxWidth()) {
                TableCell(text = i.key, weight = 0.5f)
                TableCell(text = i.value, weight = 0.5f)
            }
        }
    }

}