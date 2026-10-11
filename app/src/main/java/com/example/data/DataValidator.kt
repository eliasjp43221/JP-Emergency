package com.example.data

import com.example.models.DataImportResult
import com.example.models.SeedDataPackage
import com.example.models.SeedServiceItem
import com.example.models.ServiceCategory
import com.example.models.ServiceEntry
import com.example.models.Upazila
import com.example.models.VerificationStatus
import com.example.utils.BengaliSearchAndFormatUtils
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * JSON ডাটা ভ্যালিডেশন এবং ডুপ্লিকেট শনাক্তকরণ সিস্টেম।
 */
object DataValidator {

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    fun parseAndValidateJson(
        jsonString: String,
        existingServices: List<ServiceEntry> = emptyList()
    ): Pair<List<ServiceEntry>, DataImportResult> {
        if (jsonString.isBlank()) {
            return emptyList<ServiceEntry>() to DataImportResult(
                isSuccess = false,
                importedCount = 0,
                updatedCount = 0,
                duplicateSkippedCount = 0,
                invalidSkippedCount = 0,
                messageBn = "JSON ফাইলটি খালি।",
                errorsBn = listOf("খালি JSON ইনপুট প্রদান করা হয়েছে।")
            )
        }

        val pkg = try {
            val adapter = moshi.adapter(SeedDataPackage::class.java)
            adapter.fromJson(jsonString)
        } catch (e: Exception) {
            return emptyList<ServiceEntry>() to DataImportResult(
                isSuccess = false,
                importedCount = 0,
                updatedCount = 0,
                duplicateSkippedCount = 0,
                invalidSkippedCount = 0,
                messageBn = "JSON ফরম্যাট সঠিক নয়: ${e.localizedMessage ?: "পার্সিং ত্রুটি"}",
                errorsBn = listOf("JSON সিনট্যাক্স বা ফিল্ড টাইপ সঠিক নয়।")
            )
        }

        if (pkg == null || pkg.schemaVersion < 1) {
            return emptyList<ServiceEntry>() to DataImportResult(
                isSuccess = false,
                importedCount = 0,
                updatedCount = 0,
                duplicateSkippedCount = 0,
                invalidSkippedCount = 0,
                messageBn = "অবৈধ স্কিমা ভার্সন। ন্যূনতম schemaVersion: 1 প্রয়োজন।",
                errorsBn = listOf("অবৈধ schemaVersion।")
            )
        }

        return validateItems(pkg.services, existingServices)
    }

    fun validateItems(
        items: List<SeedServiceItem>,
        existingServices: List<ServiceEntry> = emptyList()
    ): Pair<List<ServiceEntry>, DataImportResult> {
        val existingById = existingServices.associateBy { it.id }
        val seenIds = mutableSetOf<String>()
        val seenNameAndPhone = mutableSetOf<String>()
        val validEntries = mutableListOf<ServiceEntry>()
        val errors = mutableListOf<String>()

        var importedCount = 0
        var updatedCount = 0
        var duplicateCount = 0
        var invalidCount = 0

        for ((index, item) in items.withIndex()) {
            val itemLabel = item.nameBn.ifBlank { "আইটেম #${index + 1}" }

            // ১. আবশ্যক ফিল্ড পরীক্ষা
            if (item.id.isBlank() || item.nameBn.isBlank() || item.addressBn.isBlank() || item.sourceNameBn.isBlank()) {
                invalidCount++
                errors.add("$itemLabel: আবশ্যক তথ্য (ID, বাংলা নাম, ঠিকানা বা সূত্র) অনুপস্থিত।")
                continue
            }

            // ২. ক্যাটাগরি, উপজেলা ও স্ট্যাটাস ভ্যালিডেশন
            val categoryExists = ServiceCategory.entries.any { it.id == item.categoryId && it != ServiceCategory.ALL }
            val upazilaExists = Upazila.entries.any { it.id == item.upazilaId && it != Upazila.ALL }
            val statusExists = VerificationStatus.entries.any { it.id == item.verificationStatus }

            if (!categoryExists || !upazilaExists || !statusExists) {
                invalidCount++
                errors.add("$itemLabel: অবৈধ ক্যাটাগরি, উপজেলা অথবা যাচাই স্ট্যাটাস কোড।")
                continue
            }

            // ৩. ফোন নম্বর ভ্যালিডেশন (যদি নম্বর দেওয়া থাকে, তবে তা বৈধ হতে হবে)
            if (item.primaryPhone.isNotBlank() && !BengaliSearchAndFormatUtils.isValidPhoneNumber(item.primaryPhone)) {
                invalidCount++
                errors.add("$itemLabel: প্রাথমিক ফোন নম্বরটি (${item.primaryPhone}) বৈধ নয়।")
                continue
            }

            // ৪. ডুপ্লিকেট শনাক্তকরণ (ID অথবা একই নাম + একই ফোন নম্বর)
            val normPhone = BengaliSearchAndFormatUtils.normalizeDialNumber(item.primaryPhone)
            val compositeKey = "${item.nameBn.trim()}|$normPhone"
            if (!seenIds.add(item.id.trim()) || (normPhone.isNotEmpty() && !seenNameAndPhone.add(compositeKey) && item.categoryId != ServiceCategory.AMBULANCE.id && item.categoryId != ServiceCategory.BLOOD_DONOR.id)) {
                duplicateCount++
                errors.add("$itemLabel: ডুপ্লিকেট এন্ট্রি শনাক্ত হওয়ায় বাদ দেওয়া হয়েছে।")
                continue
            }

            val existing = existingById[item.id.trim()]
            if (existing != null) {
                updatedCount++
            } else {
                importedCount++
            }

            validEntries.add(
                item.toServiceEntry(
                    isFavorite = existing?.isFavorite ?: false,
                    lastViewedTimestamp = existing?.lastViewedTimestamp
                )
            )
        }

        val isSuccess = validEntries.isNotEmpty()
        val summaryMessage = if (isSuccess) {
            "সফলভাবে ${BengaliSearchAndFormatUtils.toBengaliNumber(validEntries.size)}টি সেবা যাচাই ও যুক্ত করা হয়েছে " +
                "(নতুন: ${BengaliSearchAndFormatUtils.toBengaliNumber(importedCount)}, " +
                "আপডেট: ${BengaliSearchAndFormatUtils.toBengaliNumber(updatedCount)}, " +
                "ডুপ্লিকেট বাদ: ${BengaliSearchAndFormatUtils.toBengaliNumber(duplicateCount)}, " +
                "ত্রুটিপূর্ণ বাদ: ${BengaliSearchAndFormatUtils.toBengaliNumber(invalidCount)})।"
        } else {
            "কোনো বৈধ সেবা এন্ট্রি পাওয়া যায়নি।"
        }

        return validEntries to DataImportResult(
            isSuccess = isSuccess,
            importedCount = importedCount,
            updatedCount = updatedCount,
            duplicateSkippedCount = duplicateCount,
            invalidSkippedCount = invalidCount,
            messageBn = summaryMessage,
            errorsBn = errors
        )
    }

    fun exportToJson(services: List<ServiceEntry>, datasetVersion: String = "1.0.0"): String {
        val pkg = SeedDataPackage(
            schemaVersion = 1,
            datasetVersion = datasetVersion,
            districtNameBn = "জামালপুর",
            lastUpdatedBn = "অক্টোবর ২০২৬",
            services = services.map { entry ->
                SeedServiceItem(
                    id = entry.id,
                    nameBn = entry.nameBn,
                    officialNameEn = entry.officialNameEn,
                    categoryId = entry.categoryId,
                    upazilaId = entry.upazilaId,
                    addressBn = entry.addressBn,
                    primaryPhone = entry.primaryPhone,
                    secondaryPhone = entry.secondaryPhone,
                    email = entry.email,
                    openingHoursBn = entry.openingHoursBn,
                    isEmergency24hVerified = entry.isEmergency24hVerified,
                    availableServicesBn = entry.availableServicesBn,
                    verificationStatus = entry.verificationStatus,
                    lastVerifiedDateBn = entry.lastVerifiedDateBn,
                    sourceNameBn = entry.sourceNameBn,
                    sourceUrl = entry.sourceUrl,
                    isNationalEmergencyHotline = entry.isNationalEmergencyHotline,
                    latitude = entry.latitude,
                    longitude = entry.longitude,
                    searchKeywords = entry.searchKeywords
                )
            }
        )
        val adapter = moshi.adapter(SeedDataPackage::class.java).indent("  ")
        return adapter.toJson(pkg)
    }
}
