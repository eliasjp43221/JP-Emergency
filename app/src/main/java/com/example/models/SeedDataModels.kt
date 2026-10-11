package com.example.models

import com.squareup.moshi.JsonClass

/**
 * ভার্সনযুক্ত JSON ডাটা ইমপোর্ট এবং আপডেট সিস্টেমের মডেল।
 */
@JsonClass(generateAdapter = true)
data class SeedDataPackage(
    val schemaVersion: Int,
    val datasetVersion: String,
    val districtNameBn: String,
    val lastUpdatedBn: String,
    val services: List<SeedServiceItem>
)

@JsonClass(generateAdapter = true)
data class SeedServiceItem(
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
    val searchKeywords: String = ""
) {
    fun toServiceEntry(
        isFavorite: Boolean = false,
        lastViewedTimestamp: Long? = null
    ): ServiceEntry {
        return ServiceEntry(
            id = id.trim(),
            nameBn = nameBn.trim(),
            officialNameEn = officialNameEn.trim(),
            categoryId = categoryId.trim(),
            upazilaId = upazilaId.trim(),
            addressBn = addressBn.trim(),
            primaryPhone = primaryPhone.trim(),
            secondaryPhone = secondaryPhone.trim(),
            email = email.trim(),
            openingHoursBn = openingHoursBn.trim(),
            isEmergency24hVerified = isEmergency24hVerified,
            availableServicesBn = availableServicesBn.trim(),
            verificationStatus = verificationStatus.trim(),
            lastVerifiedDateBn = lastVerifiedDateBn.trim(),
            sourceNameBn = sourceNameBn.trim(),
            sourceUrl = sourceUrl.trim(),
            isNationalEmergencyHotline = isNationalEmergencyHotline,
            latitude = latitude,
            longitude = longitude,
            searchKeywords = searchKeywords.trim(),
            isFavorite = isFavorite,
            lastViewedTimestamp = lastViewedTimestamp
        )
    }
}

data class DataImportResult(
    val isSuccess: Boolean,
    val importedCount: Int,
    val updatedCount: Int,
    val duplicateSkippedCount: Int,
    val invalidSkippedCount: Int,
    val messageBn: String,
    val errorsBn: List<String> = emptyList()
)
