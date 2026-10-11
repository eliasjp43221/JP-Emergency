package com.example

import com.example.data.DataValidator
import com.example.data.VerifiedJamalpurSeedData
import com.example.models.ServiceCategory
import com.example.models.Upazila
import com.example.models.VerificationStatus
import com.example.utils.BengaliSearchAndFormatUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * জামালপুর সেবা অ্যাপের সার্চ, ফিল্টার, ফোন নম্বর ভ্যালিডেশন এবং JSON ডাটা ইমপোর্টের ইউনিট টেস্ট।
 */
class ExampleUnitTest {

    private val seedServices = VerifiedJamalpurSeedData.defaultSeedItems().map { it.toServiceEntry() }

    @Test
    fun `seed dataset contains all 7 upazilas and verified statuses`() {
        assertTrue(seedServices.isNotEmpty())
        for (upazila in Upazila.sevenUpazilas) {
            val entriesForUpazila = seedServices.filter { it.upazilaId == upazila.id }
            assertTrue("Missing entries for ${upazila.nameBn}", entriesForUpazila.isNotEmpty())
        }
        assertTrue(seedServices.all { VerificationStatus.entries.any { s -> s.id == it.verificationStatus } })
    }

    @Test
    fun `search by Bengali organization name returns matching services`() {
        val hospitalResults = BengaliSearchAndFormatUtils.filterServices(
            services = seedServices,
            query = "জামালপুর সদর হাসপাতাল"
        )
        assertTrue(hospitalResults.any { it.id == "hosp_jamalpur_250_general" })

        val melandahaThana = BengaliSearchAndFormatUtils.filterServices(
            services = seedServices,
            query = "মেলান্দহ থানা"
        )
        assertTrue(melandahaThana.any { it.id == "pol_melandaha_thana" })

        val electricityOffice = BengaliSearchAndFormatUtils.filterServices(
            services = seedServices,
            query = "বিদ্যুৎ অফিস"
        )
        assertTrue(electricityOffice.any { it.categoryId == ServiceCategory.ELECTRICITY_UTILITY.id })
    }

    @Test
    fun `search by phone number in both English and Bengali digits works`() {
        val byEnglishPhone = BengaliSearchAndFormatUtils.filterServices(
            services = seedServices,
            query = "01320-105179"
        )
        assertTrue(byEnglishPhone.any { it.id == "pol_sadar_thana" })

        val byBengaliPhone = BengaliSearchAndFormatUtils.filterServices(
            services = seedServices,
            query = "০১৩২০১০৫১৭৯"
        )
        assertTrue(byBengaliPhone.any { it.id == "pol_sadar_thana" })

        val by999 = BengaliSearchAndFormatUtils.filterServices(
            services = seedServices,
            query = "৯৯৯"
        )
        assertTrue(by999.any { it.primaryPhone == "999" })
    }

    @Test
    fun `category and upazila filtering work independently and simultaneously`() {
        // Category filter only
        val policeOnly = BengaliSearchAndFormatUtils.filterServices(
            services = seedServices,
            query = "",
            selectedCategory = ServiceCategory.POLICE_LAW,
            selectedUpazila = Upazila.ALL
        )
        assertTrue(policeOnly.isNotEmpty())
        assertTrue(policeOnly.all { it.categoryId == ServiceCategory.POLICE_LAW.id })

        // Upazila filter only
        val islampurOnly = BengaliSearchAndFormatUtils.filterServices(
            services = seedServices,
            query = "",
            selectedCategory = ServiceCategory.ALL,
            selectedUpazila = Upazila.ISLAMPUR
        )
        assertTrue(islampurOnly.isNotEmpty())
        assertTrue(islampurOnly.all { it.upazilaId == Upazila.ISLAMPUR.id })

        // Combined Category + Upazila filter
        val sarishabariFire = BengaliSearchAndFormatUtils.filterServices(
            services = seedServices,
            query = "",
            selectedCategory = ServiceCategory.FIRE_SERVICE,
            selectedUpazila = Upazila.SARISHABARI
        )
        assertEquals(1, sarishabariFire.size)
        assertEquals("fire_sarishabari", sarishabariFire.first().id)
    }

    @Test
    fun `empty search results when no service matches`() {
        val results = BengaliSearchAndFormatUtils.filterServices(
            services = seedServices,
            query = "অস্তিত্বহীন কাল্পনিক প্রতিষ্ঠান ৯৮৭৬৫৪"
        )
        assertTrue(results.isEmpty())
    }

    @Test
    fun `phone number validation rejects missing or invalid numbers and accepts valid ones`() {
        assertTrue(BengaliSearchAndFormatUtils.isValidPhoneNumber("999"))
        assertTrue(BengaliSearchAndFormatUtils.isValidPhoneNumber("১০২"))
        assertTrue(BengaliSearchAndFormatUtils.isValidPhoneNumber("01711-632276"))
        assertTrue(BengaliSearchAndFormatUtils.isValidPhoneNumber("+8801320105179"))
        assertTrue(BengaliSearchAndFormatUtils.isValidPhoneNumber("02997773333"))

        assertFalse(BengaliSearchAndFormatUtils.isValidPhoneNumber(""))
        assertFalse(BengaliSearchAndFormatUtils.isValidPhoneNumber("   "))
        assertFalse(BengaliSearchAndFormatUtils.isValidPhoneNumber("12"))
        assertFalse(BengaliSearchAndFormatUtils.isValidPhoneNumber("abc-def"))
    }

    @Test
    fun `JSON data validator validates valid schema and detects duplicates and invalid entries`() {
        val validJson = """
            {
              "schemaVersion": 1,
              "datasetVersion": "1.0.1",
              "districtNameBn": "জামালপুর",
              "lastUpdatedBn": "অক্টোবর ২০২৬",
              "services": [
                {
                  "id": "test_valid_1",
                  "nameBn": "জামালপুর টেস্ট ক্লিনিক",
                  "officialNameEn": "Jamalpur Test Clinic",
                  "categoryId": "HOSPITAL_HEALTH",
                  "upazilaId": "JAMALPUR_SADAR",
                  "addressBn": "সদর রোড, জামালপুর",
                  "primaryPhone": "01711-632276",
                  "availableServicesBn": "জরুরি চিকিৎসা",
                  "verificationStatus": "VERIFIED",
                  "lastVerifiedDateBn": "অক্টোবর ২০২৬",
                  "sourceNameBn": "স্বাস্থ্য অধিদপ্তর"
                },
                {
                  "id": "test_valid_1",
                  "nameBn": "ডুপ্লিকেট আইডি ক্লিনিক",
                  "officialNameEn": "Duplicate ID Clinic",
                  "categoryId": "HOSPITAL_HEALTH",
                  "upazilaId": "JAMALPUR_SADAR",
                  "addressBn": "সদর রোড, জামালপুর",
                  "primaryPhone": "01711-632276",
                  "availableServicesBn": "জরুরি চিকিৎসা",
                  "verificationStatus": "VERIFIED",
                  "lastVerifiedDateBn": "অক্টোবর ২০২৬",
                  "sourceNameBn": "স্বাস্থ্য অধিদপ্তর"
                },
                {
                  "id": "test_invalid_phone",
                  "nameBn": "ত্রুটিপূর্ণ নম্বর সেবা",
                  "officialNameEn": "Invalid Phone Service",
                  "categoryId": "HOSPITAL_HEALTH",
                  "upazilaId": "JAMALPUR_SADAR",
                  "addressBn": "সদর রোড, জামালপুর",
                  "primaryPhone": "12",
                  "availableServicesBn": "সেবা",
                  "verificationStatus": "VERIFIED",
                  "lastVerifiedDateBn": "অক্টোবর ২০২৬",
                  "sourceNameBn": "স্বাস্থ্য অধিদপ্তর"
                }
              ]
            }
        """.trimIndent()

        val (entries, result) = DataValidator.parseAndValidateJson(validJson)
        assertTrue(result.isSuccess)
        assertEquals(1, entries.size)
        assertEquals(1, result.duplicateSkippedCount)
        assertEquals(1, result.invalidSkippedCount)
    }
}
