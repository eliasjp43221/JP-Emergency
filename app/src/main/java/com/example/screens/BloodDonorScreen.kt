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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.components.UpazilaFilterRow
import com.example.models.BloodDonorEntry
import com.example.models.Upazila
import com.example.utils.BengaliSearchAndFormatUtils
import com.example.viewmodels.JamalpurUiState

private val bloodGroups = listOf("সকল", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodDonorScreen(
    uiState: JamalpurUiState,
    onBackClick: () -> Unit,
    onSelectBloodGroup: (String) -> Unit,
    onSelectUpazila: (Upazila) -> Unit,
    onToggleSampleMode: (Boolean) -> Unit,
    onAddVoluntaryDonor: (
        displayNameBn: String,
        bloodGroup: String,
        upazila: Upazila,
        contactNumber: String,
        availabilityNoteBn: String,
        isAvailabilityConfirmed: Boolean
    ) -> Unit,
    onRemoveDonor: (Long) -> Unit,
    onCallNumber: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDonorDialog by remember { mutableStateOf(false) }

    if (showAddDonorDialog) {
        AddVoluntaryDonorDialog(
            onDismiss = { showAddDonorDialog = false },
            onSave = { name, group, upazila, phone, note, available ->
                showAddDonorDialog = false
                onAddVoluntaryDonor(name, group, upazila, phone, note, available)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "রক্তদাতা ও ব্লাড ব্যাংক ডিরেক্টরি",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("blood_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ফিরে যান"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDonorDialog = true },
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
                modifier = Modifier.testTag("add_voluntary_donor_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("রক্তদাতা যুক্ত করুন", style = MaterialTheme.typography.labelLarge)
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("blood_donor_list"),
            contentPadding = PaddingValues(bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Privacy & Sample Mode Notice Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PrivacyTip,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "গোপনীয়তা ও যাচাইকৃত রক্তদাতা নীতিমালা",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "অন্যের ব্যক্তিগত ফোন নম্বর অনুমতি ছাড়া সংরক্ষণ বা প্রকাশ করা হয় না। এখানে সরকারি ব্লাড ব্যাংক এবং শুধুমাত্র আপনার ফোনে স্বেচ্ছায় যুক্ত করা রক্তদাতাদের তালিকা দেখা যাবে। যেকোনো মুহূর্তে তালিকা থেকে তথ্য মুছে ফেলা যায়।",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "নমুনা তথ্য মোড (Sample Data Mode) দেখান",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Switch(
                                checked = uiState.preferences.showSampleDonors,
                                onCheckedChange = onToggleSampleMode,
                                modifier = Modifier.testTag("sample_donor_mode_switch")
                            )
                        }
                    }
                }
            }

            // Blood Group Filter Chips
            item {
                Column {
                    Text(
                        text = "রক্তের গ্রুপ নির্বাচন করুন",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(bloodGroups) { group ->
                            val isSelected = uiState.selectedBloodGroup == group
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSelectBloodGroup(group) },
                                label = {
                                    Text(
                                        text = group,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.error,
                                    selectedLabelColor = MaterialTheme.colorScheme.onError
                                ),
                                shape = CircleShape,
                                modifier = Modifier.testTag("blood_group_chip_$group")
                            )
                        }
                    }
                }
            }

            // Upazila Filter Row
            item {
                UpazilaFilterRow(
                    selectedUpazila = uiState.selectedUpazila,
                    onSelectUpazila = onSelectUpazila
                )
            }

            // Donor & Blood Bank Items
            if (uiState.bloodDonors.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bloodtype,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(42.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "এই ফিল্টারে কোনো রক্তদাতা পাওয়া যায়নি",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "স্বেচ্ছায় রক্তদাতা হিসেবে নাম যুক্ত করতে নিচের 'রক্তদাতা যুক্ত করুন' বাটনে ট্যাপ করুন।",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(
                    items = uiState.bloodDonors,
                    key = { it.id }
                ) { donor ->
                    BloodDonorCard(
                        donor = donor,
                        onCall = { onCallNumber(donor.contactNumber) },
                        onRemove = { onRemoveDonor(donor.id) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BloodDonorCard(
    donor: BloodDonorEntry,
    onCall: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPhoneValid = BengaliSearchAndFormatUtils.isValidPhoneNumber(donor.contactNumber)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("donor_card_${donor.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (donor.isSampleEntry) MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.error.copy(alpha = 0.25f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.size(48.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (donor.bloodGroup == "সকল গ্রুপ") "ব্যাংক" else donor.bloodGroup,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = donor.displayNameBn,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${donor.upazila.nameBn} • সর্বশেষ যাচাই: ${donor.lastVerifiedDateBn}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!donor.isInstitutionalBloodBank) {
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.testTag("remove_donor_${donor.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "তথ্য মুছে ফেলুন",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (donor.isInstitutionalBloodBank) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "প্রাতিষ্ঠানিক ব্লাড ব্যাংক",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                } else if (donor.isSampleEntry) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "নমুনা তথ্য মোড (Sample Entry)",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Surface(
                        color = if (donor.isAvailabilityConfirmed) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (donor.isAvailabilityConfirmed) "স্বেচ্ছায় প্রাপ্যতা নিশ্চিতকৃত" else "কল করে প্রাপ্যতা জেনে নিন",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = donor.availabilityNoteBn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onCall,
                enabled = isPhoneValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("যোগাযোগ করুন (${donor.contactNumber})")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddVoluntaryDonorDialog(
    onDismiss: () -> Unit,
    onSave: (
        displayNameBn: String,
        bloodGroup: String,
        upazila: Upazila,
        contactNumber: String,
        availabilityNoteBn: String,
        isAvailabilityConfirmed: Boolean
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf("O+") }
    var selectedUpazila by remember { mutableStateOf(Upazila.JAMALPUR_SADAR) }
    var phone by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isAvailableNow by remember { mutableStateOf(true) }
    var consentChecked by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val selectableGroups = bloodGroups.filter { it != "সকল" }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "স্বেচ্ছাসেবী রক্তদাতা হিসেবে নাম যুক্ত করুন",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMsg = null
                    },
                    label = { Text("রক্তদাতার নাম বা প্রদর্শিত নাম *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("donor_name_input"),
                    singleLine = true
                )

                Text("রক্তের গ্রুপ নির্বাচন করুন:", style = MaterialTheme.typography.labelMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (grp in selectableGroups) {
                        FilterChip(
                            selected = selectedGroup == grp,
                            onClick = { selectedGroup = grp },
                            label = { Text(grp) }
                        )
                    }
                }

                Text("উপজেলা নির্বাচন করুন:", style = MaterialTheme.typography.labelMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (upz in Upazila.sevenUpazilas) {
                        FilterChip(
                            selected = selectedUpazila == upz,
                            onClick = { selectedUpazila = upz },
                            label = { Text(upz.nameBn, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        errorMsg = null
                    },
                    label = { Text("যোগাযোগের মোবাইল নম্বর *") },
                    placeholder = { Text("017XXXXXXXX") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("donor_phone_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("মন্তব্য বা শেষ রক্তদানের তথ্য (ঐচ্ছিক)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isAvailableNow,
                        onCheckedChange = { isAvailableNow = it }
                    )
                    Text(
                        text = "বর্তমানে রক্তদানের জন্য শারীরিকভাবে প্রস্তুত",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = consentChecked,
                        onCheckedChange = {
                            consentChecked = it
                            errorMsg = null
                        },
                        modifier = Modifier.testTag("donor_consent_checkbox")
                    )
                    Text(
                        text = "আমি স্বেচ্ছায় ও নিজ সম্মতিতে এই তথ্য যুক্ত করছি। আমি জানি যে যেকোনো সময় ডিলিট বাটনে ট্যাপ করে এই তথ্য মুছে ফেলা যাবে।",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (errorMsg != null) {
                    Text(
                        text = errorMsg!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        name.isBlank() -> errorMsg = "অনুগ্রহ করে নাম লিখুন।"
                        !BengaliSearchAndFormatUtils.isValidPhoneNumber(phone) ->
                            errorMsg = "অনুগ্রহ করে একটি বৈধ ফোন নম্বর লিখুন।"
                        !consentChecked ->
                            errorMsg = "তথ্য সংরক্ষণের জন্য সম্মতির ঘরে টিক দিন।"
                        else -> onSave(name, selectedGroup, selectedUpazila, phone, note, isAvailableNow)
                    }
                },
                modifier = Modifier.testTag("save_donor_button")
            ) {
                Text("সংরক্ষণ করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}
