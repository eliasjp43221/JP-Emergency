package com.example.models

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Immutable
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
