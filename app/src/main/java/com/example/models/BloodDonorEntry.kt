package com.example.models

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * স্বেচ্ছাসেবী রক্তদাতা ও ব্লাড ব্যাংক এন্ট্রি।
 */
@Immutable
@Entity(tableName = "blood_donors")
data class BloodDonorEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val displayNameBn: String,
    val bloodGroup: String,
    val upazilaId: String,
    val contactNumber: String,
    val lastVerifiedDateBn: String,
    val availabilityNoteBn: String,
    val isAvailabilityConfirmed: Boolean = false,
    val isInstitutionalBloodBank: Boolean = false,
    val isSampleEntry: Boolean = false,
    val consentConfirmed: Boolean = true,
    val createdAtTimestamp: Long = System.currentTimeMillis()
) {
    @Ignore
    val upazila: Upazila = Upazila.fromId(upazilaId)
}
