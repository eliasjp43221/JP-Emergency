package com.example.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.components.CategoryIconHelper
import com.example.components.LocationPermissionRationaleDialog
import com.example.components.ServiceCardComponent
import com.example.components.UpazilaFilterRow
import com.example.models.ServiceCategory
import com.example.models.ServiceEntry
import com.example.models.Upazila
import com.example.utils.BengaliSearchAndFormatUtils
import com.example.viewmodels.JamalpurUiState
import com.google.android.gms.location.LocationServices

@Composable
fun ServicesDirectoryScreen(
    uiState: JamalpurUiState,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onSelectCategory: (ServiceCategory) -> Unit,
    onSelectUpazila: (Upazila) -> Unit,
    onResetFilters: () -> Unit,
    onServiceClick: (ServiceEntry) -> Unit,
    onCallNumber: (String) -> Unit,
    onToggleFavorite: (ServiceEntry) -> Unit,
    onLocationReceived: (Double, Double) -> Unit,
    onDisableNearbySort: () -> Unit,
    onLocationDeniedMessage: (String) -> Unit,
    onOpenBloodDonors: () -> Unit,
    onOpenDataUpdate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showLocationRationale by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            fetchCurrentOrFallbackLocation(
                context = context,
                fallbackUpazila = uiState.selectedUpazila,
                onLocation = onLocationReceived
            )
        } else {
            onLocationDeniedMessage("লোকেশন অনুমতি প্রদান করা হয়নি। আপনি উপজেলা ফিল্টার ব্যবহার করে সেবা খুঁজতে পারেন।")
        }
    }

    if (showLocationRationale) {
        LocationPermissionRationaleDialog(
            onConfirmRequest = {
                showLocationRationale = false
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            },
            onDismiss = { showLocationRationale = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("services_directory_list"),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "সকল সেবা ডিরেক্টরি",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "বিভাগ এবং ৭টি উপজেলার ভিত্তিতে ফিল্টার ও অনুসন্ধান করুন",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("directory_search_input"),
                    placeholder = {
                        Text("প্রতিষ্ঠানের নাম, নম্বর, থানা বা ঠিকানা লিখুন...")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "খুঁজুন"
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = onClearSearch,
                                modifier = Modifier.testTag("directory_clear_search")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "মুছুন"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
            }
        }

        // Category Filter Row
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.testTag("category_filter_row")
            ) {
                items(ServiceCategory.entries, key = { it.id }) { category ->
                    val isSelected = uiState.selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectCategory(category) },
                        label = {
                            Text(
                                text = category.titleBn,
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = CategoryIconHelper.getIcon(category),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = CircleShape,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("category_chip_${category.id}")
                    )
                }
            }
        }

        // Upazila Filter Row
        item {
            UpazilaFilterRow(
                selectedUpazila = uiState.selectedUpazila,
                onSelectUpazila = onSelectUpazila,
                upazilaCounts = uiState.upazilaCounts
            )
        }

        // Nearby Sort & Filter Status Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "মোট ${BengaliSearchAndFormatUtils.toBengaliNumber(uiState.filteredServices.size)}টি সেবা পাওয়া গেছে",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            if (uiState.isNearbySortActive) {
                                onDisableNearbySort()
                            } else {
                                val hasPerm = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED ||
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED
                                if (hasPerm) {
                                    fetchCurrentOrFallbackLocation(
                                        context = context,
                                        fallbackUpazila = uiState.selectedUpazila,
                                        onLocation = onLocationReceived
                                    )
                                } else {
                                    showLocationRationale = true
                                }
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("nearby_services_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "কাছাকাছি সেবা",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (uiState.isNearbySortActive) "কাছাকাছি সক্রিয়" else "কাছাকাছি সেবা",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    if (uiState.selectedCategory != ServiceCategory.ALL ||
                        uiState.selectedUpazila != Upazila.ALL ||
                        uiState.searchQuery.isNotBlank()
                    ) {
                        OutlinedButton(
                            onClick = onResetFilters,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("reset_all_filters_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterAltOff,
                                contentDescription = "সব ফিল্টার মুছুন",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("রিসেট", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        // Special banner when Blood Donor category is selected
        if (uiState.selectedCategory == ServiceCategory.BLOOD_DONOR) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bloodtype,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "স্বেচ্ছাসেবী রক্তদাতা ও ব্লাড ব্যাংক ডিরেক্টরি",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "রক্তের গ্রুপ অনুযায়ী খুঁজতে বা স্বেচ্ছায় রক্তদাতা হিসেবে নাম যুক্ত করতে বিশেষায়িত রক্তদাতা ডিরেক্টরি খুলুন।",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onOpenBloodDonors,
                            modifier = Modifier.testTag("open_blood_directory_button")
                        ) {
                            Text("রক্তদাতা ডিরেক্টরি খুলুন")
                        }
                    }
                }
            }
        }

        if (uiState.filteredServices.isEmpty()) {
            item {
                val categoryCount = uiState.categoryCounts[uiState.selectedCategory] ?: 0
                if (uiState.selectedCategory != ServiceCategory.ALL && categoryCount == 0 && uiState.searchQuery.isBlank()) {
                    // Honest explanation when a category has no verified entries yet (Verified Data Policy)
                    CategoryPendingVerificationCard(
                        category = uiState.selectedCategory,
                        onOpenDataUpdate = onOpenDataUpdate,
                        onResetCategory = { onSelectCategory(ServiceCategory.ALL) },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .testTag("category_pending_verification_card")
                    )
                } else {
                    EmptySearchResultCard(
                        query = uiState.searchQuery,
                        upazila = uiState.selectedUpazila,
                        onReset = onResetFilters,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .testTag("directory_empty_state")
                    )
                }
            }
        } else {
            items(
                items = uiState.filteredServices,
                key = { it.id }
            ) { entry ->
                val distKm = if (uiState.isNearbySortActive && uiState.userLat != null && uiState.userLng != null) {
                    val lat = entry.latitude ?: entry.upazila.centerLat
                    val lng = entry.longitude ?: entry.upazila.centerLng
                    BengaliSearchAndFormatUtils.calculateDistanceKm(
                        uiState.userLat,
                        uiState.userLng,
                        lat,
                        lng
                    )
                } else {
                    null
                }

                ServiceCardComponent(
                    entry = entry,
                    onCardClick = { onServiceClick(entry) },
                    onCallClick = onCallNumber,
                    onFavoriteToggle = { onToggleFavorite(entry) },
                    distanceKm = distKm,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryPendingVerificationCard(
    category: ServiceCategory,
    onOpenDataUpdate: () -> Unit,
    onResetCategory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${category.titleBn} — যাচাইকৃত তথ্য নীতিমালা",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "জামালপুর সেবা অ্যাপে কোনো কাল্পনিক প্রতিষ্ঠান বা যাচাইবিহীন ফোন নম্বর প্রদর্শন করা হয় না। এই বিভাগে সরকারি বা প্রাতিষ্ঠানিক সূত্র থেকে যাচাইকৃত নতুন নম্বরসমূহ JSON ডাটা আপডেট সিস্টেমের মাধ্যমে যুক্ত করা যাবে।",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = onOpenDataUpdate) {
                    Text("JSON ডাটা ইমপোর্ট করুন")
                }
                OutlinedButton(onClick = onResetCategory) {
                    Text("সকল সেবা দেখুন")
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun fetchCurrentOrFallbackLocation(
    context: Context,
    fallbackUpazila: Upazila,
    onLocation: (Double, Double) -> Unit
) {
    try {
        val client = LocationServices.getFusedLocationProviderClient(context)
        client.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onLocation(location.latitude, location.longitude)
                } else {
                    onLocation(fallbackUpazila.centerLat, fallbackUpazila.centerLng)
                }
            }
            .addOnFailureListener {
                onLocation(fallbackUpazila.centerLat, fallbackUpazila.centerLng)
            }
    } catch (_: Exception) {
        onLocation(fallbackUpazila.centerLat, fallbackUpazila.centerLng)
    }
}
