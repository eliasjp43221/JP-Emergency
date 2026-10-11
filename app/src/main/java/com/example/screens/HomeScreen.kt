package com.example.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.FireTruck
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.components.CategoryIconHelper
import com.example.components.ServiceCardComponent
import com.example.components.UpazilaFilterRow
import com.example.models.ServiceCategory
import com.example.models.ServiceEntry
import com.example.models.Upazila
import com.example.utils.BengaliSearchAndFormatUtils
import com.example.viewmodels.JamalpurUiState

private val quickSearchSuggestions = listOf(
    "জামালপুর সদর হাসপাতাল",
    "অ্যাম্বুলেন্স",
    "মেলান্দহ থানা",
    "ফায়ার সার্ভিস",
    "রক্তদাতা",
    "বিদ্যুৎ অফিস"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    uiState: JamalpurUiState,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onSelectUpazila: (Upazila) -> Unit,
    onCategoryClick: (ServiceCategory) -> Unit,
    onServiceClick: (ServiceEntry) -> Unit,
    onCallNumber: (String) -> Unit,
    onToggleFavorite: (ServiceEntry) -> Unit,
    onOpenBloodDonors: () -> Unit,
    onOpenEmergencyHelp: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSearchingOrFiltering = uiState.searchQuery.isNotBlank() ||
        uiState.selectedUpazila != Upazila.ALL

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ==================== A. HEADER & HERO BANNER ====================
        item {
            HomeHeaderSection(
                totalServicesCount = uiState.allServices.size,
                onOpenEmergencyHelp = onOpenEmergencyHelp,
                onOpenSettings = onOpenSettings
            )
        }

        // ==================== B. PROMINENT SEARCH BAR ====================
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_input"),
                    placeholder = {
                        Text(
                            text = "হাসপাতাল, অ্যাম্বুলেন্স, থানা বা সেবা খুঁজুন...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "খুঁজুন",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = onClearSearch,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("clear_search_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "সার্চ মুছুন"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quickSearchSuggestions) { suggestion ->
                        AssistChip(
                            onClick = { onSearchQueryChange(suggestion) },
                            label = {
                                Text(
                                    text = suggestion,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            shape = CircleShape,
                            modifier = Modifier.testTag("suggestion_chip_$suggestion")
                        )
                    }
                }
            }
        }

        // ==================== UPAZILA SELECTION ROW ====================
        item {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "উপজেলা ও এলাকা নির্বাচন করুন",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (uiState.selectedUpazila != Upazila.ALL) {
                        TextButton(onClick = { onSelectUpazila(Upazila.ALL) }) {
                            Text("সব দেখুন", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
                UpazilaFilterRow(
                    selectedUpazila = uiState.selectedUpazila,
                    onSelectUpazila = onSelectUpazila,
                    upazilaCounts = uiState.upazilaCounts
                )
            }
        }

        // ==================== LIVE SEARCH / FILTER RESULTS ====================
        if (isSearchingOrFiltering) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "অনুসন্ধানের ফলাফল (${BengaliSearchAndFormatUtils.toBengaliNumber(uiState.filteredServices.size)}টি)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(
                        onClick = {
                            onClearSearch()
                            onSelectUpazila(Upazila.ALL)
                        },
                        modifier = Modifier.testTag("reset_home_search_button")
                    ) {
                        Text("ফিল্টার মুছুন")
                    }
                }
            }

            if (uiState.filteredServices.isEmpty()) {
                item {
                    EmptySearchResultCard(
                        query = uiState.searchQuery,
                        upazila = uiState.selectedUpazila,
                        onReset = {
                            onClearSearch()
                            onSelectUpazila(Upazila.ALL)
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .testTag("empty_search_state")
                    )
                }
            } else {
                items(
                    items = uiState.filteredServices,
                    key = { "search_${it.id}" }
                ) { service ->
                    ServiceCardComponent(
                        entry = service,
                        onCardClick = { onServiceClick(service) },
                        onCallClick = onCallNumber,
                        onFavoriteToggle = { onToggleFavorite(service) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        } else {
            // ==================== C. EMERGENCY QUICK ACTIONS ====================
            item {
                EmergencyQuickActionsSection(
                    onCall999 = { onCallNumber("999") },
                    onCall102 = { onCallNumber("102") },
                    onEmergencyHealth = { onCategoryClick(ServiceCategory.HOSPITAL_HEALTH) },
                    onAmbulance = { onCategoryClick(ServiceCategory.AMBULANCE) },
                    onPolice = { onCategoryClick(ServiceCategory.POLICE_LAW) },
                    onBloodDonor = onOpenBloodDonors
                )
            }

            // ==================== D. SERVICE CATEGORIES GRID ====================
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "সেবার বিভাগসমূহ",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "প্রয়োজনীয় বিভাগ নির্বাচন করে যাচাইকৃত নম্বর ও ঠিকানা দেখুন",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val categories = ServiceCategory.displayCategories
                    val rows = categories.chunked(2)
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        for (rowItems in rows) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                for (cat in rowItems) {
                                    val count = uiState.categoryCounts[cat] ?: 0
                                    CategoryGridCard(
                                        category = cat,
                                        count = count,
                                        onClick = {
                                            if (cat == ServiceCategory.BLOOD_DONOR) {
                                                onOpenBloodDonors()
                                            } else {
                                                onCategoryClick(cat)
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (rowItems.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // ==================== RECENTLY VIEWED SERVICES ====================
            if (uiState.recentlyViewedServices.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "সম্প্রতি দেখা সেবাসমূহ",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                items(
                    items = uiState.recentlyViewedServices.take(3),
                    key = { "recent_${it.id}" }
                ) { service ->
                    ServiceCardComponent(
                        entry = service,
                        onCardClick = { onServiceClick(service) },
                        onCallClick = onCallNumber,
                        onFavoriteToggle = { onToggleFavorite(service) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeaderSection(
    totalServicesCount: Int,
    onOpenEmergencyHelp: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.88f)
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "জামালপুর সেবা",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "আপনার প্রয়োজনীয় সেবা, এক জায়গায়",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.92f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onOpenEmergencyHelp,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("header_emergency_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Emergency,
                        contentDescription = "জরুরি সহায়তা",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "জরুরি হেল্প",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f))
                        .testTag("header_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "সেটিংস",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Hero Banner Card with generated illustration and offline badge
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(118.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_jamalpur_banner_1791687349108),
                        contentDescription = "জামালপুর জেলার জরুরি ও জনসেবা চিত্র",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.90f),
                        shape = RoundedCornerShape(bottomEnd = 12.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "৭টি উপজেলা • ${BengaliSearchAndFormatUtils.toBengaliNumber(totalServicesCount)}টি যাচাইকৃত সেবা",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = "অফলাইন সক্রিয়",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "অফলাইন ডিরেক্টরি সক্রিয় — ইন্টারনেট ছাড়াই সকল সংরক্ষিত নম্বর ও ঠিকানা ব্যবহারযোগ্য",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun EmergencyQuickActionsSection(
    onCall999: () -> Unit,
    onCall102: () -> Unit,
    onEmergencyHealth: () -> Unit,
    onAmbulance: () -> Unit,
    onPolice: () -> Unit,
    onBloodDonor: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "জরুরি কুইক অ্যাকশন",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "যাচাইকৃত হটলাইন",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Top 2 National Verified Emergency Hotlines (999 and 102)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .testTag("quick_call_999")
                    .clickable(onClick = onCall999),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "৯৯৯ এ কল করুন",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "জাতীয় জরুরি সেবা",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "৯৯৯ এ কল করুন",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Text(
                        text = "পুলিশ • ফায়ার • অ্যাম্বুলেন্স",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .testTag("quick_call_102")
                    .clickable(onClick = onCall102),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFD84315)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FireTruck,
                            contentDescription = "১০২ এ কল করুন",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ফায়ার সার্ভিস",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "১০২ এ কল করুন",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Text(
                        text = "অগ্নিকাণ্ড ও উদ্ধার হটলাইন",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Secondary 4 Emergency Quick Shortcuts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EmergencyShortcutButton(
                titleBn = "জরুরি স্বাস্থ্যসেবা",
                icon = Icons.Default.LocalHospital,
                accentColor = Color(0xFF00796B),
                testTag = "quick_action_health",
                onClick = onEmergencyHealth,
                modifier = Modifier.weight(1f)
            )
            EmergencyShortcutButton(
                titleBn = "অ্যাম্বুলেন্স",
                icon = Icons.Default.MedicalServices,
                accentColor = Color(0xFFC62828),
                testTag = "quick_action_ambulance",
                onClick = onAmbulance,
                modifier = Modifier.weight(1f)
            )
            EmergencyShortcutButton(
                titleBn = "পুলিশ",
                icon = Icons.Default.LocalPolice,
                accentColor = Color(0xFF1565C0),
                testTag = "quick_action_police",
                onClick = onPolice,
                modifier = Modifier.weight(1f)
            )
            EmergencyShortcutButton(
                titleBn = "রক্তদাতা",
                icon = Icons.Default.Bloodtype,
                accentColor = Color(0xFFB71C1C),
                testTag = "quick_action_blood",
                onClick = onBloodDonor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun EmergencyShortcutButton(
    titleBn: String,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = titleBn,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = titleBn,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CategoryGridCard(
    category: ServiceCategory,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = CategoryIconHelper.getAccentColor(category)
    Card(
        modifier = modifier
            .testTag("category_card_${category.id}")
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = CategoryIconHelper.getIcon(category),
                    contentDescription = category.titleBn,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.titleBn,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                val countText = if (category == ServiceCategory.BLOOD_DONOR) {
                    "ব্লাড ব্যাংক ও দাতা"
                } else if (count > 0) {
                    "${BengaliSearchAndFormatUtils.toBengaliNumber(count)}টি যাচাইকৃত তথ্য"
                } else {
                    "তথ্য সংযোজন প্রক্রিয়াধীন"
                }
                Text(
                    text = countText,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (count > 0 || category == ServiceCategory.BLOOD_DONOR) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

@Composable
fun EmptySearchResultCard(
    query: String,
    upazila: Upazila,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = "ফলাফল পাওয়া যায়নি",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "কোনো মিল খুঁজে পাওয়া যায়নি",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            val detailMsg = buildString {
                if (query.isNotBlank()) {
                    append("'$query' শব্দের সাথে ")
                }
                if (upazila != Upazila.ALL) {
                    append("${upazila.nameBn} এলাকায় ")
                }
                append("কোনো যাচাইকৃত সেবা পাওয়া যায়নি। বানান পরীক্ষা করুন অথবা সকল এলাকার ফিল্টার নির্বাচন করে আবার চেষ্টা করুন।")
            }
            Text(
                text = detailMsg,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onReset,
                modifier = Modifier.testTag("empty_state_reset_button")
            ) {
                Text("সকল সেবা দেখুন")
            }
        }
    }
}
