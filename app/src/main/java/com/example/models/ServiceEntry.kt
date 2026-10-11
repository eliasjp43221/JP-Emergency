package com.example.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * জামালপুর সেবা ডিরেক্টরির প্রতিটি প্রতিষ্ঠান বা জরুরি সেবার ডাটাবেস এনটিটি।
 */
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
    val category: ServiceCategory
        get() = ServiceCategory.fromId(categoryId)

    val upazila: Upazila
        get() = Upazila.fromId(upazilaId)

    val status: VerificationStatus
        get() = VerificationStatus.fromId(verificationStatus)
}
