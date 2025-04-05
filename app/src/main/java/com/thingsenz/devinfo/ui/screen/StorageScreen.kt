package com.thingsenz.devinfo.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thingsenz.devinfo.utils.Util
import com.thingsenz.devinfo.utils.formatSize

@Composable
fun StorageScreen() {
    val list = Util.getStorageDetails(LocalContext.current)
    Column(modifier = Modifier.padding(top = 20.dp, start = 20.dp)) {
        if (list.containsKey("AIM") && list.containsKey("TIM")) {
            Text(text = "Internal Memory",)
            Row {
                CircularProgressBar(
                    progress = getPercentageUsed(
                        list["AIM"] ?: 0,
                        list["TIM"] ?: 0
                    ),
                    foregroundIndicatorColor = MaterialTheme.colorScheme.inversePrimary
                )
                Column(
                    modifier = Modifier
                        .height(140.dp)
                        .padding(start = 40.dp), verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Free Memory:\n ${list["AIM"]?.formatSize()}", fontSize = 18.sp, color = getTextColor())
                    Text(text = "Total Memory:\n ${list["TIM"]?.formatSize()}", fontSize = 18.sp, color = getTextColor())
                }
            }
        }
        
        Spacer(modifier = Modifier.height(30.dp))
        if (list.containsKey("ARAM") && list.containsKey("TRAM")) {
            Text(text = "RAM")
            Row {
                CircularProgressBar(
                    progress = getPercentageUsed(
                        list["ARAM"] ?: 0,
                        list["TRAM"] ?: 0
                    ),
                    foregroundIndicatorColor = MaterialTheme.colorScheme.inversePrimary
                )
                Column(
                    modifier = Modifier
                        .height(140.dp)
                        .padding(start = 40.dp), verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Free Memory:\n ${list["ARAM"]?.formatSize()}", fontSize = 18.sp, color = getTextColor())
                    Text(text = "Total Memory:\n ${list["TRAM"]?.formatSize()}", fontSize = 18.sp, color = getTextColor())
                }
            }
        }
    }
}

private fun getPercentageUsed(a: Long, b: Long): Float {
    val rem = b-a
    return if (b!=0L) {
        (rem.toFloat() / b) * 100
    } else 0f
}

@Composable
fun CircularProgressBar(progress: Float,size: Dp = 150.dp,
                        foregroundIndicatorColor: Color = MaterialTheme.colorScheme.onSurface,
                        shadowColor: Color = Color.LightGray, indicatorThickness: Dp = 10.dp,
                        animDuration: Int = 1000, dataTextStyle: TextStyle = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = MaterialTheme.typography.headlineMedium.fontSize),
                        remainingTextStyle: TextStyle = TextStyle(
                            fontFamily = FontFamily.Cursive,
                            fontSize = 16.sp
                        )
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
            drawCircle(
                color = Color.White,
                radius = this.size.height/2,
                center = Offset(x = this.size.width/2,y=this.size.height/2)
            )
            drawCircle(
                color = Color.White,
                radius = (size/2-indicatorThickness).toPx(),
                center = Offset(x=this.size.width/2, y = this.size.height/2)
            )
            val sweepAngle = (storageAnim.value)*360/100
            drawArc(
                color = foregroundIndicatorColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = indicatorThickness.toPx(), cap = StrokeCap.Round),
                size = Size(width = (size-indicatorThickness).toPx(), height = (size-indicatorThickness).toPx()),
                topLeft = Offset(x = (indicatorThickness/2).toPx(),y=(indicatorThickness/2).toPx())
            )

        }

        DisplayText(
            animateNumber = storageAnim,
            dataTextStyle = dataTextStyle,
            remainingTextStyle = remainingTextStyle
        )
    }

    Spacer(modifier = Modifier.height(32.dp))

}

@Composable
private fun DisplayText(
    animateNumber: State<Float>,
    dataTextStyle: TextStyle,
    remainingTextStyle: TextStyle
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = (animateNumber.value).toInt().toString()+" %", style = dataTextStyle, color = MaterialTheme.colorScheme.inversePrimary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = "Used",  color = MaterialTheme.colorScheme.inversePrimary)
    }
}

@Composable
private fun getTextColor() = if (isSystemInDarkTheme()) Color.White else Color.Black