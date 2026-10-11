package com.example.repositories

import android.content.Context
import com.example.data.DataValidator
import com.example.data.VerifiedJamalpurSeedData
import com.example.database.BloodDonorDao
import com.example.database.IssueReportDao
import com.example.database.ServiceDao
import com.example.models.BloodDonorEntry
import com.example.models.DataImportResult
import com.example.models.IssueReportEntry
import com.example.models.ServiceEntry
import kotlinx.coroutines.flow.Flow

/**
 * জামালপুর সেবা অ্যাপের কেন্দ্রীয় রিপোজিটরি (Repository Pattern)।
 * লোকাল Room ডাটাবেস, সিড ডাটা এবং JSON আপডেট ভ্যালিডেশন পরিচালনা করে।
 */
class JamalpurRepository(
    private val serviceDao: ServiceDao,
    private val bloodDonorDao: BloodDonorDao,
    private val issueReportDao: IssueReportDao
) {
    val allServices: Flow<List<ServiceEntry>> = serviceDao.getAllServices()
    val favoriteServices: Flow<List<ServiceEntry>> = serviceDao.getFavoriteServices()
    val recentlyViewedServices: Flow<List<ServiceEntry>> = serviceDao.getRecentlyViewedServices()
    val allBloodDonors: Flow<List<BloodDonorEntry>> = bloodDonorDao.getAllDonors()
    val allIssueReports: Flow<List<IssueReportEntry>> = issueReportDao.getAllReports()

    fun getServiceByIdFlow(id: String): Flow<ServiceEntry?> = serviceDao.getServiceByIdFlow(id)

    suspend fun getServiceById(id: String): ServiceEntry? = serviceDao.getServiceById(id)

    /**
     * অ্যাপ চালুর সময় লোকাল ডাটাবেস খালি থাকলে যাচাইকৃত প্রাথমিক তথ্য সংরক্ষণ করে।
     */
    suspend fun ensureSeedDataPopulated(context: Context? = null) {
        val currentCount = serviceDao.getServiceCount()
        if (currentCount == 0) {
            val seedServices = VerifiedJamalpurSeedData.loadSeedServices(context)
            serviceDao.upsertServices(seedServices)
        }
        val donorCount = bloodDonorDao.getDonorCount()
        if (donorCount == 0) {
            bloodDonorDao.insertDonors(VerifiedJamalpurSeedData.defaultInstitutionalBloodBanks())
        }
    }

    /**
     * ডিফল্ট যাচাইকৃত ডাটাবেস পুনরায় সিঙ্ক করে (প্রিয় তালিকা অক্ষুণ্ণ রেখে)।
     */
    suspend fun resetOrSyncDefaultSeedData(context: Context? = null): DataImportResult {
        val existing = serviceDao.getAllServicesSnapshot()
        val items = VerifiedJamalpurSeedData.defaultSeedItems()
        val (validEntries, result) = DataValidator.validateItems(items, existing)
        if (validEntries.isNotEmpty()) {
            serviceDao.upsertServices(validEntries)
        }
        return result
    }

    /**
     * ভার্সনযুক্ত JSON স্ট্রিং থেকে নতুন বা আপডেটকৃত সেবা তথ্য যাচাই ও ইমপোর্ট করে।
     */
    suspend fun importServicesFromJson(jsonString: String): DataImportResult {
        val existing = serviceDao.getAllServicesSnapshot()
        val (validEntries, result) = DataValidator.parseAndValidateJson(jsonString, existing)
        if (result.isSuccess && validEntries.isNotEmpty()) {
            serviceDao.upsertServices(validEntries)
        }
        return result
    }

    suspend fun exportCurrentServicesJson(): String {
        val existing = serviceDao.getAllServicesSnapshot()
        return DataValidator.exportToJson(existing)
    }

    suspend fun toggleFavorite(serviceId: String, currentFavorite: Boolean) {
        serviceDao.updateFavoriteStatus(serviceId, !currentFavorite)
    }

    suspend fun markServiceViewed(serviceId: String) {
        serviceDao.updateLastViewed(serviceId, System.currentTimeMillis())
    }

    suspend fun clearRecentHistory() {
        serviceDao.clearRecentHistory()
    }

    suspend fun addVoluntaryBloodDonor(
        displayNameBn: String,
        bloodGroup: String,
        upazilaId: String,
        contactNumber: String,
        availabilityNoteBn: String,
        isAvailabilityConfirmed: Boolean
    ): Long {
        val entry = BloodDonorEntry(
            displayNameBn = displayNameBn.trim(),
            bloodGroup = bloodGroup.trim(),
            upazilaId = upazilaId,
            contactNumber = contactNumber.trim(),
            lastVerifiedDateBn = "স্বেচ্ছায় সংযোজিত",
            availabilityNoteBn = availabilityNoteBn.trim().ifBlank {
                "জরুরি রক্তের প্রয়োজনে যোগাযোগের পূর্বে সময় ও প্রাপ্যতা নিশ্চিত করে নিন।"
            },
            isAvailabilityConfirmed = isAvailabilityConfirmed,
            isInstitutionalBloodBank = false,
            isSampleEntry = false,
            consentConfirmed = true
        )
        return bloodDonorDao.insertDonor(entry)
    }

    suspend fun removeBloodDonor(donorId: Long) {
        bloodDonorDao.deleteDonorById(donorId)
    }

    suspend fun submitLocalIssueReport(
        serviceId: String,
        serviceNameBn: String,
        issueTypeBn: String,
        detailsBn: String,
        suggestedCorrectionBn: String
    ): IssueReportEntry {
        val entry = IssueReportEntry(
            serviceId = serviceId,
            serviceNameBn = serviceNameBn,
            issueTypeBn = issueTypeBn,
            detailsBn = detailsBn.trim(),
            suggestedCorrectionBn = suggestedCorrectionBn.trim()
        )
        val id = issueReportDao.insertReport(entry)
        return entry.copy(id = id)
    }

    suspend fun deleteIssueReport(reportId: Long) {
        issueReportDao.deleteReportById(reportId)
    }
}
