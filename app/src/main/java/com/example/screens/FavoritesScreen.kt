package com.example.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.components.ServiceCardComponent
import com.example.models.ServiceEntry
import com.example.utils.BengaliSearchAndFormatUtils
import com.example.viewmodels.JamalpurUiState

@Composable
fun FavoritesScreen(
    uiState: JamalpurUiState,
    onServiceClick: (ServiceEntry) -> Unit,
    onCallNumber: (String) -> Unit,
    onToggleFavorite: (ServiceEntry) -> Unit,
    onClearRecentHistory: () -> Unit,
    onExploreServicesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("favorites_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "প্রিয় ও সংরক্ষিত সেবাসমূহ",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "দ্রুত যোগাযোগের জন্য আপনার প্রিয় সেবাগুলো অফলাইনে এখানে সংরক্ষিত থাকে (${BengaliSearchAndFormatUtils.toBengaliNumber(uiState.favoriteServices.size)}টি)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (uiState.favoriteServices.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("empty_favorites_card"),
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
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "এখনও কোনো প্রিয় সেবা যুক্ত করা হয়নি",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "যেকোনো হাসপাতাল, থানা বা জরুরি সেবার কার্ডের পাশে থাকা হার্ট (♥) আইকনে ট্যাপ করে প্রিয় তালিকায় সংরক্ষণ করুন।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onExploreServicesClick,
                            modifier = Modifier.testTag("explore_services_from_favorites_button")
                        ) {
                            Text("সেবাসমূহ ঘুরে দেখুন")
                        }
                    }
                }
            }
        } else {
            items(
                items = uiState.favoriteServices,
                key = { "fav_${it.id}" },
                contentType = { "service_card" }
            ) { entry ->
                ServiceCardComponent(
                    entry = entry,
                    onCardClick = { onServiceClick(entry) },
                    onCallClick = onCallNumber,
                    onFavoriteToggle = { onToggleFavorite(entry) }
                )
            }
        }

        if (uiState.recentlyViewedServices.isNotEmpty()) {
            item(key = "recent_history_header", contentType = "header") {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "সম্প্রতি দেখা সেবা",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    TextButton(
                        onClick = onClearRecentHistory,
                        modifier = Modifier.testTag("clear_recent_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ইতিহাস মুছুন")
                    }
                }
            }

            items(
                items = uiState.recentlyViewedServices,
                key = { "hist_${it.id}" },
                contentType = { "service_card" }
            ) { entry ->
                ServiceCardComponent(
                    entry = entry,
                    onCardClick = { onServiceClick(entry) },
                    onCallClick = onCallNumber,
                    onFavoriteToggle = { onToggleFavorite(entry) }
                )
            }
        }
    }
}
