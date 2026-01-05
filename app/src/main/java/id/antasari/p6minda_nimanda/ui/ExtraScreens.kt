// Package sudah disesuaikan dengan ID Anda
package id.antasari.p6minda_nimanda.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border // <-- Import untuk Modifier.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Import data (package sudah disesuaikan)
import id.antasari.p6minda_nimanda.data.DiaryEntry
import id.antasari.p6minda_nimanda.data.DiaryRepository
import id.antasari.p6minda_nimanda.data.MindaDatabase
// Import Coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
// Import java.time
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

/* ================== CALENDAR (Screen 6) ================== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen() {
    val context = LocalContext.current
    val db = remember { MindaDatabase.getInstance(context) }
    val repo = remember { DiaryRepository(db.diaryDao()) }

    var groupedEntries by remember { mutableStateOf<Map<LocalDate, List<DiaryEntry>>>(emptyMap()) }

    LaunchedEffect(Unit) {
        val result = withContext(Dispatchers.IO) {
            val all = repo.allEntries()
            all.groupBy { it.timestamp.toLocalDate() }
                .toSortedMap(compareByDescending { it }) // terbaru di atas
        }
        groupedEntries = result
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Calendar",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { innerPadding ->
        if (groupedEntries.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No entries yet.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Your calendar will light up as you start journaling.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val dateGroups = groupedEntries.entries.toList()
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                dateGroups.forEach { (date, entriesOnDate) ->
                    item(key = "header-$date") {
                        Text(
                            text = formatDateHeader(date),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                    items(
                        items = entriesOnDate,
                        key = { it.id }
                    ) { entry ->
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "${entry.mood} ${entry.title}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = entry.content.take(80),
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 2,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

/* ================== INSIGHTS (Screen 7) ================== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    onTryTemplates: () -> Unit = {}
) {
    val context = LocalContext.current
    val db = remember { MindaDatabase.getInstance(context) }
    val repo = remember { DiaryRepository(db.diaryDao()) }
    var entries by remember { mutableStateOf<List<DiaryEntry>>(emptyList()) }

    LaunchedEffect(Unit) {
        entries = withContext(Dispatchers.IO) { repo.allEntries() }
    }

    val totalEntries = entries.size
    val distinctMoods = entries.map { it.mood }.toSet().size
    val datesSet = remember(entries) { entries.map { it.timestamp.toLocalDate() }.toSet() }
    val currentStreak = remember(datesSet) { calcCurrentStreak(datesSet) }
    val longestStreak = remember(datesSet) { calcLongestStreak(datesSet) }

    val moodCounts = remember(entries) { entries.groupingBy { it.mood }.eachCount() }
    val totalForPercent = moodCounts.values.sum().coerceAtLeast(1)
    val trendData = remember(moodCounts) {
        moodCounts.entries.sortedByDescending { it.value }
            .map { (mood, count) ->
                Triple(mood, (count.toFloat() / totalForPercent.toFloat()), moodLabel(mood))
            }
    }

    val today = LocalDate.now()
    val last7 = remember(entries) { (0..6).map { today.minusDays(it.toLong()) } }
    val hasEntryOn = remember(entries) { last7.associateWith { it in datesSet } }

    @Composable
    fun BorderedCard(content: @Composable ColumnScope.() -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 0.8.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            content = content
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Insights",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // === Summary (border) ===
            item {
                BorderedCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SummaryColumn(number = totalEntries, label = "Entries")
                        SummaryColumn(number = distinctMoods, label = "Moods")
                        SummaryColumn(number = currentStreak, label = "Streak")
                    }
                }
            }

            // === Streak (border) ===
            item {
                BorderedCard {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "Diary Streak",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            last7.forEach { d ->
                                DayCheck(
                                    date = d,
                                    checked = hasEntryOn[d] == true,
                                    size = 34.dp
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔥 Longest chain: ", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = longestStreak.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // === CTA (satu-satunya dengan background) ===
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                "No ideas to write about?",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Try out the writing templates!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Button(
                            onClick = onTryTemplates,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Try it!")
                        }
                    }
                }
            }

            // === Trends (border) ===
            item {
                BorderedCard {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            text = "Trends",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))
                        if (trendData.isEmpty()) {
                            Text(
                                text = "No mood data yet.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Donut kiri - weight 1 agar seimbang
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val donutCols = trendData.map { (mood, _, _) -> moodColor(mood) }
                                    DonutChart(
                                        fractions = trendData.map { it.second },
                                        colors = donutCols,
                                        size = 92.dp,
                                        thickness = 22.dp
                                    )
                                }
                                // Legend kanan - weight 1
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    val donutCols = trendData.map { (mood, _, _) -> moodColor(mood) }
                                    trendData.forEachIndexed { idx, (mood, frac, label) ->
                                        LegendRowTight(
                                            dotColor = donutCols[idx],
                                            label = moodLabel(mood),
                                            percent = (frac * 100f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


/* ================== SETTINGS (Screen 8) ================== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(userName: String?) {
    val displayName = userName ?: "Anonymous"
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // PERSONAL
            item { SectionHeader(title = "PERSONAL") }
            item { SettingsItem(leading = { Icon(Icons.Filled.Person, null) }, label = "Your name") }
            item { SettingsItem(leading = { Icon(Icons.Filled.Lock, null) }, label = "Password (PIN)") }
            item { SettingsItem(leading = { Icon(Icons.Filled.Palette, null) }, label = "Themes") }

            // MY DATA
            item { SectionHeader(title = "MY DATA") }
            item { SettingsItem(leading = { Icon(Icons.Filled.Cloud, null) }, label = "Backup & Restore") }
            item { SettingsItem(leading = { Icon(Icons.Filled.Delete, null) }, label = "Delete app data") }

            // REMINDERS
            item { SectionHeader(title = "REMINDERS") }
            item { SettingsItem(leading = { Icon(Icons.Filled.Notifications, null) }, label = "Daily logging reminder") }

            // OTHER
            item { SectionHeader(title = "OTHER") }
            item { SettingsItem(leading = { Icon(Icons.Filled.Share, null) }, label = "Share with friends") }
            item { SettingsItem(leading = { Icon(Icons.Filled.Help, null) }, label = "Help and Feedback") }
            item { SettingsItem(leading = { Icon(Icons.Filled.Star, null) }, label = "Rate app") }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier.padding(vertical = 6.dp)
    )
}

@Composable
private fun SettingsItem(
    leading: @Composable (() -> Unit),
    label: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 0.8.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(22.dp), contentAlignment = Alignment.Center) { leading() }
        Spacer(Modifier.width(14.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Icon(Icons.Filled.ChevronRight, contentDescription = "Next")
    }
}

/* ================== Helper UI ================== */
@Composable
private fun SummaryColumn(number: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.widthIn(min = 60.dp)
    ) {
        Text(text = number.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun DayCheck(date: LocalDate, checked: Boolean, size: Dp) {
    val monthFmt = remember { DateTimeFormatter.ofPattern("MMM") }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (checked) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(Icons.Filled.Check, contentDescription = "checked", tint = Color.White)
            } else {
                Text("–", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(text = "${date.dayOfMonth}", style = MaterialTheme.typography.bodySmall)
        Text(text = date.format(monthFmt), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun DonutChart(
    fractions: List<Float>,
    colors: List<Color>,
    size: Dp,
    thickness: Dp
) {
    val normalized = if (fractions.isEmpty()) listOf(1f) else fractions
    val total = normalized.sum()
    val parts = normalized.map { it / total }
    val fallbackColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = Modifier.size(size)) {
        var start = -90f
        parts.forEachIndexed { idx, p ->
            val color = colors.getOrElse(idx) { fallbackColor }
            val sweepAngle = p * 360f
            drawArc(
                color = color,
                startAngle = start,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = thickness.toPx(), cap = StrokeCap.Round)
            )
            start += sweepAngle // Perbaikan dari bug di modul
        }
    }
}

/** Legend row rapat, label bodySmall, persen bodySmall, label dekat ke % */
@Composable
private fun LegendRowTight(dotColor: Color, label: String, percent: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
        Text("${percent.roundToInt()}%", style = MaterialTheme.typography.bodySmall)
    }
}

/* ================== Helper tanggal & Mood ================== */
private fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

private val dateHeaderFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEEE, d MMM yyyy")

private fun formatDateHeader(date: LocalDate): String =
    date.format(dateHeaderFormatter)

private fun calcCurrentStreak(dates: Set<LocalDate>): Int {
    if (dates.isEmpty()) return 0
    var streak = 0
    var cursor = LocalDate.now()
    while (cursor in dates) {
        streak++
        cursor = cursor.minusDays(1)
    }
    return streak
}

private fun calcLongestStreak(dates: Set<LocalDate>): Int {
    if (dates.isEmpty()) return 0
    val sorted = dates.sorted()
    var best = 1
    var run = 1
    (1 until sorted.size).forEach { i ->
        if (sorted[i-1].plusDays(1) == sorted[i]) {
            run++
        } else {
            best = maxOf(best, run)
            run = 1
        }
    }
    return maxOf(best, run)
}

// FUNGSI INI SEKARANG TIDAK @COMPOSABLE
private fun moodColor(mood: String): Color {
    return when (mood.lowercase()) {
        "😊" -> Color(0xFF4CAF50) // hijau
        "😌" -> Color(0xFF42A5F5) // biru
        "😔" -> Color(0xFFFF5350) // merah
        "😠" -> Color(0xFFFF7043) // orange
        "😩" -> Color(0xFFAB47BC) // ungu
        "😎" -> Color(0xFF26A69A) // teal
        else -> Color.Gray // Fallback
    }
}

// FUNGSI INI SEKARANG TIDAK @COMPOSABLE
private fun moodLabel(mood: String): String = when (mood.lowercase()) {
    "😊" -> "Happy"
    "😌" -> "Calm"
    "😔" -> "Sad"
    "😠" -> "Angry"
    "😩" -> "Tired"
    "😎" -> "Cool"
    else -> mood
}