package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.models.ServiceEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Query("SELECT * FROM service_entries ORDER BY isNationalEmergencyHotline DESC, nameBn ASC")
    fun getAllServices(): Flow<List<ServiceEntry>>

    @Query("SELECT * FROM service_entries ORDER BY isNationalEmergencyHotline DESC, nameBn ASC")
    suspend fun getAllServicesSnapshot(): List<ServiceEntry>

    @Query("SELECT * FROM service_entries WHERE id = :id LIMIT 1")
    fun getServiceByIdFlow(id: String): Flow<ServiceEntry?>

    @Query("SELECT * FROM service_entries WHERE id = :id LIMIT 1")
    suspend fun getServiceById(id: String): ServiceEntry?

    @Query("SELECT * FROM service_entries WHERE isFavorite = 1 ORDER BY nameBn ASC")
    fun getFavoriteServices(): Flow<List<ServiceEntry>>

    @Query("SELECT * FROM service_entries WHERE lastViewedTimestamp IS NOT NULL ORDER BY lastViewedTimestamp DESC LIMIT 15")
    fun getRecentlyViewedServices(): Flow<List<ServiceEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertServices(services: List<ServiceEntry>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertService(service: ServiceEntry)

    @Query("UPDATE service_entries SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)

    @Query("UPDATE service_entries SET lastViewedTimestamp = :timestamp WHERE id = :id")
    suspend fun updateLastViewed(id: String, timestamp: Long)

    @Query("UPDATE service_entries SET lastViewedTimestamp = NULL")
    suspend fun clearRecentHistory()

    @Query("SELECT COUNT(*) FROM service_entries")
    suspend fun getServiceCount(): Int
}
