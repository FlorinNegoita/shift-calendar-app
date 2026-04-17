package com.example.turecalendar.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class ShiftWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Content()
        }
    }

    @Composable
    private fun Content() {
        val now = LocalDateTime.now()

        val shiftToday = getEffectiveShift(now)
        val shiftTomorrow = getShiftForDate(now.toLocalDate().plusDays(1))

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color(0x00FFFFFF))),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TEAM B",
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 9.sp
                )
            )

            Spacer(modifier = GlanceModifier.height(1.dp))

            Text(
                text = shiftToday,
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 15.sp
                )
            )

            Spacer(modifier = GlanceModifier.height(1.dp))

            Text(
                text = "Mâine: $shiftTomorrow",
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 14.sp
                )
            )
        }
    }
}

private val cycleStartDate: LocalDate = LocalDate.of(2025, 11, 3)

private fun getShiftForDate(date: LocalDate): String {
    val days = ChronoUnit.DAYS.between(cycleStartDate, date).toInt()
    val index = ((days % 8) + 8) % 8

    return when (index) {
        0, 1 -> "SC1"
        2, 3 -> "SC2"
        4, 5 -> "SC3"
        else -> "LIB"
    }
}

private fun getEffectiveShift(now: LocalDateTime): String {
    val today = now.toLocalDate()
    val yesterday = today.minusDays(1)
    val hour = now.hour

    return when {
        hour < 7 && getShiftForDate(yesterday) == "SC3" -> "SC3"
        hour >= 23 && getShiftForDate(today) == "SC3" -> "SC3"
        else -> getShiftForDate(today)
    }
}