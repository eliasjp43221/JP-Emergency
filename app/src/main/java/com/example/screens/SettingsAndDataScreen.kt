package com.example.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    onSyncDefaultData: () -> Unit,
    onCallDeveloper: (String) -> Unit,
    onShareIssueReport: (IssueReportEntry) -> Unit,
    onDeleteIssueReport: (Long) -> Unit,
    activeRefreshRateHz: Int = 120,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "settings_header", contentType = "header") {
            Column {
                Text(
                    text = "সেটিংস, ডাটা আপডেট ও পরিচিতি",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "১২০ হার্জ স্মুথ ইন্টারফেস, থিম, ডিফল্ট এলাকা ও ডেভেলপার পরিচিতি",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ১. ১২০ হার্জ আল্ট্রা-স্মুথ পারফরম্যান্স স্ট্যাটাস
        item(key = "settings_120hz_card", contentType = "card") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "120Hz Ultra-Smooth UI Engine সক্রিয়",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "ডিসপ্লে রিফ্রেশ রেট: ${activeRefreshRateHz}Hz • হার্ডওয়্যার অ্যাক্সিলারেটেড স্ক্রলিং ও জিরো-ল্যাগ রেন্ডারিং",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // ২. থিম ও প্রদর্শনী সেটিংস
        item(key = "settings_theme_card", contentType = "card") {
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

        // ৩. অফলাইন ডাটাবেস তথ্য ও সিঙ্ক (কোনো আপলোড বা JSON অপশন ছাড়া)
        item(key = "settings_db_card", contentType = "card") {
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
                            text = "অফলাইন ডাটাবেস স্ট্যাটাস",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Text(
                        text = "ডাটাবেস সংস্করণ: v2.0.0 • মোট সংরক্ষিত যাচাইকৃত সেবা: ${BengaliSearchAndFormatUtils.toBengaliNumber(uiState.allServices.size)}টি",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "জামালপুর সদরের সকল সরকারি ও বেসরকারি কলেজ, সমস্ত সরকারি দপ্তর এবং ৭টি উপজেলার জরুরি সেবার পূর্ণাঙ্গ তথ্য অ্যাপের ভেতরে অফলাইনে সংরক্ষিত রয়েছে।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = onSyncDefaultData,
                        modifier = Modifier.testTag("sync_default_data_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("ডিফল্ট ডাটাবেস রিসেট / রিফ্রেশ করুন")
                    }
                }
            }
        }

        // ৪. সংরক্ষিত তথ্য সংশোধন রিপোর্টসমূহ (Issue Reports)
        if (uiState.issueReports.isNotEmpty()) {
            item(key = "settings_reports_header", contentType = "header") {
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
                key = { "rep_${it.id}" },
                contentType = { "report_item" }
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

        // ৫. অ্যাপ পরিচিতি, ডেভেলপার তথ্য, দায়মুক্তি ও গোপনীয়তা নীতি (About & Developer Info)
        item(key = "settings_about_card", contentType = "card") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("about_section_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Developer Highlight Box requested by User
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("developer_credit_box"),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "This app make by Elias Hossain",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Text(
                                text = "Phone Number +8801889515106",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(
                                onClick = { onCallDeveloper("+8801889515106") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("call_developer_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("ডেভেলপারকে কল করুন (+8801889515106)")
                            }
                        }
                    }

                    Text(
                        text = "সংস্করণ (Version): 2.0.0 (120Hz Ultra-Smooth Release)\nপ্যাকেজ আইডি: com.jamalpur.sheba\nআওতাধীন এলাকা: জামালপুর জেলার ৭টি উপজেলা (জামালপুর সদর, মেলান্দহ, ইসলামপুর, দেওয়ানগঞ্জ, সরিষাবাড়ী, মাদারগঞ্জ ও বকশীগঞ্জ)।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "উদ্দেশ্য: জামালপুর জেলার সাধারণ মানুষের জন্য জরুরি মুহূর্তে জাতীয় হটলাইন (৯৯৯, ১০২, ১৬২৬৩), সরকারি হাসপাতাল, উপজেলা স্বাস্থ্য কমপ্লেক্স, ৭টি থানার ওসি নম্বর, ফায়ার সার্ভিস স্টেশন, পল্লী বিদ্যুৎ, জামালপুর সদরের সকল কলেজ এবং সরকারি সংস্থার যাচাইকৃত তথ্য ইন্টারনেট ছাড়াই সহজলভ্য করা।",
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
                        text = "এই অ্যাপের তথ্যসমূহ বাংলাদেশ জাতীয় তথ্য বাতায়ন (jamalpur.gov.bd), স্বাস্থ্য অধিদপ্তর (DGHS), শিক্ষা বোর্ড এবং ফায়ার সার্ভিসের প্রকাশিত ডিরেক্টরি থেকে সংগ্রহ ও যাচাই করা হয়েছে। এটি কোনো সরকারি সংস্থার দাপ্তরিক অ্যাপ নয়। চরম জরুরি অবস্থায় সর্বদা জাতীয় হটলাইন ৯৯৯ বা ১০২ ব্যবহার করুন।",
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
