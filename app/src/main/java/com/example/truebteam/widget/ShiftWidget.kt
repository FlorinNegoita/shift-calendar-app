package com.example.turecalendar.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
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
        val shift = getEffectiveShift(now)

        val shiftColor = when (shift) {
            "SC1" -> Color(0xFF60A5FA)
            "SC2" -> Color(0xFFF59E0B)
            "SC3" -> Color(0xFFEF4444)
            "LIB" -> Color(0xFF22C55E)
            "CO"  -> Color(0xFFA78BFA)
            else  -> Color(0xFF9CA3AF)
        }

        GlanceTheme {
            // 🔥 BOX MIC — NU mai fillMaxSize
            Box(
                modifier = GlanceModifier
                    .wrapContentSize()
                    .padding(4.dp)
            ) {
                Column(
                    modifier = GlanceModifier
                        // 🔥 cât textul, nu full
                        .wrapContentSize()
                        // 🔥 fundal mic + transparent
                        .background(ColorProvider(Color(0x33FFFFFF)))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "TEAM B",
                        style = TextStyle(
                            color = ColorProvider(Color(0xFF374151)),
                            fontSize = 10.sp
                        )
                    )

                    Spacer(modifier = GlanceModifier.height(2.dp))

                    Text(
                        text = shift,
                        style = TextStyle(
                            color = ColorProvider(shiftColor),
                            fontSize = 16.sp
                        )
                    )

                    Spacer(modifier = GlanceModifier.height(2.dp))

                    Text(
                        text = getShiftInterval(shift),
                        style = TextStyle(
                            color = ColorProvider(Color(0xFF6B7280)),
                            fontSize = 9.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Rotație: SC1 → SC2 → SC3 → LIB → LIB → (repeat)
 * TODO: Ajustează CYCLE_START_DATE la data reală de referință pentru Team B
 *       și CYCLE_START_SHIFT la schimbul corespunzător acelei date.
 */
fun getEffectiveShift(now: LocalDateTime): String {
    val CYCLE_START_DATE = LocalDate.of(2024, 1, 1) // <-- modifică data de referință
    val cycle = listOf("SC1", "SC1", "SC2", "SC2", "SC3", "SC3", "LIB", "LIB")

    val today = now.toLocalDate()
    val daysSinceStart = ChronoUnit.DAYS.between(CYCLE_START_DATE, today).toInt()
    val index = ((daysSinceStart % cycle.size) + cycle.size) % cycle.size
    return cycle[index]
}

fun getShiftInterval(shift: String): String {
    return when (shift) {
        "SC1" -> "07:00 - 15:00"
        "SC2" -> "15:00 - 23:00"
        "SC3" -> "23:00 - 07:00"
        "LIB" -> "Liber"
        "CO"  -> "Concediu"
        else  -> "-"
    }
}