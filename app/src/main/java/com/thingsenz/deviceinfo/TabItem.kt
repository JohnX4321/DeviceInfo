package com.thingsenz.deviceinfo

import androidx.compose.runtime.Composable
import com.thingsenz.deviceinfo.ui.components.DeviceScreen
import com.thingsenz.deviceinfo.ui.components.SocScreen
import com.thingsenz.deviceinfo.ui.components.SoftwareScreen

sealed class TabItem(var icon: Int, var title: String, var layout: @Composable ()->Unit) {
    object Device : TabItem(0,"Device", {DeviceScreen()})
    object Software: TabItem(1,"Software",{SoftwareScreen()})
    object SOC : TabItem(2,"SOC", {SocScreen()})
}
