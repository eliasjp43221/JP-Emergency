package com.example.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.models.ServiceEntry
import com.example.models.Upazila
import com.example.models.VerificationStatus
import com.example.ui.theme.IncompleteSlate
import com.example.ui.theme.IncompleteSlateContainer
import com.example.ui.theme.ReverifyAmber
import com.example.ui.theme.ReverifyAmberContainer
import com.example.ui.theme.VerifiedGreen
import com.example.ui.theme.VerifiedGreenContainer
import com.example.utils.BengaliSearchAndFormatUtils

private val CardShape16 = RoundedCornerShape(16.dp)
private val IconBoxShape12 = RoundedCornerShape(12.dp)
private val BadgeShape8 = RoundedCornerShape(8.dp)
private val ButtonShape12 = RoundedCornerShape(12.dp)

@Composable
fun ServiceCardComponent(
    entry: ServiceEntry,
    onCardClick: () -> Unit,
    onCallClick: (String) -> Unit,
    onFavoriteToggle: () -> Unit,
    distanceKm: Double? = null,
    modifier: Modifier = Modifier
) {
    val accentColor = remember(entry.categoryId) { CategoryIconHelper.getAccentColor(entry.category) }
    val categoryIcon = remember(entry.categoryId) { CategoryIconHelper.getIcon(entry.category) }
    val isPhoneValid = entry.isPrimaryPhoneValid

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("service_card_${entry.id}")
            .clickable(onClick = onCardClick),
        shape = CardShape16,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = if (entry.isNationalEmergencyHotline) {
            BorderStroke(1.5.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.45f))
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(IconBoxShape12)
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = categoryIcon,
                        contentDescription = entry.category.titleBn,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.nameBn,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (entry.officialNameEn.isNotBlank()) {
                        Text(
                            text = entry.officialNameEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("favorite_toggle_${entry.id}")
                ) {
                    Icon(
                        imageVector = if (entry.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (entry.isFavorite) "প্রিয় থেকে সরান" else "প্রিয়তে যুক্ত করুন",
                        tint = if (entry.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fast single-pass Row instead of multi-pass FlowRow for 120Hz smooth scrolling
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                VerificationStatusBadge(status = entry.status)

                Surface(
                    shape = BadgeShape8,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = entry.upazila.nameBn,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                if (entry.isEmergency24hVerified) {
                    Surface(
                        shape = BadgeShape8,
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = "২৪ ঘণ্টা জরুরি",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Surface(
                        shape = BadgeShape8,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = entry.category.titleBn,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (distanceKm != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = BadgeShape8,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NearMe,
                            contentDescription = "দূরত্ব",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = BengaliSearchAndFormatUtils.formatDistanceBn(distanceKm),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "ঠিকানা",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = entry.addressBn,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onCallClick(entry.primaryPhone) },
                    enabled = isPhoneValid,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("call_button_${entry.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (entry.isNationalEmergencyHotline) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    ),
                    shape = ButtonShape12
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "কল করুন",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPhoneValid) "কল: ${entry.primaryPhone}" else "নম্বর উপলব্ধ নয়",
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                OutlinedButton(
                    onClick = onCardClick,
                    modifier = Modifier
                        .height(46.dp)
                        .testTag("details_button_${entry.id}"),
                    shape = ButtonShape12
                ) {
                    Text(
                        text = "বিস্তারিত",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
fun VerificationStatusBadge(
    status: VerificationStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, contentColor, icon) = when (status) {
        VerificationStatus.VERIFIED -> Triple(
            VerifiedGreenContainer,
            VerifiedGreen,
            Icons.Default.CheckCircle
        )
        VerificationStatus.NEEDS_REVERIFICATION -> Triple(
            ReverifyAmberContainer,
            ReverifyAmber,
            Icons.Default.WarningAmber
        )
        VerificationStatus.INCOMPLETE -> Triple(
            IncompleteSlateContainer,
            IncompleteSlate,
            Icons.Default.Info
        )
    }

    Surface(
        shape = BadgeShape8,
        color = bgColor,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = status.labelBn,
                tint = contentColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status.labelBn,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
                maxLines = 1
            )
        }
    }
}

@Composable
fun UpazilaFilterRow(
    selectedUpazila: Upazila,
    onSelectUpazila: (Upazila) -> Unit,
    upazilaCounts: Map<Upazila, Int> = emptyMap(),
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .testTag("upazila_filter_row"),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = Upazila.entries,
            key = { it.id },
            contentType = { "upazila_chip" }
        ) { upazila ->
            val isSelected = selectedUpazila == upazila
            val count = upazilaCounts[upazila]
            val labelText = remember(upazila, count) {
                if (count != null && count > 0) {
                    "${upazila.nameBn} (${BengaliSearchAndFormatUtils.toBengaliNumber(count)})"
                } else {
                    upazila.nameBn
                }
            }

            FilterChip(
                selected = isSelected,
                onClick = { onSelectUpazila(upazila) },
                label = {
                    Text(
                        text = labelText,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = CircleShape,
                modifier = Modifier.testTag("upazila_chip_${upazila.id}")
            )
        }
    }
}
