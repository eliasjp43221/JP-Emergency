package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.models.IssueReportEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface IssueReportDao {
    @Query("SELECT * FROM issue_reports ORDER BY createdAtTimestamp DESC")
    fun getAllReports(): Flow<List<IssueReportEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: IssueReportEntry): Long

    @Query("DELETE FROM issue_reports WHERE id = :id")
    suspend fun deleteReportById(id: Long)
}
