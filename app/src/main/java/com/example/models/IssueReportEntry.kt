package com.example.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * সেবা তথ্যে ভুল বা পরিবর্তনের রিপোর্ট স্থানীয়ভাবে সংরক্ষণের মডেল।
 * ব্যবহারকারী চাইলে এই রিপোর্টটি শেয়ার বা ইমেইলের মাধ্যমে পাঠাতে পারবেন।
 */
@Entity(tableName = "issue_reports")
data class IssueReportEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val serviceId: String,
    val serviceNameBn: String,
    val issueTypeBn: String,
    val detailsBn: String,
    val suggestedCorrectionBn: String = "",
    val createdAtTimestamp: Long = System.currentTimeMillis()
)
