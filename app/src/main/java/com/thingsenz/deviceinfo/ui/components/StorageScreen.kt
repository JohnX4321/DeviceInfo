package com.thingsenz.deviceinfo.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StorageScreen() {

}

@Composable
fun CircularProgressBar(progress: Float,size: Dp = 260.dp,
                        foregroundIndicatorColor: Color = MaterialTheme.colors.onSurface,
                        shadowColor: Color = Color.LightGray, indicatorThickness: Dp = 18.dp,
                        animDuration: Int = 1000, dataTextStyle: TextStyle = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = MaterialTheme.typography.h3.fontSize),
) {

    var storageRem by remember {
        mutableStateOf(-1f)
    }

    var storageAnim = animateFloatAsState(targetValue = storageRem, animationSpec = tween(durationMillis = animDuration))

    LaunchedEffect(Unit) {
        storageRem = progress
    }
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size = size) ) {
            drawCircle(brush = Brush.radialGradient(colors = listOf(shadowColor,Color.White), center = Offset(x=this.size.width/2,y=this.size.height/2), radius = this.size.height/2),
                radius = this.size.height/2, center = Offset(x = this.size.width/2, y = this.size.height/2)
            )

        }
    }

}