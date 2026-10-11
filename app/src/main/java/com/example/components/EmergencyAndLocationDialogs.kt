package com.example.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun EmergencyHelpDialog(
    onDismiss: () -> Unit,
    onDialNumber: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Emergency,
                contentDescription = "জরুরি সহায়তা",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "জরুরি সহায়তা ও হটলাইন",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "সতর্কতা: জীবন বিপন্নকারী জরুরি পরিস্থিতি, অগ্নিকাণ্ড, গুরুতর অসুস্থতা বা অপরাধ প্রতিরোধের ক্ষেত্রে নিচের যাচাইকৃত সরকারি জরুরি নম্বরে কল করুন। অপ্রয়োজনে বা কৌতূহলবশত জরুরি নম্বরে কল করা থেকে বিরত থাকুন।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                EmergencyQuickDialRow(
                    titleBn = "জাতীয় জরুরি সেবা — ৯৯৯",
                    subtitleBn = "পুলিশ, ফায়ার সার্ভিস ও অ্যাম্বুলেন্স (২৪ ঘণ্টা টোল-ফ্রি)",
                    phone = "999",
                    isNational = true,
                    onDial = { onDialNumber("999") }
                )

                EmergencyQuickDialRow(
                    titleBn = "ফায়ার সার্ভিস কেন্দ্রীয় হটলাইন — ১০২",
                    subtitleBn = "অগ্নিকাণ্ড ও জরুরি উদ্ধার সহায়তা",
                    phone = "102",
                    isNational = true,
                    onDial = { onDialNumber("102") }
                )

                EmergencyQuickDialRow(
                    titleBn = "স্বাস্থ্য বাতায়ন — ১৬২৬৩",
                    subtitleBn = "২৪ ঘণ্টা সরকারি ডাক্তার ও জরুরি স্বাস্থ্য পরামর্শ",
                    phone = "16263",
                    isNational = true,
                    onDial = { onDialNumber("16263") }
                )

                EmergencyQuickDialRow(
                    titleBn = "২৫০ শয্যা জেনারেল হাসপাতাল, জামালপুর",
                    subtitleBn = "জামালপুর সদর হাসপাতাল জরুরি বিভাগ",
                    phone = "01711-632276",
                    isNational = false,
                    onDial = { onDialNumber("01711-632276") }
                )

                EmergencyQuickDialRow(
                    titleBn = "জামালপুর ফায়ার সার্ভিস স্টেশন",
                    subtitleBn = "জেলা সদর ফায়ার স্টেশন ল্যান্ডলাইন",
                    phone = "02997773333",
                    isNational = false,
                    onDial = { onDialNumber("02997773333") }
                )

                EmergencyQuickDialRow(
                    titleBn = "জেলা পুলিশ কন্ট্রোল রুম, জামালপুর",
                    subtitleBn = "পুলিশ সুপার কার্যালয় নিয়ন্ত্রণ কক্ষ",
                    phone = "01320-106098",
                    isNational = false,
                    onDial = { onDialNumber("01320-106098") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("close_emergency_dialog_button")
            ) {
                Text("বন্ধ করুন", style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}

@Composable
private fun EmergencyQuickDialRow(
    titleBn: String,
    subtitleBn: String,
    phone: String,
    isNational: Boolean,
    onDial: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titleBn,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitleBn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isNational) "যাচাইকৃত জাতীয় হটলাইন • $phone" else "যাচাইকৃত প্রাতিষ্ঠানিক নম্বর • $phone",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isNational) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onDial,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isNational) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "কল করুন",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("কল", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun LocationPermissionRationaleDialog(
    onConfirmRequest: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "লোকেশন পারমিশন",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "কাছাকাছি সেবা খুঁজতে লোকেশন অনুমতি",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "আপনার বর্তমান অবস্থান থেকে জামালপুর জেলার কোন হাসপাতাল, থানা বা ফায়ার সার্ভিস স্টেশনটি সবচেয়ে কাছে তা দূরত্ব অনুযায়ী সাজিয়ে দেখানোর জন্য লোকেশন (Location) অনুমতি প্রয়োজন।",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "আপনার লোকেশন তথ্য শুধুমাত্র ফোনের ভেতরেই দূরত্ব হিসাব করতে ব্যবহৃত হয়; কোথাও পাঠানো বা সংরক্ষণ করা হয় না। অনুমতি না দিলেও অ্যাপের সকল সেবা স্বাভাবিকভাবে কাজ করবে।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmRequest,
                modifier = Modifier.testTag("confirm_location_permission_button")
            ) {
                Text("অনুমতি দিন")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_location_permission_button")
            ) {
                Text("এখন নয়")
            }
        }
    )
}
