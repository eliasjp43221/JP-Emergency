package com.example.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Source
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.components.CategoryIconHelper
import com.example.components.VerificationStatusBadge
import com.example.models.IssueReportEntry
import com.example.models.ServiceEntry
import com.example.utils.BengaliSearchAndFormatUtils

private val reportIssueTypes = listOf(
    "ভুল ফোন নম্বর",
    "ঠিকানা পরিবর্তন হয়েছে",
    "প্রতিষ্ঠানটি বর্তমানে বন্ধ",
    "সময়সূচি সঠিক নয়",
    "অন্যান্য তথ্যগত ত্রুটি"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ServiceDetailsScreen(
    entry: ServiceEntry?,
    onBackClick: () -> Unit,
    onCallNumber: (String) -> Unit,
    onOpenMap: (ServiceEntry) -> Unit,
    onShareService: (ServiceEntry) -> Unit,
    onToggleFavorite: (ServiceEntry) -> Unit,
    onSubmitIssueReport: (
        serviceId: String,
        serviceNameBn: String,
        issueTypeBn: String,
        detailsBn: String,
        suggestedCorrectionBn: String,
        onSaved: (IssueReportEntry) -> Unit
    ) -> Unit,
    onShareSavedReport: (IssueReportEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    var showReportDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "সেবার বিস্তারিত তথ্য",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("details_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ফিরে যান"
                        )
                    }
                },
                actions = {
                    if (entry != null) {
                        IconButton(
                            onClick = { onToggleFavorite(entry) },
                            modifier = Modifier.testTag("details_favorite_button")
                        ) {
                            Icon(
                                imageVector = if (entry.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (entry.isFavorite) "প্রিয় থেকে সরান" else "প্রিয়তে যুক্ত করুন",
                                tint = if (entry.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (entry == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "সেবার তথ্য খুঁজে পাওয়া যায়নি।",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            return@Scaffold
        }

        if (showReportDialog) {
            ReportIssueDialog(
                service = entry,
                onDismiss = { showReportDialog = false },
                onSubmit = { issueType, details, correction, shareImmediately ->
                    showReportDialog = false
                    onSubmitIssueReport(
                        entry.id,
                        entry.nameBn,
                        issueType,
                        details,
                        correction
                    ) { savedReport ->
                        if (shareImmediately) {
                            onShareSavedReport(savedReport)
                        }
                    }
                }
            )
        }

        val isPrimaryPhoneValid = entry.isPrimaryPhoneValid
        val isSecondaryPhoneValid = entry.isSecondaryPhoneValid
        val accentColor = CategoryIconHelper.getAccentColor(entry.category)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("service_details_scroll"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(accentColor.copy(alpha = 0.14f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = CategoryIconHelper.getIcon(entry.category),
                                    contentDescription = entry.category.titleBn,
                                    tint = accentColor,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = entry.nameBn,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (entry.officialNameEn.isNotBlank()) {
                                    Text(
                                        text = entry.officialNameEn,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            VerificationStatusBadge(status = entry.status)

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "উপজেলা/এলাকা: ${entry.upazila.nameBn}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "বিভাগ: ${entry.category.titleBn}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }

                            if (entry.isEmergency24hVerified) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.errorContainer
                                ) {
                                    Text(
                                        text = "২৪ ঘণ্টা জরুরি সেবা যাচাইকৃত",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Primary Action Buttons ("কল করুন", "ম্যাপে দেখুন", "তথ্য শেয়ার করুন", "তথ্যে সমস্যা জানান")
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onCallNumber(entry.primaryPhone) },
                            enabled = isPrimaryPhoneValid,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("detail_call_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (entry.isNationalEmergencyHotline) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isPrimaryPhoneValid) "কল করুন (${entry.primaryPhone})" else "কল করার নম্বর উপলব্ধ নয়",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        if (isSecondaryPhoneValid) {
                            OutlinedButton(
                                onClick = { onCallNumber(entry.secondaryPhone) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("detail_secondary_call_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Call, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("বিকল্প নম্বরে কল করুন (${entry.secondaryPhone})")
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onOpenMap(entry) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("detail_map_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Map,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ম্যাপে দেখুন")
                            }

                            OutlinedButton(
                                onClick = { onShareService(entry) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("detail_share_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("তথ্য শেয়ার করুন")
                            }
                        }

                        OutlinedButton(
                            onClick = { showReportDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("detail_report_button"),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("তথ্যে সমস্যা জানান")
                        }
                    }
                }
            }

            // Detailed Information Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "যোগাযোগ ও সেবার বিবরণ",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        DetailInfoRow(
                            icon = Icons.Default.LocationOn,
                            label = "ঠিকানা ও এলাকা",
                            value = "${entry.addressBn} (${entry.upazila.nameBn})"
                        )

                        HorizontalDivider()

                        DetailInfoRow(
                            icon = Icons.Default.Call,
                            label = "প্রাথমিক যোগাযোগের নম্বর",
                            value = entry.primaryPhone.ifBlank { "যাচাইকৃত নম্বর পাওয়া যায়নি" }
                        )

                        if (entry.secondaryPhone.isNotBlank()) {
                            HorizontalDivider()
                            DetailInfoRow(
                                icon = Icons.Default.Call,
                                label = "বিকল্প ফোন নম্বর",
                                value = entry.secondaryPhone
                            )
                        }

                        if (entry.email.isNotBlank()) {
                            HorizontalDivider()
                            DetailInfoRow(
                                icon = Icons.Default.Email,
                                label = "ইমেইল",
                                value = entry.email
                            )
                        }

                        HorizontalDivider()

                        DetailInfoRow(
                            icon = Icons.Default.AccessTime,
                            label = "সেবার সময়সূচি",
                            value = entry.openingHoursBn.ifBlank { "সময়সূচি যাচাই করা হয়নি — সেবা গ্রহণের পূর্বে ফোনে জেনে নিন" }
                        )

                        HorizontalDivider()

                        Column {
                            Text(
                                text = "প্রাপ্য সেবাসমূহ",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = entry.availableServicesBn,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Source & Verification Metadata Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "তথ্য যাচাইকরণ ও উৎস (Source Verification)",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        DetailInfoRow(
                            icon = Icons.Default.Verified,
                            label = "যাচাই স্ট্যাটাস ও সর্বশেষ যাচাইয়ের তারিখ",
                            value = "${entry.status.labelBn} • ${entry.lastVerifiedDateBn}\n(${entry.status.descriptionBn})"
                        )

                        DetailInfoRow(
                            icon = Icons.Default.Source,
                            label = "তথ্যের উৎস (Source)",
                            value = if (entry.sourceUrl.isNotBlank()) {
                                "${entry.sourceNameBn}\n(${entry.sourceUrl})"
                            } else {
                                entry.sourceNameBn
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReportIssueDialog(
    service: ServiceEntry,
    onDismiss: () -> Unit,
    onSubmit: (issueType: String, details: String, correction: String, shareImmediately: Boolean) -> Unit
) {
    var selectedType by remember { mutableStateOf(reportIssueTypes.first()) }
    var detailsText by remember { mutableStateOf("") }
    var correctionText by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "তথ্যে সমস্যা বা সংশোধন জানান",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "প্রতিষ্ঠান: ${service.nameBn}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "নোট: এই অ্যাপটি অফলাইনে কাজ করে এবং কোনো গোপন সার্ভারে আপনার ব্যক্তিগত তথ্য পাঠায় না। আপনার রিপোর্টটি ফোনে সংরক্ষিত হবে এবং আপনি চাইলে ইমেইল বা মেসেজের মাধ্যমে ডেভেলপার/কমিউনিটি টিমে শেয়ার করতে পারবেন।",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "সমস্যার ধরন নির্বাচন করুন:",
                    style = MaterialTheme.typography.labelMedium
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (type in reportIssueTypes) {
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type, style = MaterialTheme.typography.labelSmall) },
                            shape = CircleShape
                        )
                    }
                }

                OutlinedTextField(
                    value = detailsText,
                    onValueChange = {
                        detailsText = it
                        validationError = null
                    },
                    label = { Text("সমস্যার সংক্ষিপ্ত বিবরণ *") },
                    placeholder = { Text("যেমন: নম্বরটি বর্তমানে বন্ধ পাওয়া যাচ্ছে...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("report_details_input"),
                    minLines = 2
                )

                OutlinedTextField(
                    value = correctionText,
                    onValueChange = { correctionText = it },
                    label = { Text("সঠিক নম্বর বা ঠিকানা (যদি জানা থাকে)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("report_correction_input"),
                    singleLine = true
                )

                if (validationError != null) {
                    Text(
                        text = validationError!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        if (detailsText.isBlank()) {
                            validationError = "অনুগ্রহ করে সমস্যার সংক্ষিপ্ত বিবরণ লিখুন।"
                        } else {
                            onSubmit(selectedType, detailsText, correctionText, false)
                        }
                    },
                    modifier = Modifier.testTag("save_report_locally_button")
                ) {
                    Text("সংরক্ষণ করুন")
                }
                Button(
                    onClick = {
                        if (detailsText.isBlank()) {
                            validationError = "অনুগ্রহ করে সমস্যার সংক্ষিপ্ত বিবরণ লিখুন।"
                        } else {
                            onSubmit(selectedType, detailsText, correctionText, true)
                        }
                    },
                    modifier = Modifier.testTag("save_and_share_report_button")
                ) {
                    Text("সংরক্ষণ ও শেয়ার")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}
