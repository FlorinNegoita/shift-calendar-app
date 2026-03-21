package com.example.turecalendar.ui


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.TextStyle as JavaTextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

private val messagesSC1 = listOf(
    "E devreme, dar măcar avem lumină și viață în oase.",
    "Cafeaua e colegul tău de tură. Ține-l aproape.",
    "Am venit primul. Plec primul. Șah mat, restul lumii.",
    "Când alții încă dorm, noi deja ne întrebăm de ce."
)

private val messagesSC2 = listOf(
    "Tura asta începe frumos și se termină… mai vedem noi.",
    "Nu e nici zi, nici noapte. E între lumi. Aici locuim noi.",
    "Ai ajuns la ora când oamenii mănâncă ciorbă. Tu lucrezi. Respect.",
    "Tura aceasta e ca un sandwich cu pâine prea groasă. Se digeră greu."
)

private val messagesSC3 = listOf(
    "Felicitări. Modul liliac profesional este activ.",
    "Când lumea doarme, tu calculezi secundele. Samurai al fluorescenței.",
    "Relație serioasă cu aparatul de cafea. Oficial.",
    "Noaptea e liniște. Tu doar... exiști. Profund."
)

private val messagesLIB = listOf(
    "Respiră. Trăiește. Nu te mișca. E liber.",
    "Astăzi nu e tură. Astăzi ești pernuță.",
    "Zi liberă = viața are opțiunea demo activată.",
    "Azi iubești patul. Patul te iubește înapoi. Relație serioasă."
)

private val messagesAlmostDone = listOf(
    "Frățioare… încă puțin și zbori afară ca avionul. ✈️",
    "Ține-te, planeta te dă jos imediat.",
    "Mai ai doar câteva minute... rezistăm eroic!",
    "Ești la final. Nu renunța acum. Ar fi păcat."
)

private val messages123 = listOf(
    "Acum ești liber.",
    "Șefii n-au găsit semnalul.",
    "Momentan, fără stres.",
    "Încă n-ai fost convocat.",
    "Ești pe modul chill."
)

private val cycleStartDate = LocalDate.of(2025, 11, 3)

private val SIMULATE_SHIFT_TODAY: String? = null
private val DEBUG_TIME: LocalDateTime? = null

private val vacationDays = setOf(
    LocalDate.of(2026, 4, 10),
    LocalDate.of(2026, 4, 11),
    LocalDate.of(2026, 4, 12),
    LocalDate.of(2026, 4, 13),
    LocalDate.of(2026, 8, 8),
    LocalDate.of(2026, 8, 9),
    LocalDate.of(2026, 8, 10),
    LocalDate.of(2026, 8, 11),
    LocalDate.of(2026, 8, 12),
    LocalDate.of(2026, 8, 13),
    LocalDate.of(2026, 8, 14),
    LocalDate.of(2026, 8, 15),
    LocalDate.of(2026, 8, 16),
    LocalDate.of(2026, 8, 17),
    LocalDate.of(2026, 8, 18),
    LocalDate.of(2026, 8, 19),
    LocalDate.of(2026, 8, 20),
    LocalDate.of(2026, 8, 21),
    LocalDate.of(2026, 8, 22),
    LocalDate.of(2026, 8, 23),
    LocalDate.of(2026, 12, 23),
    LocalDate.of(2026, 12, 24),
    LocalDate.of(2026, 12, 25),
    LocalDate.of(2026, 12, 26),
    LocalDate.of(2026, 12, 27),
    LocalDate.of(2026, 12, 28),
    LocalDate.of(2026, 12, 29),
    LocalDate.of(2026, 12, 30),
    LocalDate.of(2026, 12, 31),
)

private val legalHolidays = setOf(
    LocalDate.of(2026, 1, 1),
    LocalDate.of(2026, 1, 2),
    LocalDate.of(2026, 1, 6),
    LocalDate.of(2026, 1, 7),
    LocalDate.of(2026, 1, 24),
    LocalDate.of(2026, 4, 13),
    LocalDate.of(2026, 4, 10),
    LocalDate.of(2026, 4, 11),
    LocalDate.of(2026, 4, 12),
    LocalDate.of(2026, 5, 1),
    LocalDate.of(2026, 5, 31),
    LocalDate.of(2026, 6, 1),
    LocalDate.of(2026, 8, 15),
    LocalDate.of(2026, 11, 30),
    LocalDate.of(2026, 12, 1),
    LocalDate.of(2026, 12, 25),
    LocalDate.of(2026, 12, 26)
)

private val BgTopLight = Color(0xFFF0F3F7)
private val BgBase1 = Color(0xFF737ca1)
private val BgBase2 = Color(0xFF36454f)
private val SurfaceSoft = Color(0xFFECEFF3)

private val MonthBlue = Color(0xFF3B82F6)

private val Sc1Color = Color(0xFF54B4D3)
private val Sc2Color = Color(0xFFE4A11B)
private val Sc3Color = Color(0xFFDC4C64)
private val LibColor = Color(0xFF15803D)
private val CoColor = Color(0xFF16A34A)

private fun isVacation(date: LocalDate): Boolean = vacationDays.contains(date)
private fun isLegalHoliday(date: LocalDate): Boolean = legalHolidays.contains(date)

fun getShiftForDate(date: LocalDate): String {
    val d = ChronoUnit.DAYS.between(cycleStartDate, date)
    if (d < 0) return "LIB"
    return when ((d % 8).toInt()) {
        0, 1 -> "SC1"
        2, 3 -> "SC2"
        4, 5 -> "SC3"
        else -> "LIB"
    }
}

fun getEffectiveShift(now: LocalDateTime = LocalDateTime.now()): String {
    val today = now.toLocalDate()
    val time = now.toLocalTime()

    if (time < LocalTime.of(7, 0)) {
        val yesterdayShift = getShiftForDate(today.minusDays(1))
        if (yesterdayShift == "SC3") return "SC3"
    }

    if (time >= LocalTime.of(23, 0)) {
        val tomorrowShift = getShiftForDate(today.plusDays(1))
        if (tomorrowShift == "SC3") return "SC3"
    }

    return getShiftForDate(today)
}

fun getShiftProgress(shift: String, now: LocalTime = LocalTime.now()): String {
    fun toMinutes(t: LocalTime) = t.hour * 60 + t.minute

    val startMin = when (shift) {
        "SC1" -> 7 * 60
        "SC2" -> 15 * 60
        "SC3" -> 23 * 60
        else -> return messagesLIB.random()
    }

    val endMin = when (shift) {
        "SC1" -> 15 * 60
        "SC2" -> 23 * 60
        "SC3" -> (7 * 60) + 24 * 60
        else -> startMin
    }

    var nowMin = toMinutes(now)

    if (shift == "SC3" && nowMin < startMin) {
        nowMin += 24 * 60
    }

    if (shift != "SC3" && nowMin < startMin) {
        return messages123.random()
    }

    if (shift == "SC3" && toMinutes(now) >= 7 * 60 && toMinutes(now) < 23 * 60) {
        return messages123.random()
    }

    val remaining = endMin - nowMin

    if (remaining <= 0) {
        return """
GATA! Ești acasă....relax, relax, relax....
""".trimIndent()
    }

    if (remaining <= 30) return messagesAlmostDone.random()

    return when (shift) {
        "SC1" -> messagesSC1.random()
        "SC2" -> messagesSC2.random()
        "SC3" -> messagesSC3.random()
        else -> messagesLIB.random()
    }
}

private fun monthTitle(month: YearMonth): String {
    return month.month.getDisplayName(JavaTextStyle.FULL, Locale.forLanguageTag("ro"))
        .uppercase(Locale.forLanguageTag("ro")) + " ${month.year}"
}

private fun shiftTextColor(displayShift: String): Color {
    return when (displayShift) {
        "SC1" -> Sc1Color
        "SC2" -> Sc2Color
        "SC3" -> Sc3Color
        "LIB" -> LibColor
        "CO" -> CoColor
        else -> Color(0xFF222222)
    }
}

private fun shiftGlowColor(displayShift: String): Color {
    return when (displayShift) {
        "SC1" -> Sc1Color
        "SC2" -> Sc2Color
        "SC3" -> Sc3Color
        "LIB" -> LibColor
        "CO" -> CoColor
        else -> Color.Transparent
    }
}

private data class CalendarCell(
    val date: LocalDate,
    val day: Int,
    val isCurrentMonth: Boolean
)

private fun Modifier.neuButtonShadow(
    cornerRadius: Dp,
    pressed: Boolean = false
): Modifier = this.drawBehind {
    val corner = cornerRadius.toPx()
    val neoShadow = Color(0xFF98A0BC)

    val white1 = if (pressed) 0.28f else 0.55f
    val white2 = if (pressed) 0.10f else 0.22f
    val white3 = if (pressed) 0.03f else 0.08f

    val shadow1 = if (pressed) 0.42f else 0.90f
    val shadow2 = if (pressed) 0.22f else 0.60f
    val shadow3 = if (pressed) 0.12f else 0.34f

    drawRoundRect(
        color = Color.White.copy(alpha = white1),
        topLeft = Offset((-1).dp.toPx(), (-1).dp.toPx()),
        size = size,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner, corner)
    )
    drawRoundRect(
        color = Color.White.copy(alpha = white2),
        topLeft = Offset((-2).dp.toPx(), (-2).dp.toPx()),
        size = size,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner, corner)
    )
    drawRoundRect(
        color = Color.White.copy(alpha = white3),
        topLeft = Offset((-3).dp.toPx(), (-3).dp.toPx()),
        size = size,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner, corner)
    )

    drawRoundRect(
        color = neoShadow.copy(alpha = shadow1),
        topLeft = Offset(3.dp.toPx(), 3.dp.toPx()),
        size = size,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner, corner)
    )
    drawRoundRect(
        color = neoShadow.copy(alpha = shadow2),
        topLeft = Offset(6.dp.toPx(), 6.dp.toPx()),
        size = size,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner, corner)
    )
    drawRoundRect(
        color = neoShadow.copy(alpha = shadow3),
        topLeft = Offset(9.dp.toPx(), 9.dp.toPx()),
        size = size,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner, corner)
    )
}

@Composable
fun CalendarScreen() {
    val lifecycleOwner = LocalLifecycleOwner.current

    var tick by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(10000L)
            tick++
        }
    }

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var direction by remember { mutableStateOf(1) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var todayAnimationTrigger by remember { mutableStateOf(0) }

    fun changeMonth(newMonth: YearMonth) {
        direction = if (newMonth > currentMonth) 1 else -1
        currentMonth = newMonth
    }

    val today = remember(tick) { LocalDate.now() }

    val normHours = remember(currentMonth) {
        (1..currentMonth.lengthOfMonth()).count { day ->
            val date = currentMonth.atDay(day)
            val isWorkingDay = date.dayOfWeek in listOf(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY
            )
            isWorkingDay && !isVacation(date)
        } * 8
    }

    val workedHours = remember(currentMonth) {
        (1..currentMonth.lengthOfMonth()).count { day ->
            val date = currentMonth.atDay(day)
            val isWorkedShift = getShiftForDate(date) in listOf("SC1", "SC2", "SC3")
            isWorkedShift && !isVacation(date)
        } * 8
    }

    val overtime = workedHours - normHours

    val now = DEBUG_TIME ?: LocalDateTime.now()
    val todayShift = SIMULATE_SHIFT_TODAY ?: getEffectiveShift(now)

    var progressText by remember { mutableStateOf("") }

    LaunchedEffect(todayShift, tick) {
        val refreshedNow = DEBUG_TIME ?: LocalDateTime.now()
        progressText = getShiftProgress(todayShift, refreshedNow.toLocalTime())
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val refreshedNow = DEBUG_TIME ?: LocalDateTime.now()
                val effectiveShift = SIMULATE_SHIFT_TODAY ?: getEffectiveShift(refreshedNow)
                progressText = getShiftProgress(effectiveShift, refreshedNow.toLocalTime())
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val overtimeText = when {
        overtime > 0 -> "OVERTIME = $overtime ore"
        overtime < 0 -> "OVERTIME = $overtime ore"
        else -> "Frățioare, luna asta ești pe 0"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFF3E7DC),
                        Color(0xFFEAE3E0),
                        Color(0xFFD7E1F7),
                        Color(0xFFBCCBFF)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1400f, 2400f)
                )
            )
            .drawBehind {
                // glow cald sus-stânga
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFF6EF).copy(alpha = 0.95f),
                            Color(0xFFF8E9DC).copy(alpha = 0.55f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.10f, size.height * 0.08f),
                        radius = size.minDimension * 0.42f
                    ),
                    radius = size.minDimension * 0.42f,
                    center = Offset(size.width * 0.10f, size.height * 0.08f)
                )

                // lumină diagonală moale ca în referință
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.34f),
                            Color.White.copy(alpha = 0.18f),
                            Color.Transparent
                        ),
                        start = Offset(size.width * 0.02f, size.height * 0.02f),
                        end = Offset(size.width * 0.72f, size.height * 0.42f)
                    ),
                    topLeft = Offset.Zero,
                    size = size
                )

                // glow rece jos-dreapta
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF8FA8FF).copy(alpha = 0.34f),
                            Color(0xFFB7C6FF).copy(alpha = 0.16f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.90f, size.height * 0.82f),
                        radius = size.minDimension * 0.46f
                    ),
                    radius = size.minDimension * 0.46f,
                    center = Offset(size.width * 0.90f, size.height * 0.82f)
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 14.dp)
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "TEAM B",
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 3.sp,
                color = Color(0xFFECF0F3),
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.65f),
                        offset = Offset(9f, 9f),
                        blurRadius = 2f
                    )
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = overtimeText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (overtime > 0) Color(0xFF0F8A3B)
                else if (overtime <= 0) Color(0xFFD65A5A)
                else Color(0xFF555555)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NavCircleButton(
                    text = "‹",
                    onClick = { changeMonth(currentMonth.minusMonths(1)) }
                )

                Text(
                    text = monthTitle(currentMonth),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MonthBlue
                )

                NavCircleButton(
                    text = "›",
                    onClick = { changeMonth(currentMonth.plusMonths(1)) }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            SoftPillButton(
                text = "Azi",
                onClick = {
                    changeMonth(YearMonth.now())
                    todayAnimationTrigger++
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("LU", "MA", "MI", "JO", "VI", "SA", "DU").forEach { day ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day,
                            fontSize = 13.sp,
                            color = Color(0xFF4A4A4A),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 14.dp)
            ) {
                AnimatedContent(
                    targetState = currentMonth,
                    transitionSpec = {
                        slideInHorizontally(
                            animationSpec = tween(350, easing = FastOutSlowInEasing),
                            initialOffsetX = { fullWidth -> fullWidth * direction }
                        ) togetherWith slideOutHorizontally(
                            animationSpec = tween(350, easing = FastOutSlowInEasing),
                            targetOffsetX = { fullWidth -> -fullWidth * direction }
                        )
                    },
                    label = "monthSlide"
                ) { month ->
                    val monthCells = remember(month) { buildCalendarCells(month) }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        userScrollEnabled = false,
                        contentPadding = PaddingValues(
                            start = 0.dp,
                            end = 0.dp,
                            top = 10.dp,
                            bottom = 8.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 6.dp)
                    ) {
                        items(monthCells) { cell ->
                            val realShift = getShiftForDate(cell.date)
                            val vacation = isVacation(cell.date)
                            val displayShift = if (vacation) "CO" else realShift
                            val isToday = cell.date == today

                            CalendarDayCell(
                                dayNumber = cell.day,
                                displayShift = displayShift,
                                realShift = realShift,
                                isOtherMonth = !cell.isCurrentMonth,
                                isToday = isToday,
                                isHoliday = isLegalHoliday(cell.date),
                                animationTrigger = todayAnimationTrigger,
                                onClick = { selectedDate = cell.date }
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 50.dp)
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(Sc1Color, "SC1")
            LegendItem(Sc2Color, "SC2")
            LegendItem(Sc3Color, "SC3")
            LegendItem(LibColor, "LIB")
            LegendItem(CoColor, "CO")
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 6.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Azi e ${
                        today.dayOfWeek.getDisplayName(JavaTextStyle.FULL, Locale.forLanguageTag("ro"))
                    }, ${today.dayOfMonth} ${
                        today.month.getDisplayName(JavaTextStyle.FULL, Locale.forLanguageTag("ro"))
                    }",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF293140),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = progressText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }

        if (selectedDate != null) {
            val date = selectedDate!!
            val realShift = getShiftForDate(date)
            val vacation = isVacation(date)
            val displayShift = if (vacation) "CO" else realShift

            AlertDialog(
                onDismissRequest = { selectedDate = null },
                title = {
                    Text(
                        text = "Zi selectată",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        buildString {
                            append("${date.dayOfMonth} ")
                            append(date.month.getDisplayName(JavaTextStyle.FULL, Locale.forLanguageTag("ro")))
                            append(" ${date.year}\n")
                            append("Ești: $displayShift")
                            if (vacation) append(" (tura reală: $realShift)")
                        }
                    )
                },
                confirmButton = {
                    TextButton(onClick = { selectedDate = null }) {
                        Text("OK")
                    }
                },
                containerColor = Color(0xFFF3F6FA)
            )
        }
    }
}
private fun buildCalendarCells(month: YearMonth): List<CalendarCell> {
    val first = month.atDay(1)
    val offset = (first.dayOfWeek.value + 6) % 7

    val prevMonth = month.minusMonths(1)
    val prevMonthLength = prevMonth.lengthOfMonth()

    val currentDays = (1..month.lengthOfMonth()).map {
        CalendarCell(
            date = month.atDay(it),
            day = it,
            isCurrentMonth = true
        )
    }

    val leading = (0 until offset).map { i ->
        val day = prevMonthLength - offset + 1 + i
        CalendarCell(
            date = prevMonth.atDay(day),
            day = day,
            isCurrentMonth = false
        )
    }

    val totalCells = if (leading.size + currentDays.size > 35) 42 else 35
    val trailingCount = totalCells - (leading.size + currentDays.size)

    val nextMonth = month.plusMonths(1)
    val trailing = (1..trailingCount).map { day ->
        CalendarCell(
            date = nextMonth.atDay(day),
            day = day,
            isCurrentMonth = false
        )
    }

    return leading + currentDays + trailing
}

@Composable
private fun NavCircleButton(
    text: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = spring(dampingRatio = 0.60f, stiffness = 700f),
        label = "navButtonScale"
    )

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceSoft,
            contentColor = MonthBlue
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        modifier = Modifier
            .size(42.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .neuButtonShadow(cornerRadius = 21.dp, pressed = isPressed)
    ) {
        Text(
            text = text,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
@Composable
private fun SoftPillButton(
    text: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.60f, stiffness = 700f),
        label = "todayButtonScale"
    )

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceSoft,
            contentColor = MonthBlue
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .neuButtonShadow(cornerRadius = 18.dp, pressed = isPressed)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CalendarDayCell(
    dayNumber: Int,
    displayShift: String,
    realShift: String,
    isOtherMonth: Boolean,
    isToday: Boolean,
    isHoliday: Boolean,
    animationTrigger: Int,
    onClick: () -> Unit
) {
    val glowColor = shiftGlowColor(displayShift)
    val shiftColor = shiftTextColor(displayShift)

    val scaleAnim = remember(isToday) { Animatable(1f) }
    var startTodayAnimation by remember(isToday) { mutableStateOf(false) }

    LaunchedEffect(isToday, animationTrigger) {
        if (isToday) {
            startTodayAnimation = false
            scaleAnim.snapTo(1f)
            delay(900)

            startTodayAnimation = true

            scaleAnim.animateTo(
                targetValue = 1.72f,
                animationSpec = tween(320, easing = FastOutSlowInEasing)
            )

            scaleAnim.animateTo(
                targetValue = 1.38f,
                animationSpec = tween(420, easing = FastOutSlowInEasing)
            )

            scaleAnim.animateTo(
                targetValue = 1.52f,
                animationSpec = tween(520, easing = FastOutSlowInEasing)
            )
        } else {
            startTodayAnimation = false
            scaleAnim.snapTo(1f)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "todayBorderAnim")

    val borderAnim by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderAlpha"
    )

    val neoShadow = Color(0xFF98abbe)
    val baseAlpha = if (isOtherMonth) 0.42f else 1f
    val dateColor = if (isHoliday && !isOtherMonth) Color(0xFFD32F2F) else Color(0xFF202020)

    val borderColor = when {
        isToday && startTodayAnimation -> Color(0xFFE53935).copy(alpha = borderAnim)
        isToday -> Color(0xFFE53935).copy(alpha = 0.65f)
        else -> Color.White.copy(alpha = 0.62f)
    }

    val glowPrimaryAlpha = when {
        isToday && displayShift == "CO" -> 0.52f
        displayShift == "CO" -> 0.64f
        isToday -> 0.40f
        else -> 0.20f
    }

    val glowSecondaryAlpha = when {
        isToday && displayShift == "CO" -> 0.38f
        displayShift == "CO" -> 0.34f
        else -> 0.09f
    }

    val glowRadius = when {
        displayShift == "CO" -> sizeAwareRadiusMultiplier(isToday = isToday, isCo = true)
        else -> sizeAwareRadiusMultiplier(isToday = isToday, isCo = false)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(baseAlpha)
            .zIndex(if (isToday) 10f else 0f)
            .graphicsLayer {
                scaleX = scaleAnim.value
                scaleY = scaleAnim.value
            }
            .drawBehind {
                val corner = 8.dp.toPx()

                drawRoundRect(
                    color = Color.White.copy(alpha = 0.95f),
                    topLeft = Offset((-2).dp.toPx(), (-2).dp.toPx()),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner, corner)
                )
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.45f),
                    topLeft = Offset((-4).dp.toPx(), (-4).dp.toPx()),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner, corner)
                )
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.18f),
                    topLeft = Offset((-7).dp.toPx(), (-7).dp.toPx()),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner, corner)
                )

                drawRoundRect(
                    color = neoShadow.copy(alpha = 0.90f),
                    topLeft = Offset(3.dp.toPx(), 3.dp.toPx()),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner, corner)
                )
                drawRoundRect(
                    color = neoShadow.copy(alpha = 0.60f),
                    topLeft = Offset(6.dp.toPx(), 6.dp.toPx()),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner, corner)
                )
                drawRoundRect(
                    color = neoShadow.copy(alpha = if (isToday) 0.36f else 0.34f),
                    topLeft = Offset(9.dp.toPx(), 9.dp.toPx()),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner, corner)
                )
            }
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceSoft)
            .drawWithContent {
                drawContent()

                if (!isOtherMonth) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                glowColor.copy(alpha = glowPrimaryAlpha),
                                glowColor.copy(alpha = glowSecondaryAlpha),
                                Color.Transparent
                            ),
                            center = center,
                            radius = size.minDimension * glowRadius
                        ),
                        radius = size.minDimension * glowRadius,
                        center = center
                    )

                    if (displayShift == "CO") {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = if (isToday) 0.26f else 0.18f),
                                    Color.White.copy(alpha = if (isToday) 0.10f else 0.07f),
                                    Color.Transparent
                                ),
                                center = Offset(size.width * 0.34f, size.height * 0.30f),
                                radius = size.minDimension * 0.62f
                            ),
                            radius = size.minDimension * 0.62f,
                            center = Offset(size.width * 0.34f, size.height * 0.30f)
                        )
                    }
                }
            }
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .height(54.dp)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayNumber.toString(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = dateColor
            )

            if (!isOtherMonth) {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (displayShift == "CO") "CO" else realShift,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = shiftColor,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        shadow = Shadow(
                            color = glowColor.copy(alpha = if (displayShift == "CO") 0.16f else 0.10f),
                            offset = Offset.Zero,
                            blurRadius = if (displayShift == "CO") 2.2f else 1.5f
                        )
                    )
                )
            }
        }
    }
}

private fun sizeAwareRadiusMultiplier(isToday: Boolean, isCo: Boolean): Float {
    return when {
        isCo && isToday -> 1.10f
        isCo -> 1.06f
        isToday -> 0.98f
        else -> 0.98f
    }
}
@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF555555)
        )
    }
}