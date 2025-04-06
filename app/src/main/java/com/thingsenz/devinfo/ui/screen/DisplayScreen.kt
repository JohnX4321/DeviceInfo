package com.thingsenz.devinfo.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
fun DisplayScreen() {
    val displayList = Util.getDisplayDetails(LocalContext.current)
    Column(Modifier.fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        for (i in displayList) {
            Row(modifier = Modifier
                .fillMaxWidth()) {
                TableCell(text = i.key, weight = 0.5f)
                TableCell(text = i.value, weight = 0.5f)
            }
        }
    }
}