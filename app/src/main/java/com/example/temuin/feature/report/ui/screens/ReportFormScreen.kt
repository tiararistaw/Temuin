package com.example.temuin.feature.report.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.temuin.feature.report.data.model.ReportStatus
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.temuin.feature.profile.util.readAndCompress
import com.example.temuin.feature.report.data.model.CampusSpots
import com.example.temuin.feature.report.data.model.ReportCategories
import com.example.temuin.feature.report.data.model.ReportDraft
import com.example.temuin.feature.report.data.model.ReportType
import com.example.temuin.feature.report.ui.components.ReportDark
import com.example.temuin.feature.report.ui.components.ReportDarkTeal
import com.example.temuin.feature.report.ui.components.ReportLostRed
import com.example.temuin.feature.report.ui.components.ReportPhoto
import com.example.temuin.feature.report.ui.components.ReportTopBar
import com.example.temuin.feature.report.ui.viewmodel.ReportViewModel
import com.example.temuin.feature.report.util.formatDate
import com.example.temuin.feature.report.util.formatTime
import com.example.temuin.feature.report.util.millisToDateTime
import com.example.temuin.feature.report.util.toMillis
import com.example.temuin.feature.review.ui.components.TemuinBackground
import com.example.temuin.feature.review.ui.components.TemuinBorder
import com.example.temuin.feature.review.ui.components.TemuinLavender
import com.example.temuin.feature.review.ui.components.TemuinMuted
import com.example.temuin.feature.review.ui.components.TemuinPaleBlue
import com.example.temuin.feature.review.ui.components.TemuinTeal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

private const val MAX_PHOTOS = 3
private const val MAX_DESCRIPTION = 500

/**
 * Halaman 3 - Form Tambah / Edit Laporan.
 * [reportId] = null -> mode tambah; terisi -> mode edit laporan milik sendiri.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFormScreen(
    viewModel: ReportViewModel,
    userId: String,
    userName: String,
    defaultContact: String,
    reportId: String? = null,
    initialType: ReportType = ReportType.FOUND,
    onBack: () -> Unit,
    onPublished: (String) -> Unit,
    onUpdated: () -> Unit = onBack
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val existing = remember(reportId, state.reports) { reportId?.let { viewModel.getById(it) } }
    val isEdit = existing != null

    // ---- state form ----
    var type by rememberSaveable(existing?.id, initialType) { mutableStateOf(existing?.type ?: initialType) }
    var name by rememberSaveable(existing?.id) { mutableStateOf(existing?.itemName ?: "") }
    var category by rememberSaveable(existing?.id) {
        mutableStateOf(existing?.category ?: ReportCategories.first())
    }
    var location by rememberSaveable(existing?.id) { mutableStateOf(existing?.location ?: "") }
    var description by rememberSaveable(existing?.id) { mutableStateOf(existing?.description ?: "") }
    var anonymous by rememberSaveable(existing?.id) { mutableStateOf(existing?.isAnonymous ?: false) }
    var contact by rememberSaveable(existing?.id, defaultContact) {
        mutableStateOf(existing?.contact ?: defaultContact)
    }

    val initialDateTime = remember(existing?.id) {
        millisToDateTime(existing?.occurredAtMillis ?: System.currentTimeMillis())
    }
    var date by remember(existing?.id) { mutableStateOf(initialDateTime.toLocalDate()) }
    var time by remember(existing?.id) { mutableStateOf(initialDateTime.toLocalTime().withSecond(0).withNano(0)) }

    val photos: SnapshotStateList<String> = remember(existing?.id) {
        mutableStateListOf<String>().apply { addAll(existing?.photoUrls.orEmpty()) }
    }

    var showErrors by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showCampusDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // ---- picker foto (maks 3) ----
    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(MAX_PHOTOS)
    ) { uris: List<Uri> ->
        val room = MAX_PHOTOS - photos.size
        uris.take(room).forEach { photos.add(it.toString()) }
    }

    // ---- validasi ----
    val photoRequired = type == ReportType.FOUND
    val nameError = showErrors && name.isBlank()
    val locationError = showErrors && location.isBlank()
    val descError = showErrors && description.isBlank()
    val photoError = showErrors && photoRequired && photos.isEmpty()

    fun submit() {
        showErrors = true
        if (name.isBlank() || location.isBlank() || description.isBlank() ||
            (photoRequired && photos.isEmpty())
        ) return

        val draft = ReportDraft(
            type = type,
            itemName = name.trim(),
            category = category,
            location = location.trim(),
            occurredAtMillis = toMillis(date, time),
            description = description.trim(),
            photoUrls = photos.toList(),
            isAnonymous = anonymous,
            contact = contact.trim()
        )

        scope.launch {
            // Kompres hanya foto baru (content://), foto lama (http) dibiarkan.
            val bytes = withContext(Dispatchers.IO) {
                photos.filter { it.startsWith("content://") }
                    .mapNotNull { readAndCompress(context, Uri.parse(it)) }
            }
            if (isEdit && existing != null) {
                viewModel.update(existing.id, draft, bytes) { onUpdated() }
            } else {
                viewModel.publish(draft, userId, userName, bytes) { id -> onPublished(id) }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TemuinBackground)
            .imePadding()
    ) {
        ReportTopBar(
            title = if (isEdit) "Edit Laporan" else "Form Lapor Barang",
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // Banner info
            Surface(shape = RoundedCornerShape(16.dp), color = TemuinPaleBlue) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = TemuinLavender,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Outlined.VerifiedUser,
                                null,
                                tint = TemuinTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            "Bantu Komunitas Kampus",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = ReportDark
                        )
                        Text(
                            "Laporanmu tersinkronisasi otomatis dengan sistem penelusuran barang kampus Temuin.",
                            fontSize = 12.sp, color = TemuinMuted
                        )
                    }
                }
            }

            // Tipe laporan
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FieldLabel("Tipe Laporan")
                Surface(shape = RoundedCornerShape(50), color = TemuinLavender) {
                    Row(Modifier.fillMaxWidth().padding(4.dp)) {
                        TypeToggle(
                            label = "Barang Hilang",
                            selected = type == ReportType.LOST,
                            isFound = false,
                            modifier = Modifier.weight(1f)
                        ) { type = ReportType.LOST }
                        TypeToggle(
                            label = "Barang Ditemukan",
                            selected = type == ReportType.FOUND,
                            isFound = true,
                            modifier = Modifier.weight(1f)
                        ) { type = ReportType.FOUND }
                    }
                }
            }

            // Foto barang
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    FieldLabel(
                        "Foto Barang",
                        required = photoRequired,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "Maksimal $MAX_PHOTOS foto (PNG/JPG)",
                        fontSize = 11.sp,
                        color = TemuinMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = TemuinPaleBlue,
                    border = if (photoError) BorderStroke(1.dp, ReportLostRed) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = photos.size < MAX_PHOTOS) {
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = TemuinLavender,
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.AddAPhoto, null, tint = TemuinTeal)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Pilih Foto dari Galeri",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = ReportDark
                        )
                        Text(
                            "Unggah foto barang yang jelas dari beberapa sudut agar mudah dikenali oleh pemilik.",
                            fontSize = 12.sp, color = TemuinMuted,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                if (photoError) ErrorText("Foto wajib diunggah untuk laporan barang ditemukan.")

                if (photos.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(photos.toList()) { uri ->
                            Box {
                                ReportPhoto(
                                    url = uri,
                                    modifier = Modifier
                                        .size(110.dp)
                                        .clip(RoundedCornerShape(14.dp)),
                                    contentDescription = "Foto barang"
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xCC4B5563),
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .size(24.dp)
                                        .clickable { photos.remove(uri) }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Close,
                                            "Hapus foto",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Nama barang
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FieldLabel("Nama Barang", required = true)
                FormTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Misal: Kunci Motor Vario hitam + gantungan dino",
                    icon = Icons.Outlined.Inventory2,
                    isError = nameError
                )
                if (nameError) ErrorText("Nama barang wajib diisi.")
            }

            // Kategori
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FieldLabel("Kategori Barang", required = true)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ReportCategories) { c ->
                        SelectChip(label = c, selected = category == c) { category = c }
                    }
                }
            }

            // Lokasi
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    FieldLabel(
                        if (type == ReportType.FOUND) "Lokasi Ditemukan" else "Lokasi Terakhir Terlihat",
                        required = true,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = { showCampusDialog = true },
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(
                            Icons.Outlined.NearMe,
                            null,
                            tint = TemuinTeal,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Gunakan Titik Kampus",
                            color = TemuinTeal,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                FormTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = "Misal: Gazebo Gedung D Lantai 2",
                    icon = Icons.Outlined.LocationOn,
                    isError = locationError
                )
                if (locationError) ErrorText("Lokasi wajib diisi.")

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Saran:", fontSize = 11.sp, color = TemuinMuted)
                    Spacer(Modifier.width(8.dp))
                    LazyRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(CampusSpots) { spot ->
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = TemuinLavender,
                                modifier = Modifier.clickable { location = spot }
                            ) {
                                Text(
                                    spot,
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    ),
                                    fontSize = 11.sp,
                                    color = ReportDark,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Tanggal & waktu
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FieldLabel("Tanggal", required = true)
                    PickerField(Icons.Outlined.CalendarMonth, formatDate(date)) {
                        showDatePicker = true
                    }
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FieldLabel("Waktu Perkiraan")
                    PickerField(Icons.Outlined.Schedule, formatTime(time)) { showTimePicker = true }
                }
            }

            // Deskripsi
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    FieldLabel(
                        "Deskripsi & Ciri Khusus",
                        required = true,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "${description.length}/$MAX_DESCRIPTION",
                        fontSize = 11.sp,
                        color = TemuinMuted
                    )
                }
                OutlinedTextField(
                    value = description,
                    onValueChange = { if (it.length <= MAX_DESCRIPTION) description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    placeholder = {
                        Text(
                            "Contoh: Ada goresan kecil di bagian belakang, gantungan kunci boneka dinosaurus hijau, stiker logo di sudut kiri.",
                            fontSize = 13.sp
                        )
                    },
                    isError = descError,
                    shape = RoundedCornerShape(14.dp),
                    colors = fieldColors()
                )
                if (descError) ErrorText("Deskripsi wajib diisi.")
            }

            // Anonim & kontak
            Surface(shape = RoundedCornerShape(18.dp), color = TemuinPaleBlue) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = TemuinLavender,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.Shield,
                                    null,
                                    tint = TemuinTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Samarkan Identitas (Anonim)",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = ReportDark
                            )
                            Text(
                                "Nama profil kamu tidak akan ditampilkan di papan feed publik.",
                                fontSize = 12.sp, color = TemuinMuted
                            )
                        }
                        Switch(
                            checked = anonymous,
                            onCheckedChange = { anonymous = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = ReportDarkTeal)
                        )
                    }

                    Text(
                        "WhatsApp / Kontak Penyerahan",
                        fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = ReportDark
                    )
                    FormTextField(
                        value = contact,
                        onValueChange = { contact = it },
                        placeholder = "08xx-xxxx-xxxx",
                        icon = Icons.Outlined.Phone
                    )
                }
            }

            state.error?.let {
                Text(it, color = ReportLostRed, fontSize = 12.sp)
            }

            Spacer(Modifier.height(4.dp))
        }

        // Bottom action
        Surface(color = Color.White, shadowElevation = 8.dp) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { submit() },
                    enabled = !state.isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = ReportDarkTeal)
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (isEdit) "Simpan Perubahan" else "Publikasikan Laporan",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (isEdit && existing?.status == ReportStatus.ACTIVE) {
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        enabled = !state.isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, Color(0xFFB54747)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB54747))
                    ) {
                        Icon(
                            Icons.Outlined.Delete,
                            null,
                            tint = Color(0xFFB54747),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Hapus Laporan",
                            color = Color(0xFFB54747),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog && existing != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Laporan", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah kamu yakin ingin menghapus laporan \"${existing.itemName}\"? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteReport(existing.id, onSuccess = onBack)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB54747))
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // ---- dialog tanggal ----
    if (showDatePicker) {
        val dpState = rememberDatePickerState(
            initialSelectedDateMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
            selectableDates = object : SelectableDates {
                // Tanggal kejadian tidak boleh di masa depan.
                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                    utcTimeMillis <= System.currentTimeMillis() + 24L * 60 * 60 * 1000
            }
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dpState.selectedDateMillis?.let {
                        date = Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("Pilih", color = TemuinTeal) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Batal", color = TemuinMuted) }
            }
        ) { DatePicker(state = dpState) }
    }

    // ---- dialog waktu ----
    if (showTimePicker) {
        val tpState = rememberTimePickerState(
            initialHour = time.hour,
            initialMinute = time.minute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    time = LocalTime.of(tpState.hour, tpState.minute)
                    showTimePicker = false
                }) { Text("Pilih", color = TemuinTeal) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Batal", color = TemuinMuted) }
            },
            title = { Text("Waktu Perkiraan") },
            text = { TimePicker(state = tpState) }
        )
    }

    // ---- dialog titik kampus ----
    if (showCampusDialog) {
        AlertDialog(
            onDismissRequest = { showCampusDialog = false },
            confirmButton = {
                TextButton(onClick = { showCampusDialog = false }) { Text("Tutup", color = TemuinMuted) }
            },
            title = { Text("Pilih Titik Kampus") },
            text = {
                Column {
                    CampusSpots.forEach { spot ->
                        Text(
                            text = spot,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    location = spot
                                    showCampusDialog = false
                                }
                                .padding(vertical = 12.dp),
                            color = ReportDark
                        )
                    }
                }
            }
        )
    }
}

// ----------------------------------------------------------------------------
// Komponen kecil khusus form
// ----------------------------------------------------------------------------

@Composable
private fun FieldLabel(text: String, required: Boolean = false, modifier: Modifier = Modifier) {
    Row(modifier) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = ReportDark)
        if (required) Text(" *", color = ReportLostRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
private fun ErrorText(text: String) {
    Text(text, color = ReportLostRed, fontSize = 11.sp)
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = TemuinTeal,
    unfocusedBorderColor = TemuinBorder,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    errorContainerColor = Color.White,
    errorBorderColor = ReportLostRed
)

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, fontSize = 13.sp, maxLines = 1) },
        leadingIcon = { Icon(icon, null, tint = TemuinMuted) },
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(14.dp),
        colors = fieldColors()
    )
}

@Composable
private fun PickerField(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, TemuinBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = TemuinMuted, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Text(text, color = ReportDark, fontSize = 14.sp)
        }
    }
}

@Composable
private fun SelectChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) ReportDarkTeal else Color.White,
        border = if (selected) null else BorderStroke(1.dp, TemuinBorder),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            color = if (selected) Color.White else ReportDark,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TypeToggle(
    label: String,
    selected: Boolean,
    isFound: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) ReportDarkTeal else Color.Transparent,
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isFound) Icons.Filled.CheckCircle else Icons.Filled.PriorityHigh,
                contentDescription = null,
                tint = if (selected) Color.White else ReportDark,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                color = if (selected) Color.White else ReportDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}