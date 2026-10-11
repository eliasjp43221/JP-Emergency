package com.example.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * স্বেচ্ছাসেবী রক্তদাতা ও ব্লাড ব্যাংক এন্ট্রি।
 * গোপনীয়তা নীতি অনুযায়ী শুধুমাত্র স্বেচ্ছায় প্রদত্ত তথ্য অথবা প্রাতিষ্ঠানিক ব্লাড ব্যাংক সংরক্ষিত হয়।
 */
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
    val upazila: Upazila
        get() = Upazila.fromId(upazilaId)
}
