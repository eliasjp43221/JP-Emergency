package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.models.BloodDonorEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface BloodDonorDao {
    @Query("SELECT * FROM blood_donors ORDER BY isInstitutionalBloodBank DESC, createdAtTimestamp DESC")
    fun getAllDonors(): Flow<List<BloodDonorEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonor(donor: BloodDonorEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonors(donors: List<BloodDonorEntry>)

    @Query("DELETE FROM blood_donors WHERE id = :id")
    suspend fun deleteDonorById(id: Long)

    @Query("SELECT COUNT(*) FROM blood_donors")
    suspend fun getDonorCount(): Int
}
