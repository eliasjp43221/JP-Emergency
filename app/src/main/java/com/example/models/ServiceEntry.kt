package com.example.models

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.utils.BengaliSearchAndFormatUtils
import java.util.Locale

/**
 * জামালপুর সেবা ডিরেক্টরির প্রতিটি প্রতিষ্ঠান বা জরুরি সেবার ডাটাবেস এনটিটি।
 * স্ক্রলিং ও সার্চ পারফরম্যান্স বৃদ্ধির জন্য @Immutable এবং প্রি-কমপিউটেড প্রপার্টি যুক্ত।
 */
@Immutable
@Entity(
    tableName = "service_entries",
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["upazilaId"]),
        Index(value = ["isFavorite"])
    ]
)
data class ServiceEntry(
    @PrimaryKey
    val id: String,
    val nameBn: String,
    val officialNameEn: String,
    val categoryId: String,
    val upazilaId: String,
    val addressBn: String,
    val primaryPhone: String,
    val secondaryPhone: String = "",
    val email: String = "",
    val openingHoursBn: String = "",
    val isEmergency24hVerified: Boolean = false,
    val availableServicesBn: String,
    val verificationStatus: String,
    val lastVerifiedDateBn: String,
    val sourceNameBn: String,
    val sourceUrl: String = "",
    val isNationalEmergencyHotline: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val searchKeywords: String = "",
    val isFavorite: Boolean = false,
    val lastViewedTimestamp: Long? = null
) {
    @Ignore
    val category: ServiceCategory = ServiceCategory.fromId(categoryId)

    @Ignore
    val upazila: Upazila = Upazila.fromId(upazilaId)

    @Ignore
    val status: VerificationStatus = VerificationStatus.fromId(verificationStatus)

    @Ignore
    val normalizedPrimaryPhone: String = BengaliSearchAndFormatUtils.normalizeDialNumber(primaryPhone)

    @Ignore
    val normalizedSecondaryPhone: String = BengaliSearchAndFormatUtils.normalizeDialNumber(secondaryPhone)

    @Ignore
    val isPrimaryPhoneValid: Boolean = BengaliSearchAndFormatUtils.isValidPhoneNumber(primaryPhone)

    @Ignore
    val isSecondaryPhoneValid: Boolean = BengaliSearchAndFormatUtils.isValidPhoneNumber(secondaryPhone)

    @Ignore
    val searchableBlob: String = buildString {
        append(nameBn).append(' ')
        append(officialNameEn).append(' ')
        append(category.titleBn).append(' ')
        append(category.titleEn).append(' ')
        append(upazila.nameBn).append(' ')
        append(upazila.nameEn).append(' ')
        append(addressBn).append(' ')
        append(availableServicesBn).append(' ')
        append(searchKeywords).append(' ')
        append(primaryPhone).append(' ')
        append(secondaryPhone)
    }.lowercase(Locale.ROOT)

    @Ignore
    val searchableBlobAscii: String = BengaliSearchAndFormatUtils.toAsciiDigits(searchableBlob)
}
