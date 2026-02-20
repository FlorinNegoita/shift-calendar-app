package com.example.turecalendar.ui
import java.time.LocalDateTime
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

private val messagesSC1 = listOf(
    "E devreme, dar măcar avem lumină și viață în oase.",
    "Cafeaua e colegul tău de tură. Ține-l aproape.",
    "Am venit primul. Plec primul. Șah mat, restul lumii.",
    "Când alții încă dorm, noi deja ne întrebăm *de ce*."
)

private val messagesSC2 = listOf(
    "Tura asta începe frumos și se termină… mai vedem noi.",
    "Nu e nici zi, nici noapte. E *între lumi*. Aici locuim noi.",
    "Ai ajuns la ora când oamenii mănâncă ciorbă. Tu lucrezi. Respect.",
    "Tura aceasta e ca un sandwich cu pâine prea groasă. Se digeră greu."
)

private val messagesSC3 = listOf(
    "Felicitări. Modul *liliac profesional* este activ.",
    "Când lumea doarme, tu calculezi secundele. Samurai al fluorescentei.",
    "Relație serioasă cu aparatul de cafea. Oficial.",
    "Noaptea e liniște. Tu doar... exiști. Profund."
)

private val messagesLIB = listOf(
    "Respiră. Trăiește. Nu te mișca. E liber.",
    "Astăzi nu e tură. Astăzi ești pernuță.",
    "Zi liberă = viața are opțiunea *demo* activată.",
    "Azi iubești patul. Patul te iubește înapoi. Relație serioasă."
)

private val messagesAlmostDone = listOf(
    "Frățioare… încă puțin și zbori afară ca avionul. ✈️",
    "Ține-te, planeta te dă jos imediat.",
    "Mai ai doar câteva minute... rezistăm eroic!",
    "Ești la final. Nu renunța acum. Ar fi păcat."
)

private val messages123 = listOf(
    "Acum ești liber. Armata schimburilor încă nu te-a convocat.",
    "Inca esti acasa. Munca nu te-a reperat...inca.",
    "Acum ești liber. Planeta e încă în modul demo.",
    "Așa arată viața fără șefi. Prețuiește-o.",
    "Muncă? Da… dar nu încă. Acum ești în căldare low-power mode."
)

// Vrei ca luna Noiembrie să înceapă cu SC1
private val cycleStartDate = LocalDate.of(2025, 11, 3)

// Dacă vrei să “simulezi că acum ești în SC2”, pune aici "SC2". Pentru normal, pune null.
private val SIMULATE_SHIFT_TODAY: String? = null

// ======================
// ✅ CONCEDII (CO) – bagă manual aici datele
// ======================
private val vacationDays = setOf(
    // aici scrie zilele de concediu
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
)

private val legalHolidays = setOf(
    LocalDate.of(2026, 1, 1),   // Anul Nou
    LocalDate.of(2026, 1, 2),   // A doua zi de Anul Nou
    LocalDate.of(2026, 1, 6),   // Boboteaza
    LocalDate.of(2026, 1, 7),   // Sf. Ion
    LocalDate.of(2026, 1, 24),  // Unirea Principatelor
    LocalDate.of(2026, 4, 13),   // Paste
    LocalDate.of(2026, 4, 10),  // Vinerea MNare
    LocalDate.of(2026, 4, 11),  // Paste
    LocalDate.of(2026, 4, 12),   // Paste
    LocalDate.of(2026, 5, 1),   // Ziua Muncii
    LocalDate.of(2026, 5, 31),  // Rsusaliile
    LocalDate.of(2026, 6, 1),   // Ziua Copilului, Rusalii
    LocalDate.of(2026, 8, 15),  // Adormirea Maicii Domnului
    LocalDate.of(2026, 11, 30), // Sf. Andrei
    LocalDate.of(2026, 12, 1),  // Ziua Națională
    LocalDate.of(2026, 12, 25), // Craciunul
    LocalDate.of(2026, 12, 26)  //A doua zi de Craciun
)

private fun isVacation(date: LocalDate): Boolean = vacationDays.contains(date)

// ✅ NOU: sărbători legale
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

    // între 00:00–07:00 → verificăm dacă ieri a fost SC3
    if (time < LocalTime.of(7, 0)) {
        val yesterdayShift = getShiftForDate(today.minusDays(1))
        if (yesterdayShift == "SC3") return "SC3"
    }

    val todayShift = getShiftForDate(today)

    // după 23:00 → începe SC3
    if (todayShift == "SC3" && time >= LocalTime.of(23, 0)) {
        return "SC3"
    }

    return todayShift
}

fun getShiftProgress(shift: String, now: LocalTime = LocalTime.now()): String {

    val start = when (shift) {
        "SC1" -> LocalTime.of(7, 0)
        "SC2" -> LocalTime.of(15, 0)
        "SC3" -> LocalTime.of(23, 0)
        else -> return messagesLIB.random()
    }

    val end = when (shift) {
        "SC1" -> LocalTime.of(15, 0)
        "SC2" -> LocalTime.of(23, 0)
        "SC3" -> LocalTime.of(7, 0).plusHours(24) // ✅ FIX
        else -> start
    }

    // ✅ Dacă e înainte de ora de start → ești acasă
    if (shift == "SC3" && now.isBefore(start) && now >= LocalTime.of(7,0)) {
        return messages123.random()
    }

    if (shift != "SC3" && now.isBefore(start)) {
        return messages123.random()
    }

    // Ajustare pentru ture peste miezul nopții
    val nowAdjusted =
        if (shift == "SC3" && now.isBefore(start)) now.plusHours(24)
        else now

    val remaining = Duration.between(nowAdjusted, end).toMinutes()

    if (remaining <= 0) {
        return """
GATA! Ești acasă, boss! 🎉
...relax, relax, relax... 🍺
""".trimIndent()
    }

    // Dacă mai sunt sub 30 minute → mesaj de final random
    if (remaining <= 30) return messagesAlmostDone.random()

    // Altfel → mesaj random în funcție de tură
    return when (shift) {
        "SC1" -> messagesSC1.random()
        "SC2" -> messagesSC2.random()
        "SC3" -> messagesSC3.random()
        else -> messagesLIB.random()
    }
}

@Composable
fun CalendarScreen() {

    val lifecycleOwner = LocalLifecycleOwner.current

    // ——— Auto-refresh: recompoziție la fiecare 10s ———
    var tick by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(10000L)
            tick++
        }
    }

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var previousMonth by remember { mutableStateOf(currentMonth) }
    var direction by remember { mutableStateOf(1) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    fun changeMonth(newMonth: YearMonth) {
        direction = if (newMonth > currentMonth) 1 else -1
        previousMonth = currentMonth
        currentMonth = newMonth
    }

    val today = remember(tick) { LocalDate.now() }

    val normHours = remember(currentMonth) {
        (1..currentMonth.lengthOfMonth()).count { day ->
            val date = currentMonth.atDay(day)

            val isWorkingDay =
                date.dayOfWeek in listOf(
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

            val isWorkedShift =
                getShiftForDate(date) in listOf("SC1", "SC2", "SC3")

            isWorkedShift && !isVacation(date)
        } * 8
    }

    val overtime = workedHours - normHours

    // 🔥 FIX: luăm timpul curent la fiecare recompoziție (tick forțează recompoziția)
    val now = LocalDateTime.now()

    // 🔥 FIX: scoatem remember(tick) și folosim getEffectiveShift(now)
    val todayShift = SIMULATE_SHIFT_TODAY ?: getEffectiveShift(now)

    val themeBackground = when (todayShift) {
        "SC1" -> Color(0xFFE8F6FF)
        "SC2" -> Color(0xFFFFF3D6)
        "SC3" -> Color(0xFFF7D6E8)
        "LIB" -> Color(0xFFE3FFE8)
        else -> Color.White
    }

    // 🔥 FIX: mesajul ține cont și de ora reală + se actualizează la tick
    var progressText by remember { mutableStateOf("") }

    LaunchedEffect(todayShift, tick) {
        val refreshedNow = LocalDateTime.now()
        progressText = getShiftProgress(
            todayShift,
            refreshedNow.toLocalTime()
        )
    }

    // 🎯 Recalculăm mesajul când se schimbă tura "de azi"
    LaunchedEffect(todayShift) {
        val refreshedNow = LocalDateTime.now()
        progressText = getShiftProgress(todayShift, refreshedNow.toLocalTime())
    }

    // 🔄 Refresh mesaj când aplicația revine în prim-plan
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val refreshedNow = LocalDateTime.now()
                val effectiveShift = SIMULATE_SHIFT_TODAY ?: getEffectiveShift(refreshedNow)
                progressText = getShiftProgress(effectiveShift, refreshedNow.toLocalTime())
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeBackground)
    ) {

        // HEADER TEAM B
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .height(40.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF8A2BE2), Color(0xFF00C9FF))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("TEAM B", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }

        // ——— OVERTIME ———
        val overtimeText = when {
            overtime > 0 -> "OVERTIME: = $overtime ore  💪"
            overtime < 0 -> "OVERTIME: = $overtime ore  👎"
            else -> "Frățioare, luna asta ești pe 0  😡 "
        }

        Text(
            text = overtimeText,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 90.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            color = when {
                overtime > 0 -> Color(0xFF000000)
                overtime <= 0 -> Color(0xFFFF6A6A)
                else -> Color.Black
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF2ECC71),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 16.dp)
        ) {

            // ——— BARĂ NAV LUNĂ ———
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { changeMonth(currentMonth.minusMonths(1)) }) { Text("<") }
                Text(
                    currentMonth.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("ro")).uppercase() +
                            " " + currentMonth.year,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Button(onClick = { changeMonth(currentMonth.plusMonths(1)) }) { Text(">") }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { changeMonth(YearMonth.now()) }) { Text("Today") }
            Spacer(modifier = Modifier.height(24.dp))

            // ——— HEADER ZILE ———
            val headerDays = listOf("Lu", "Ma", "Mi", "Jo", "Vi", "Sa", "Du")
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                userScrollEnabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                items(headerDays.size) { i ->
                    DayBox(
                        text = headerDays[i],
                        background = Color.Transparent,
                        borderColor = Color.Transparent,
                        borderWidth = 0.dp,
                        textAlignStart = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ——— CALENDAR SLIDE ———
            AnimatedContent(
                targetState = currentMonth,
                transitionSpec = {
                    slideInHorizontally(
                        animationSpec = tween(350),
                        initialOffsetX = { fullWidth -> fullWidth * direction }
                    ) togetherWith slideOutHorizontally(
                        animationSpec = tween(350),
                        targetOffsetX = { fullWidth -> -fullWidth * direction }
                    )
                }
            ) { month ->

                val first = month.atDay(1)
                val offset = (first.dayOfWeek.value + 6) % 7
                val prevDays = month.minusMonths(1).lengthOfMonth()

                val leading = List(offset) { prevDays - offset + 1 + it }
                val current = (1..month.lengthOfMonth()).toList()
                val totalCells =
                    if (leading.size + current.size > 35) 42 else 35
                val trailing = (1..(totalCells - (leading.size + current.size))).toList()

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // ✅ FIX: leading cells normale (nu redefinim DayBox aici)
                    items(leading.size) { i ->
                        DayBox(
                            text = "${leading[i]}",
                            background = Color.LightGray.copy(alpha = 0.12f),
                            borderColor = Color.Black.copy(alpha = 0.2f),
                            textAlignStart = true
                        )
                    }

                    items(current.size) { i ->
                        val day = current[i]
                        val date = month.atDay(day)

                        val realShift = getShiftForDate(date)
                        val vacation = isVacation(date)

                        // ✅ Afișare: CO peste ture (dar tura reală rămâne realShift)
                        val displayShift = if (vacation) "CO" else realShift

                        val bg = when {
                            vacation -> Color(0xFFB39DDB).copy(alpha = 0.55f)
                            realShift == "SC1" -> Color(0xFFADD8E6).copy(alpha = 0.90f)
                            realShift == "SC2" -> Color(0xFFFFD580).copy(alpha = 0.45f)
                            realShift == "SC3" -> Color(0xFFFFA8A8).copy(alpha = 0.45f)
                            realShift == "LIB" -> Color(0xFF90EE90).copy(alpha = 0.45f)
                            else -> Color.White
                        }


                        val isToday = (date == today)
                        val border = if (isToday) Color.Red else Color.Black
                        val borderW = if (isToday) 5.dp else 1.dp

                        // ✅ DOAR sărbătorile legale = text roșu
                        val textColor = if (isLegalHoliday(date)) Color.Red else Color.Black

                        DayBox(
                            text = "$day\n$displayShift",
                            background = bg,
                            borderColor = border,
                            borderWidth = borderW,
                            textAlignStart = true,
                            textColor = textColor
                        ) { selectedDate = date }
                    }

                    items(trailing.size) { i ->
                        DayBox(
                            text = "${trailing[i]}",
                            background = Color.LightGray.copy(alpha = 0.12f),
                            borderColor = Color.Black.copy(alpha = 0.2f),
                            textAlignStart = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(Color(0xFFADD8E6), "SC1")
                LegendItem(Color(0xFFFFD580), "SC2")
                LegendItem(Color(0xFFFFA8A8), "SC3")
                LegendItem(Color(0xFF90EE90), "LIBER")
                LegendItem(Color(0xFFB39DDB), "CO")
            }
        }

        // ——— FOOTER ———
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .border(2.dp, Color.Black)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF8FFF8E), Color(0xFF6AB8FF))
                    )
                )
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Azi e ${today.dayOfMonth} ${
                        today.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("ro"))
                    } ${today.year}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = progressText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    textAlign = TextAlign.Center
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
                title = { Text("Zi selectată") },
                text = {
                    Text(
                        buildString {
                            append("Azi e ${date.dayOfMonth} ")
                            append(date.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("ro")))
                            append(" ${date.year}\n")
                            append("Ești: $displayShift")
                            if (vacation) append(" (tura: $realShift)")
                        }
                    )
                },
                confirmButton = {
                    Button(onClick = { selectedDate = null }) { Text("OK") }
                }
            )
        }
    }
}

@Composable
fun DayBox(
    text: String,
    background: Color,
    borderColor: Color,
    borderWidth: Dp = 1.dp,
    textAlignStart: Boolean = false,
    textColor: Color = Color.Black, // ✅ NOU (default negru)
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .width(32.dp)
            .height(52.dp)
            .background(background)
            .border(borderWidth, borderColor)
            .clickable { onClick() }
            .padding(start = 6.dp, top = 4.dp, end = 4.dp, bottom = 4.dp),
        contentAlignment = if (textAlignStart) Alignment.TopStart else Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = textColor, // ✅ NOU
            textAlign = if (textAlignStart) TextAlign.Start else TextAlign.Center
        )
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(20.dp).background(color).border(1.dp, Color.Black))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label)
    }
}
