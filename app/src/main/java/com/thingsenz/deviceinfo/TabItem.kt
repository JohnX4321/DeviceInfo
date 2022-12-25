package com.thingsenz.deviceinfo

import androidx.compose.runtime.Composable

sealed class TabItem(var icon: Int, var title: String, var layout: @Composable ()->Unit) {
    object Device : TabItem(R.drawable.phone,"Device", {DeviceScreen()})
    object Software: TabItem(R.drawable.android,"Software",{SoftwareScreen()})
    object SOC : TabItem(R.drawable.soc,"SOC", {SocScreen()})
}
