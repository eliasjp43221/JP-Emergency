package com.example.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.models.IssueReportEntry
import com.example.models.Upazila
import com.example.repositories.ThemeMode
import com.example.utils.BengaliSearchAndFormatUtils
import com.example.viewmodels.JamalpurUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsAndDataScreen(
    uiState: JamalpurUiState,
    onSetThemeMode: (ThemeMode) -> Unit,
    onSetDefaultUpazila: (Upazila) -> Unit,
    onSetShowSampleDonors: (Boolean) -> Unit,
    onImportJson: (String) -> Unit,
    onSyncDefaultData: () -> Unit,
    onShareIssueReport: (IssueReportEntry) -> Unit,
    onDeleteIssueReport: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var customJsonInput by remember { mutableStateOf("") }
    var showJsonEditor by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "সেটিংস, ডাটা আপডেট ও পরিচিতি",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "অ্যাপের থিম, ডিফল্ট এলাকা, অফলাইন ডাটাবেস আপডেট এবং গোপনীয়তা নীতিমালা",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ১. থিম ও প্রদর্শনী সেটিংস
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "থিম ও প্রদর্শনী (Light / Dark Theme)",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (mode in ThemeMode.entries) {
                            FilterChip(
                                selected = uiState.preferences.themeMode == mode,
                                onClick = { onSetThemeMode(mode) },
                                label = { Text(mode.labelBn) },
                                shape = CircleShape,
                                modifier = Modifier.testTag("theme_chip_${mode.id}")
                            )
                        }
                    }

                    HorizontalDivider()

                    Text(
                        text = "ডিফল্ট উপজেলা ফিল্টার নির্বাচন করুন:",
                        style = MaterialTheme.typography.labelLarge
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (upazila in Upazila.entries) {
                            FilterChip(
                                selected = uiState.preferences.defaultUpazila == upazila,
                                onClick = { onSetDefaultUpazila(upazila) },
                                label = { Text(upazila.nameBn, style = MaterialTheme.typography.labelSmall) },
                                shape = CircleShape
                            )
                        }
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "রক্তদাতা ডিরেক্টরিতে নমুনা মোড দেখান",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "বন্ধ করলে শুধুমাত্র প্রাতিষ্ঠানিক ব্লাড ব্যাংক ও স্বেচ্ছায় সংযোজিত রক্তদাতা দেখা যাবে",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.preferences.showSampleDonors,
                            onCheckedChange = onSetShowSampleDonors
                        )
                    }
                }
            }
        }

        // ২. ডাটা আপডেট ও JSON ইমপোর্ট সিস্টেম (Section 11)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "অফলাইন ডাটাবেস ও JSON আপডেট সিস্টেম",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Text(
                        text = "বর্তমান ডাটাবেস সংস্করণ: v${uiState.preferences.datasetVersion} • মোট সংরক্ষিত সেবা: ${BengaliSearchAndFormatUtils.toBengaliNumber(uiState.allServices.size)}টি",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "নতুন যাচাইকৃত সরকারি বা প্রাতিষ্ঠানিক সেবার তথ্য ভার্সনযুক্ত JSON ফরম্যাটে যাচাই ও ডুপ্লিকেট পরীক্ষা করে লোকাল ডাটাবেসে যুক্ত করতে পারবেন।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onSyncDefaultData,
                            modifier = Modifier.testTag("sync_default_data_button")
                        ) {
                            Text("ডিফল্ট ডাটা রিসেট/সিঙ্ক")
                        }
                        OutlinedButton(
                            onClick = { showJsonEditor = !showJsonEditor },
                            modifier = Modifier.testTag("toggle_json_import_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileUpload,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (showJsonEditor) "JSON বক্স লুকান" else "JSON ইমপোর্ট")
                        }
                    }

                    if (showJsonEditor) {
                        OutlinedTextField(
                            value = customJsonInput,
                            onValueChange = { customJsonInput = it },
                            label = { Text("ভার্সনযুক্ত JSON ডাটা পেস্ট করুন (schemaVersion: 1)") },
                            placeholder = {
                                Text("{\"schemaVersion\": 1, \"datasetVersion\": \"1.0.1\", \"districtNameBn\": \"জামালপুর\", \"lastUpdatedBn\": \"অক্টোবর ২০২৬\", \"services\": [...]}")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("json_import_text_field"),
                            minLines = 4
                        )

                        Button(
                            onClick = { onImportJson(customJsonInput) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("execute_json_import_button")
                        ) {
                            Text("যাচাই ও ইমপোর্ট করুন")
                        }
                    }

                    if (uiState.lastImportResult != null) {
                        val res = uiState.lastImportResult
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (res.isSuccess) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.errorContainer
                                }
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = res.messageBn,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                                if (res.errorsBn.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    for (err in res.errorsBn.take(3)) {
                                        Text(
                                            text = "• $err",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ৩. সংরক্ষিত তথ্য সংশোধন রিপোর্টসমূহ (Issue Reports)
        if (uiState.issueReports.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "আপনার তৈরিকৃত তথ্য সংশোধন রিপোর্ট (${BengaliSearchAndFormatUtils.toBengaliNumber(uiState.issueReports.size)}টি)",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }

            items(
                items = uiState.issueReports,
                key = { "rep_${it.id}" }
            ) { report ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${report.serviceNameBn} (${report.issueTypeBn})",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = report.detailsBn,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        IconButton(onClick = { onShareIssueReport(report) }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "শেয়ার করুন"
                            )
                        }
                        IconButton(onClick = { onDeleteIssueReport(report.id) }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "মুছুন",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        // ৪. অ্যাপ পরিচিতি, তথ্যের দায়মুক্তি ও গোপনীয়তা নীতি (About & Privacy Policy)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "অ্যাপ পরিচিতি (About জামালপুর সেবা)",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Text(
                        text = "সংস্করণ (Version): 1.0.0 (Production Release)\nপ্যাকেজ আইডি: com.jamalpur.sheba\nআওতাধীন এলাকা: জামালপুর জেলার ৭টি উপজেলা (জামালপুর সদর, মেলান্দহ, ইসলামপুর, দেওয়ানগঞ্জ, সরিষাবাড়ী, মাদারগঞ্জ ও বকশীগঞ্জ)।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "উদ্দেশ্য: জামালপুর জেলার সাধারণ মানুষের জন্য জরুরি মুহূর্তে জাতীয় হটলাইন (৯৯৯, ১০২, ১৬২৬৩), সরকারি হাসপাতাল, উপজেলা স্বাস্থ্য কমপ্লেক্স, ৭টি থানার ওসি নম্বর, ফায়ার সার্ভিস স্টেশন, পল্লী বিদ্যুৎ এবং সরকারি দপ্তরের যাচাইকৃত তথ্য ইন্টারনেট ছাড়াই সহজলভ্য করা।",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    HorizontalDivider()

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "তথ্যের নির্ভুলতা ও দায়মুক্তি (Disclaimer)",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                    Text(
                        text = "এই অ্যাপের তথ্যসমূহ বাংলাদেশ জাতীয় তথ্য বাতায়ন (jamalpur.gov.bd), স্বাস্থ্য অধিদপ্তর (DGHS) এবং ফায়ার সার্ভিসের প্রকাশিত ডিরেক্টরি থেকে সংগ্রহ ও যাচাই করা হয়েছে। এটি কোনো সরকারি সংস্থার দাপ্তরিক অ্যাপ নয়। দাপ্তরিক নম্বর সময়ের সাথে পরিবর্তিত হতে পারে; কোনো নম্বরে অসঙ্গতি দেখলে 'তথ্যে সমস্যা জানান' বাটনের মাধ্যমে রিপোর্ট করার অনুরোধ করা হচ্ছে। চরম জরুরি অবস্থায় সর্বদা জাতীয় হটলাইন ৯৯৯ বা ১০২ ব্যবহার করুন।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider()

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PrivacyTip,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "গোপনীয়তা নীতি (Privacy Policy)",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                    Text(
                        text = "১. জামালপুর সেবা অ্যাপ ব্যবহারকারীর কন্টাক্ট লিস্ট, এসএমএস, কল লগ বা ব্যাকগ্রাউন্ড লোকেশন অ্যাক্সেস করে না।\n" +
                            "২. কল করার জন্য শুধুমাত্র অ্যান্ড্রয়েডের নিজস্ব সিস্টেম ডায়ালার (ACTION_DIAL) খোলা হয়।\n" +
                            "৩. কাছাকাছি সেবা খোঁজার জন্য ঐচ্ছিক লোকেশন পারমিশন শুধুমাত্র দূরত্ব গণনার কাজে ডিভাইসের ভেতরে ব্যবহৃত হয়।\n" +
                            "৪. প্রিয় তালিকা, স্বেচ্ছাসেবী রক্তদাতা এন্ট্রি ও রিপোর্ট শুধুমাত্র আপনার ফোনের লোকাল Room ডাটাবেসে সংরক্ষিত থাকে।\n" +
                            "৫. ওপেন-সোর্স লাইসেন্স: MIT License।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
