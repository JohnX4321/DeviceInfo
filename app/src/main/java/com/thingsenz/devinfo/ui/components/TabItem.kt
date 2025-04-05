package com.thingsenz.devinfo.ui.components

import androidx.compose.runtime.Composable
import com.thingsenz.devinfo.ui.screen.DeviceScreen
import com.thingsenz.devinfo.ui.screen.DisplayScreen
import com.thingsenz.devinfo.ui.screen.SocScreen
import com.thingsenz.devinfo.ui.screen.SoftwareScreen
import com.thingsenz.devinfo.ui.screen.StorageScreen

sealed class TabItem(var icon: Int, var title: String, var layout: @Composable ()->Unit) {
    data object Device : TabItem(0,"Device", { DeviceScreen() })
    data object Software: TabItem(1,"Software",{ SoftwareScreen() })
    data object SOC : TabItem(2,"SOC", { SocScreen() })
    data object Storage: TabItem(3, "Storage", { StorageScreen() })
    data object Display: TabItem(4,"Display",{ DisplayScreen() })
}
