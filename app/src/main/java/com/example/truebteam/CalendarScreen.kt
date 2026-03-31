package com.example.turecalendar.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.geometry.CornerRadius
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
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as JavaTextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

// ------------------------------------------------------------
// MESAJELE MOTIVAȚIONALE
// aici e mica poezie industrială a aplicației:
// un pic de încurajare, un pic de caterincă, cât să nu fie totul beton armat
// ------------------------------------------------------------

private val messagesSC1 = listOf(
    "Cafea și atac.",
    "Dimineață de campion.",
    "SC1 în forță.",
    "Devreme, dar bine."
)

private val messagesSC2 = listOf(
    "SC2, hai tare.",
    "După-amiază lungă.",
    "Între lumi, ca de obicei.",
    "Tura doi, rezistăm."
)

private val messagesSC3 = listOf(
    "Mod liliac: ON.",
    "Noapte grea, suflet tare.",
    "SC3 în acțiune.",
    "Cafeaua conduce."
)

private val messagesLIB = listOf(
    "Azi e liber.",
    "Mod relax activ.",
    "Patul te cheamă.",
    "Zero stres azi."
)

private val messagesAlmostDone = listOf(
    "Încă puțin.",
    "Aproape ai scăpat.",
    "Finalul e aproape.",
    "Mai ai un pic."
)

private val messages123 = listOf(
    "Ești liber acum.",
    "Momentan, chill.",
    "Încă e liniște.",
    "Fără stres acum."
)

// ------------------------------------------------------------
// CONFIG DE CICLU
// de aici începe toată șmecheria calendarului etern.
// dacă baza e bună, tot restul dansează frumos.
// ------------------------------------------------------------

private val cycleStartDate = LocalDate.of(2025, 11, 3)

// pentru teste rapide; când e null, aplicația merge pe viața reală
private val SIMULATE_SHIFT_TODAY: String? = null
private val DEBUG_TIME: LocalDateTime? = null

// ------------------------------------------------------------
// CONCEDII
// când omul e în CO, nu ne mai interesează tura reală.
// pace, liniște și eventual o cafea băută fără alarmă.
// ------------------------------------------------------------

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

// ------------------------------------------------------------
// SĂRBĂTORI LEGALE
// zilele în care și calendarul știe că e rost de altă culoare.
// ------------------------------------------------------------

private val legalHolidays = setOf(
    LocalDate.of(2026, 1, 1),
    LocalDate.of(2026, 1, 2),
    LocalDate.of(2026, 1, 6),
    LocalDate.of(2026, 1, 7),
    LocalDate.of(2026, 1, 24),
    LocalDate.of(2026, 4, 10),
    LocalDate.of(2026, 4, 11),
    LocalDate.of(2026, 4, 12),
    LocalDate.of(2026, 4, 13),
    LocalDate.of(2026, 5, 1),
    LocalDate.of(2026, 5, 31),
    LocalDate.of(2026, 6, 1),
    LocalDate.of(2026, 8, 15),
    LocalDate.of(2026, 11, 30),
    LocalDate.of(2026, 12, 1),
    LocalDate.of(2026, 12, 25),
    LocalDate.of(2026, 12, 26)
)

// ------------------------------------------------------------
// PALETA APLICAȚIEI
// fiecare schimb cu personalitatea lui.
// SC3 e roșu, că noaptea nu vine să glumească.
// ------------------------------------------------------------

private val SurfaceSoft = Color(0xFFEEF1F5)
private val MonthBlue = Color(0xFF3B82F6)

private val Sc1Color = Color(0xFF60A5FA)
private val Sc2Color = Color(0xFFF59E0B)
private val Sc3Color = Color(0xFFEF4444)
private val LibColor = Color(0xFF22C55E)
private val CoColor = Color(0xFFA78BFA)

// verificări simple, dar foarte folositoare;
// genul de funcții mici care îți salvează nervii mai târziu
private fun isVacation(date: LocalDate): Boolean = date in vacationDays
private fun isLegalHoliday(date: LocalDate): Boolean = date in legalHolidays

// culoarea de accent pentru popup-ul zilei selectate
// adică să știe și dialogul cu cine ține
private fun popupAccentColor(displayShift: String): Color {
    return when (displayShift) {
        "SC1" -> Sc1Color
        "SC2" -> Sc2Color
        "SC3" -> Sc3Color
        "LIB" -> LibColor
        "CO" -> CoColor
        else -> MonthBlue
    }
}

// ------------------------------------------------------------
// LOGICA DE TURE
// aici stă motorul principal:
// 2 zile SC1, 2 zile SC2, 2 zile SC3, 2 zile LIB.
// simplu, elegant, fără telenovelă.
// ------------------------------------------------------------

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

// aici nu ne uităm doar la zi, ci și la oră,
// pentru că SC3 e o fiară specială: începe într-o zi și se termină în alta
fun getEffectiveShift(now: LocalDateTime = LocalDateTime.now()): String {
    val today = now.toLocalDate()
    val time = now.toLocalTime()

    // înainte de 07:00, dacă ieri a fost SC3, încă ești în logica ei
    if (time < LocalTime.of(7, 0)) {
        val yesterdayShift = getShiftForDate(today.minusDays(1))
        if (yesterdayShift == "SC3") return "SC3"
    }

    // după 23:00, dacă mâine e SC3, intrăm deja în modul liliac
    if (time >= LocalTime.of(23, 0)) {
        val tomorrowShift = getShiftForDate(today.plusDays(1))
        if (tomorrowShift == "SC3") return "SC3"
    }

    return getShiftForDate(today)
}

// ------------------------------------------------------------
// TEXTUL DE PROGRES AL TUREI
// aici aplicația mai și vorbește cu omul,
// nu doar afișează cifre ca un robot fără suflet.
// ------------------------------------------------------------

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

    // pentru SC3, dacă e după miezul nopții, adăugăm 24h în calcule
    // altfel matematica o ia pe câmpii
    if (shift == "SC3" && nowMin < startMin) {
        nowMin += 24 * 60
    }

    // dacă nu e SC3 și încă n-a început tura, nu are rost să panicăm omul
    if (shift != "SC3" && nowMin < startMin) {
        return messages123.random()
    }

    // intervalul mort dintre 07:00 și 23:00 pentru SC3
    // adică nu e tura activă, deci putem respira
    if (shift == "SC3" && toMinutes(now) in (7 * 60) until (23 * 60)) {
        return messages123.random()
    }

    val remaining = endMin - nowMin

    if (remaining <= 0) {
        return "GATA! Ești acasă....relax, relax, relax...."
    }

    if (remaining <= 30) return messagesAlmostDone.random()

    return when (shift) {
        "SC1" -> messagesSC1.random()
        "SC2" -> messagesSC2.random()
        "SC3" -> messagesSC3.random()
        else -> messagesLIB.random()
    }
}

// titlul lunii, cu majuscule frumoase, să arate a calendar cu prestanță
private fun monthTitle(month: YearMonth): String {
    return month.month
        .getDisplayName(JavaTextStyle.FULL, Locale.forLanguageTag("ro"))
        .uppercase(Locale.forLanguageTag("ro")) + " ${month.year}"
}

// culoarea textului de schimb din celulă
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

// glow-ul subtil al schimbului;
// un fel de “uite-mă, dar cu bun-simț”
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

// text mai natural pentru popup;
// sună mai omenesc decât să-i spui omului doar "SC2"
private fun shiftDisplayName(shift: String): String {
    return when (shift) {
        "SC1" -> "schimbul 1"
        "SC2" -> "schimbul 2"
        "SC3" -> "schimbul 3"
        "LIB" -> "liber"
        "CO" -> "în concediu"
        else -> shift
    }
}

// model simplu pentru o celulă din calendar
// fără figuri, doar ce ne trebuie
private data class CalendarCell(
    val date: LocalDate,
    val day: Int,
    val isCurrentMonth: Boolean
)

// ------------------------------------------------------------
// UMBRA NEUMORPHICĂ PENTRU BUTOANE
// aici se face magia aia de buton “moale”, premium,
// de zici că-l poți apăsa și cu sufletul, nu doar cu degetul.
// ------------------------------------------------------------

private fun Modifier.neuButtonShadow(
    cornerRadius: Dp,
    pressed: Boolean = false
): Modifier = drawBehind {
    val corner = cornerRadius.toPx()
    val darkShadow = Color(0xFF98A0BC)

    if (!pressed) {
        // lumină sus-stânga, ca în viață: speranța vine de acolo
        drawRoundRect(
            color = Color.White.copy(alpha = 0.55f),
            topLeft = Offset((-1).dp.toPx(), (-1).dp.toPx()),
            size = size,
            cornerRadius = CornerRadius(corner, corner)
        )
        drawRoundRect(
            color = Color.White.copy(alpha = 0.22f),
            topLeft = Offset((-2).dp.toPx(), (-2).dp.toPx()),
            size = size,
            cornerRadius = CornerRadius(corner, corner)
        )
        drawRoundRect(
            color = Color.White.copy(alpha = 0.08f),
            topLeft = Offset((-3).dp.toPx(), (-3).dp.toPx()),
            size = size,
            cornerRadius = CornerRadius(corner, corner)
        )

        // umbra jos-dreapta, că fără ea totul pare lipit de perete
        drawRoundRect(
            color = darkShadow.copy(alpha = 0.90f),
            topLeft = Offset(2.dp.toPx(), 2.dp.toPx()),
            size = size,
            cornerRadius = CornerRadius(corner, corner)
        )
        drawRoundRect(
            color = darkShadow.copy(alpha = 0.60f),
            topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
            size = size,
            cornerRadius = CornerRadius(corner, corner)
        )
        drawRoundRect(
            color = darkShadow.copy(alpha = 0.34f),
            topLeft = Offset(6.dp.toPx(), 6.dp.toPx()),
            size = size,
            cornerRadius = CornerRadius(corner, corner)
        )
    } else {
        // la apăsare inversăm subtil senzația,
        // să pară că butonul intră puțin în decor
        drawRoundRect(
            color = darkShadow.copy(alpha = 0.28f),
            topLeft = Offset((-1).dp.toPx(), (-1).dp.toPx()),
            size = size,
            cornerRadius = CornerRadius(corner, corner)
        )
        drawRoundRect(
            color = darkShadow.copy(alpha = 0.14f),
            topLeft = Offset((-2).dp.toPx(), (-2).dp.toPx()),
            size = size,
            cornerRadius = CornerRadius(corner, corner)
        )
        drawRoundRect(
            color = darkShadow.copy(alpha = 0.06f),
            topLeft = Offset((-3).dp.toPx(), (-3).dp.toPx()),
            size = size,
            cornerRadius = CornerRadius(corner, corner)
        )

        drawRoundRect(
            color = Color.White.copy(alpha = 0.20f),
            topLeft = Offset(1.dp.toPx(), 1.dp.toPx()),
            size = size,
            cornerRadius = CornerRadius(corner, corner)
        )
        drawRoundRect(
            color = Color.White.copy(alpha = 0.10f),
            topLeft = Offset(2.dp.toPx(), 2.dp.toPx()),
            size = size,
            cornerRadius = CornerRadius(corner, corner)
        )
        drawRoundRect(
            color = Color.White.copy(alpha = 0.04f),
            topLeft = Offset(3.dp.toPx(), 3.dp.toPx()),
            size = size,
            cornerRadius = CornerRadius(corner, corner)
        )
    }
}

@Composable
fun CalendarScreen() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val roLocale = remember { Locale.forLanguageTag("ro") }

    // ticker discret ca să reîmprospătăm mesajul de progres din când în când
    // fără să stea app-ul cu capul în nori
    var tick by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(10_000L)
            tick++
        }
    }

    // state-ul principal al ecranului
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var direction by remember { mutableStateOf(1) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var todayAnimationTrigger by remember { mutableStateOf(0) }

    // schimbarea lunii + direcția animației
    fun changeMonth(newMonth: YearMonth) {
        direction = if (newMonth > currentMonth) 1 else -1
        currentMonth = newMonth
    }

    val currentNow = DEBUG_TIME ?: LocalDateTime.now()
    val today = remember(tick, DEBUG_TIME) { (DEBUG_TIME ?: LocalDateTime.now()).toLocalDate() }
    val todayShift = remember(tick, DEBUG_TIME) {
        SIMULATE_SHIFT_TODAY ?: getEffectiveShift(DEBUG_TIME ?: LocalDateTime.now())
    }

    // ------------------------------------------------------------
    // CALCUL ORE NORMĂ
    // luăm doar zilele lucrătoare de luni până vineri,
    // și excludem concediul, că n-avem chiar inimă de piatră.
    // ------------------------------------------------------------
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

    // ore efectiv lucrate în lună după logica de ture
    val workedHours = remember(currentMonth) {
        (1..currentMonth.lengthOfMonth()).count { day ->
            val date = currentMonth.atDay(day)
            val isWorkedShift = getShiftForDate(date) in listOf("SC1", "SC2", "SC3")
            isWorkedShift && !isVacation(date)
        } * 8
    }

    val overtime = workedHours - normHours
    var progressText by remember { mutableStateOf("") }

    // actualizăm textul de progres la fiecare tick sau schimbare relevantă
    LaunchedEffect(todayShift, tick) {
        val refreshedNow = DEBUG_TIME ?: LocalDateTime.now()
        progressText = getShiftProgress(todayShift, refreshedNow.toLocalTime())
    }

    // când revine aplicația în prim-plan, refacem mesajul
    // să nu rămână blocat în trecut ca un unchi care povestește armata
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val refreshedNow = DEBUG_TIME ?: LocalDateTime.now()
                val effectiveShift = SIMULATE_SHIFT_TODAY ?: getEffectiveShift(refreshedNow)
                progressText = getShiftProgress(effectiveShift, refreshedNow.toLocalTime())
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val overtimeText = when {
        overtime > 0 -> "OVERTIME = $overtime ore"
        overtime < 0 -> "OVERTIME = $overtime ore"
        else -> "Frățioare, luna asta ești pe 0"
    }

    val overtimeColor = when {
        overtime > 0 -> Color(0xFF0F8A3B)
        overtime < 0 -> Color(0xFFD65A5A)
        else -> Color(0xFF555555)
    }

    // ------------------------------------------------------------
    // CONTAINERUL MARE AL ECRANULUI
    // aici intră tot decorul: fundalul, lumina, atmosfera.
    // pe scurt: hainele bune ale calendarului.
    // ------------------------------------------------------------
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFEEF1F5),
                        Color(0xFFEEF1F5),
                        Color(0xFFD7E1F7),
                        Color(0xFFBCCBFF)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1400f, 2400f)
                )
            )
            .drawBehind {
                // lumină caldă sus-stânga — colțul ăla “premium”
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFEEF1F5).copy(alpha = 0.95f),
                            Color(0xFFE9EDF2).copy(alpha = 0.95f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.10f, size.height * 0.08f),
                        radius = size.minDimension * 0.42f
                    ),
                    radius = size.minDimension * 0.42f,
                    center = Offset(size.width * 0.10f, size.height * 0.08f)
                )

                // voal subtil peste fundal, să nu pară “plat ca foaia de tablă”
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

                // pată rece jos-dreapta, ca să echilibrăm povestea vizuală
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
        // ------------------------------------------------------------
        // CONȚINUTUL PRINCIPAL
        // header, overtime, navigație lună, butonul Azi și grila calendarului.
        // ------------------------------------------------------------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 14.dp)
                .padding(bottom = 140.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // titlul aplicației – simplu, aerisit, cu umbră serioasă
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

            // mesajul de overtime – adevărul gol-goluț, dar frumos colorat
            Text(
                text = overtimeText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = overtimeColor
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ------------------------------------------------------------
            // NAVIGAȚIA LUNII
            // stânga, titlu, dreapta – sfânta treime a calendarului.
            // ------------------------------------------------------------
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

            // butonul care te aduce înapoi la prezent,
            // că uneori omul pleacă prin luni viitoare și trebuie readus cu blândețe
            SoftPillButton(
                text = "Azi",
                onClick = {
                    changeMonth(YearMonth.from(currentNow))
                    todayAnimationTrigger++
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // antetul zilelor săptămânii
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

            // ------------------------------------------------------------
            // ZONA GRILEI CALENDARULUI
            // luna intră și iese cu animație fină,
            // ca un actor care știe când să apară pe scenă.
            // ------------------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 24.dp)
            ) {
                AnimatedContent(
                    targetState = currentMonth,
                    transitionSpec = {
                        (
                                slideInHorizontally(
                                    animationSpec = tween(620, easing = FastOutSlowInEasing),
                                    initialOffsetX = { fullWidth ->
                                        (fullWidth * 0.18f).toInt() * direction
                                    }
                                ) +
                                        fadeIn(animationSpec = tween(500)) +
                                        scaleIn(
                                            initialScale = 0.985f,
                                            animationSpec = tween(420, easing = FastOutSlowInEasing)
                                        )
                                ) togetherWith (
                                slideOutHorizontally(
                                    animationSpec = tween(420, easing = FastOutSlowInEasing),
                                    targetOffsetX = { fullWidth ->
                                        -(fullWidth * 0.18f).toInt() * direction
                                    }
                                ) +
                                        fadeOut(animationSpec = tween(380)) +
                                        scaleOut(
                                            targetScale = 0.985f,
                                            animationSpec = tween(320, easing = FastOutSlowInEasing)
                                        )
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
                            bottom = 28.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 6.dp)
                    ) {
                        items(monthCells) { cell ->
                            val realShift = getShiftForDate(cell.date)
                            val vacation = isVacation(cell.date)

                            // dacă e concediu, afișăm CO.
                            // tura reală o știm noi, dar nu ne mai doare acum.
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

        // ------------------------------------------------------------
        // LEGENDA
        // jos, frumos, cuminte, să știe omul dintr-o privire ce culoare cu ce se ceartă.
        // ------------------------------------------------------------
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

        // ------------------------------------------------------------
        // FOOTERUL ZILEI CURENTE
        // mic rezumat elegant: azi ce zi e și ce simte tura despre asta.
        // ------------------------------------------------------------
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
                        today.dayOfWeek.getDisplayName(JavaTextStyle.FULL, roLocale)
                    }, ${today.dayOfMonth} ${
                        today.month.getDisplayName(JavaTextStyle.FULL, roLocale)
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

        // ------------------------------------------------------------
        // POPUP-UL ZILEI SELECTATE
        // apeși pe o zi și primești verdictul:
        // ce ai fost, ce ești sau ce urmează să fii.
        // ------------------------------------------------------------
        selectedDate?.let { date ->
            val realShift = getShiftForDate(date)
            val vacation = isVacation(date)
            val displayShift = if (vacation) "CO" else realShift

            AlertDialog(
                onDismissRequest = { selectedDate = null },
                icon = {},
                title = {
                    Column {
                        Text(
                            text = "Zi selectată",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = popupAccentColor(displayShift)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // linia mică de accent – detaliu mic, efect mare
                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .fillMaxWidth(0.35f)
                                .clip(RoundedCornerShape(99.dp))
                                .background(popupAccentColor(displayShift).copy(alpha = 0.75f))
                        )
                    }
                },
                text = {
                    val naturalShift = shiftDisplayName(displayShift)

                    val shiftSentence = when {
                        date.isBefore(today) -> "Ai fost $naturalShift"
                        date.isEqual(today) -> "Ești $naturalShift"
                        else -> "O să fii $naturalShift"
                    }

                    // textul cu personalitate – nu mult, nu puțin, cât să zâmbești
                    val funnyText = when {
                        vacation && date.isBefore(today) -> "Ai fost în concediu. Elegant."
                        vacation && date.isEqual(today) -> "Ești în concediu. Trăiești corect."
                        vacation && date.isAfter(today) -> "Libertateeee. Ține-te bine."

                        displayShift == "LIB" && date.isBefore(today) -> "Ai avut o zi de respiro."
                        displayShift == "LIB" && date.isEqual(today) -> "Azi nu te deranjează fabrica."
                        displayShift == "LIB" && date.isAfter(today) -> "Se vede o pauză frumoasă la orizont."

                        displayShift == "SC1" && date.isBefore(today) -> "Ai învins dimineața."
                        displayShift == "SC1" && date.isEqual(today) -> "Cafeaua încă lucrează pentru tine."
                        displayShift == "SC1" && date.isAfter(today) -> "Pregătește cafeaua și curajul."

                        displayShift == "SC2" && date.isBefore(today) -> "Ai dus-o până la capăt."
                        displayShift == "SC2" && date.isEqual(today) -> "Tura elastică e în desfășurare."
                        displayShift == "SC2" && date.isAfter(today) -> "Urmează partea aia ciudată a zilei."

                        displayShift == "SC3" && date.isBefore(today) -> "Ai fost soldatul neonului."
                        displayShift == "SC3" && date.isEqual(today) -> "Mod liliac profesional: activ."
                        displayShift == "SC3" && date.isAfter(today) -> "Noaptea te așteaptă la program."

                        else -> ""
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", roLocale)
                        val formattedDate = date.format(formatter)
                            .replaceFirstChar { it.uppercase(roLocale) }

                        Text(
                            text = formattedDate,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1F2937)
                        )

                        Text(
                            text = shiftSentence,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = popupAccentColor(displayShift)
                        )

                        if (funnyText.isNotBlank()) {
                            Text(
                                text = funnyText,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = Color(0xFF4B5563)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { selectedDate = null },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = popupAccentColor(displayShift),
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp
                        )
                    ) {
                        Text(
                            text = "OK",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                shape = RoundedCornerShape(28.dp),
                containerColor = Color(0xFFF7F9FC),
                tonalElevation = 0.dp
            )
        }
    }
}

// ------------------------------------------------------------
// CONSTRUCȚIA CELULELOR CALENDARULUI
// punem zilele lunii curente, plus cele de umplere din luna anterioară și următoare,
// ca grila să stea dreaptă și demnă, nu șchioapă.
// ------------------------------------------------------------
private fun buildCalendarCells(month: YearMonth): List<CalendarCell> {
    val first = month.atDay(1)

    // mutăm începutul săptămânii pe luni
    val offset = (first.dayOfWeek.value + 6) % 7

    val prevMonth = month.minusMonths(1)
    val prevMonthLength = prevMonth.lengthOfMonth()

    val currentDays = (1..month.lengthOfMonth()).map { day ->
        CalendarCell(
            date = month.atDay(day),
            day = day,
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

    // 5 sau 6 rânduri, după caz; nu forțăm luna să încapă unde nu vrea
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

    // o mică animație de scalare la apăsare;
    // adică feedback bun, nu apăsat “în gol”
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

    // animăm ziua curentă ca să iasă în evidență
    // vedeta lunii trebuie să intre în scenă cum se cuvine
    val scaleAnim = remember(isToday) { Animatable(1f) }
    var startTodayAnimation by remember(isToday) { mutableStateOf(false) }

    LaunchedEffect(isToday, animationTrigger) {
        if (isToday) {
            startTodayAnimation = false
            scaleAnim.snapTo(1f)

            // mică pauză înainte de “respirația” vizuală
            delay(700)

            startTodayAnimation = true

            scaleAnim.animateTo(
                targetValue = 1.72f,
                animationSpec = tween(420, easing = FastOutSlowInEasing)
            )

            scaleAnim.animateTo(
                targetValue = 1.38f,
                animationSpec = tween(660, easing = FastOutSlowInEasing)
            )

            scaleAnim.animateTo(
                targetValue = 1.52f,
                animationSpec = tween(820, easing = FastOutSlowInEasing)
            )
        } else {
            startTodayAnimation = false
            scaleAnim.snapTo(1f)
        }
    }

    // animațiile infinite pentru border-ul zilei de azi
    // pulsează discret, nu ca pomul de Crăciun
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

    val borderWidthAnim by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderWidth"
    )

    val neoShadow = Color(0xFF98ABBE)
    val baseAlpha = if (isOtherMonth) 0.42f else 1f

    // sărbătorile legale ies în roșu, că merită tratament special
    val dateColor = if (isHoliday && !isOtherMonth) {
        Color(0xFFD32F2F)
    } else {
        Color(0xFF202020)
    }

    val borderColor = when {
        isToday && startTodayAnimation -> Color(0xFFE53935).copy(alpha = borderAnim)
        isToday -> Color(0xFFE53935).copy(alpha = 0.65f)
        isOtherMonth -> Color(0xFF000000).copy(alpha = 0.65f)
        else -> Color.White.copy(alpha = 0.42f)
    }

    // intensitatea glow-ului în funcție de ce afișăm
    // CO are tratament un pic mai special, că merită
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
            .padding(bottom = if (isToday) 12.dp else 0.dp)
    ) {
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

                    // highlight-ul alb de sus-stânga
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.95f),
                        topLeft = Offset((-2).dp.toPx(), (-2).dp.toPx()),
                        size = size,
                        cornerRadius = CornerRadius(corner, corner)
                    )
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.45f),
                        topLeft = Offset((-4).dp.toPx(), (-4).dp.toPx()),
                        size = size,
                        cornerRadius = CornerRadius(corner, corner)
                    )
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.18f),
                        topLeft = Offset((-7).dp.toPx(), (-7).dp.toPx()),
                        size = size,
                        cornerRadius = CornerRadius(corner, corner)
                    )

                    // umbra inferioară – partea care dă volum și “carne” celulei
                    drawRoundRect(
                        color = neoShadow.copy(alpha = 0.90f),
                        topLeft = Offset(3.dp.toPx(), 3.dp.toPx()),
                        size = size,
                        cornerRadius = CornerRadius(corner, corner)
                    )
                    drawRoundRect(
                        color = neoShadow.copy(alpha = 0.60f),
                        topLeft = Offset(6.dp.toPx(), 6.dp.toPx()),
                        size = size,
                        cornerRadius = CornerRadius(corner, corner)
                    )
                    drawRoundRect(
                        color = neoShadow.copy(alpha = if (isToday) 0.36f else 0.34f),
                        topLeft = Offset(9.dp.toPx(), 9.dp.toPx()),
                        size = size,
                        cornerRadius = CornerRadius(corner, corner)
                    )
                }
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceSoft)
                .drawWithContent {
                    drawContent()

                    if (!isOtherMonth) {
                        // glow-ul de schimb – subtil, colorat, viu
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

                        // pentru concediu adăugăm și o lumină albă suplimentară,
                        // ca să pară mai “visător”, mai liber, mai fără pontaj
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
                .border(
                    width = if (isToday) borderWidthAnim.dp else 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(onClick = onClick)
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
                        // dacă e concediu, afișăm CO direct;
                        // altfel arătăm schimbul real
                        text = if (displayShift == "CO") "CO" else realShift,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = shiftColor,
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            shadow = Shadow(
                                color = glowColor.copy(
                                    alpha = when (displayShift) {
                                        "CO" -> 0.28f
                                        "LIB" -> 0.24f
                                        else -> 0.22f
                                    }
                                ),
                                offset = Offset.Zero,
                                blurRadius = when (displayShift) {
                                    "CO" -> 5.5f
                                    "LIB" -> 4.8f
                                    else -> 4.2f
                                }
                            )
                        )
                    )
                }
            }
        }
    }
}
// puțin ajutor pentru mărimea glow-ului,
// în funcție de cât de specială e celula
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
        // pătrățelul colorat – mic, dar spune multe
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