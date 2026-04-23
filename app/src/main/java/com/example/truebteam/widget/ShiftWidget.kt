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
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class ShiftWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Content(context)
        }
    }

    @Composable
    private fun Content(context: Context) {
        val prefs = context.getSharedPreferences("shift_prefs", Context.MODE_PRIVATE)
        val selectedTeamIndex = prefs.getInt("selected_team_index", 1).coerceIn(0, 3)

        val currentTeamTitle = teamTitles[selectedTeamIndex]
        val currentTeamOffset = teamOffsets[selectedTeamIndex]

        val savedVacationDates = prefs.getStringSet("vacation_dates", emptySet()) ?: emptySet()
        val vacationDays = savedVacationDates.map { LocalDate.parse(it) }.toSet()

        val now = LocalDateTime.now()
        val todayDate = now.toLocalDate()
        val tomorrowDate = todayDate.plusDays(1)

        val realShiftToday = getEffectiveShift(now, currentTeamOffset)
        val realShiftTomorrow = getShiftForDate(tomorrowDate, currentTeamOffset)

        val shiftToday = if (todayDate in vacationDays) "CO" else realShiftToday
        val shiftTomorrow = if (tomorrowDate in vacationDays) "CO" else realShiftTomorrow

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color(0x00FFFFFF))),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentTeamTitle,
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

suspend fun updateShiftWidgets(context: Context) {
    ShiftWidget().updateAll(context)
}

private val teamTitles = listOf(
    "TEAM A",
    "TEAM B",
    "TEAM C",
    "TEAM D"
)

private val teamOffsets = listOf(
    -2,
    0,
    2,
    4
)

private val cycleStartDate: LocalDate = LocalDate.of(2025, 11, 3)

private fun getShiftForDate(date: LocalDate, offset: Int): String {
    val days = ChronoUnit.DAYS.between(cycleStartDate, date).toInt()

    if (days < 0) return "LIB"

    val index = (((days + offset) % 8) + 8) % 8

    return when (index) {
        0, 1 -> "SC1"
        2, 3 -> "SC2"
        4, 5 -> "SC3"
        else -> "LIB"
    }
}

private fun getEffectiveShift(now: LocalDateTime, offset: Int): String {
    val today = now.toLocalDate()
    val time = now.toLocalTime()

    if (time < LocalTime.of(7, 0)) {
        val yesterdayShift = getShiftForDate(today.minusDays(1), offset)
        if (yesterdayShift == "SC3") return "SC3"
    }

    if (time >= LocalTime.of(23, 0)) {
        val tomorrowShift = getShiftForDate(today.plusDays(1), offset)
        if (tomorrowShift == "SC3") return "SC3"
    }

    return getShiftForDate(today, offset)
}