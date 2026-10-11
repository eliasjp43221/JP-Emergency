package com.example.utils

import com.example.models.ServiceCategory
import com.example.models.ServiceEntry
import com.example.models.Upazila
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * বাংলা ও ইংরেজি অনুসন্ধান, সংখ্যা রূপান্তর, ফোন নম্বর যাচাই এবং দূরত্ব পরিমাপের ইউটিলিটি।
 */
object BengaliSearchAndFormatUtils {

    private val bengaliToEnglishDigits = mapOf(
        '০' to '0',
        '১' to '1',
        '২' to '2',
        '৩' to '3',
        '৪' to '4',
        '৫' to '5',
        '৬' to '6',
        '৭' to '7',
        '৮' to '8',
        '৯' to '9'
    )

    private val englishToBengaliDigits = bengaliToEnglishDigits.entries.associate { (k, v) -> v to k }

    /**
     * যেকোনো বাংলা সংখ্যাকে ইংরেজি সংখ্যায় (ASCII digits) রূপান্তর করে।
     */
    fun toAsciiDigits(input: String): String {
        val sb = StringBuilder(input.length)
        for (ch in input) {
            sb.append(bengaliToEnglishDigits[ch] ?: ch)
        }
        return sb.toString()
    }

    /**
     * যেকোনো ইংরেজি সংখ্যাকে বাংলা সংখ্যায় রূপান্তর করে।
     */
    fun toBengaliDigits(input: String): String {
        val sb = StringBuilder(input.length)
        for (ch in input) {
            sb.append(englishToBengaliDigits[ch] ?: ch)
        }
        return sb.toString()
    }

    fun toBengaliNumber(number: Int): String = toBengaliDigits(number.toString())

    /**
     * ডায়াল করার জন্য ফোন নম্বর পরিষ্কার ও যাচাই করে।
     */
    fun normalizeDialNumber(rawPhone: String): String {
        val ascii = toAsciiDigits(rawPhone.trim())
        val cleaned = StringBuilder()
        for (i in ascii.indices) {
            val c = ascii[i]
            if (c.isDigit() || (c == '+' && cleaned.isEmpty())) {
                cleaned.append(c)
            }
        }
        return cleaned.toString()
    }

    /**
     * ফোন নম্বরটি ডায়াল করার উপযোগী ও বৈধ কি না তা পরীক্ষা করে।
     * ৩-৫ ডিজিটের জাতীয় হটলাইন (যেমন 999, 102, 131, 333, 1090, 16263, 16999)
     * এবং ৮-১৫ ডিজিটের ল্যান্ডলাইন/মোবাইল নম্বর গ্রহণযোগ্য।
     */
    fun isValidPhoneNumber(rawPhone: String): Boolean {
        val normalized = normalizeDialNumber(rawPhone)
        if (normalized.isEmpty()) return false
        val digitsOnly = normalized.removePrefix("+")
        if (!digitsOnly.all { it.isDigit() }) return false
        return digitsOnly.length in 3..5 || digitsOnly.length in 7..15
    }

    /**
     * বাংলা ও ইংরেজি শব্দ, ফোন নম্বর, ক্যাটাগরি, উপজেলা ও ঠিকানার ভিত্তিতে সার্চ ম্যাচিং।
     */
    fun matchesSearchQuery(entry: ServiceEntry, rawQuery: String): Boolean {
        val trimmed = rawQuery.trim()
        if (trimmed.isEmpty()) return true

        val queryLower = trimmed.lowercase(Locale.ROOT)
        val queryAscii = toAsciiDigits(queryLower)
        val queryNormPhone = normalizeDialNumber(trimmed)

        val category = ServiceCategory.fromId(entry.categoryId)
        val upazila = Upazila.fromId(entry.upazilaId)

        // ফোন নম্বর ম্যাচিং (বাংলা বা ইংরেজি যেকোনো ডিজিট দিয়ে)
        if (queryNormPhone.isNotEmpty() && queryNormPhone.all { it.isDigit() || it == '+' }) {
            val primaryNorm = normalizeDialNumber(entry.primaryPhone)
            val secondaryNorm = normalizeDialNumber(entry.secondaryPhone)
            if (primaryNorm.contains(queryNormPhone.removePrefix("+")) ||
                secondaryNorm.contains(queryNormPhone.removePrefix("+"))
            ) {
                return true
            }
        }

        // টেক্সট ফিল্ডগুলোর সমন্বিত সার্চ স্ট্রিং
        val searchableBlob = buildString {
            append(entry.nameBn).append(' ')
            append(entry.officialNameEn).append(' ')
            append(category.titleBn).append(' ')
            append(category.titleEn).append(' ')
            append(upazila.nameBn).append(' ')
            append(upazila.nameEn).append(' ')
            append(entry.addressBn).append(' ')
            append(entry.availableServicesBn).append(' ')
            append(entry.searchKeywords).append(' ')
            append(entry.primaryPhone).append(' ')
            append(entry.secondaryPhone)
        }.lowercase(Locale.ROOT)

        val searchableBlobAscii = toAsciiDigits(searchableBlob)

        // একাধিক শব্দের কুয়েরির জন্য প্রতিটি টোকেন পরীক্ষা করা হয়
        val tokens = queryAscii.split(Regex("\\s+")).filter { it.isNotEmpty() }
        return tokens.all { token ->
            searchableBlob.contains(token) || searchableBlobAscii.contains(token)
        }
    }

    /**
     * ক্যাটাগরি ও উপজেলা ফিল্টারের সাথে সার্চ কুয়েরির সমন্বিত ফিল্টার।
     */
    fun filterServices(
        services: List<ServiceEntry>,
        query: String,
        selectedCategory: ServiceCategory = ServiceCategory.ALL,
        selectedUpazila: Upazila = Upazila.ALL
    ): List<ServiceEntry> {
        return services.filter { entry ->
            val categoryMatch = selectedCategory == ServiceCategory.ALL ||
                entry.categoryId == selectedCategory.id
            val upazilaMatch = when (selectedUpazila) {
                Upazila.ALL -> true
                Upazila.DISTRICT_WIDE -> entry.upazilaId == Upazila.DISTRICT_WIDE.id
                else -> entry.upazilaId == selectedUpazila.id ||
                    entry.upazilaId == Upazila.DISTRICT_WIDE.id && query.isNotBlank()
            }
            // যদি ব্যবহারকারী নির্দিষ্ট কোনো উপজেলা ফিল্টার নির্বাচন করেন এবং সার্চ খালি থাকে,
            // তবে শুধুমাত্র সেই উপজেলার সেবা দেখাবে:
            val strictUpazilaMatch = when (selectedUpazila) {
                Upazila.ALL -> true
                else -> entry.upazilaId == selectedUpazila.id
            }
            categoryMatch && strictUpazilaMatch && matchesSearchQuery(entry, query)
        }
    }

    /**
     * দুটি ভৌগোলিক স্থানাঙ্কের মধ্যকার দূরত্ব (কিলোমিটারে) নির্ণয় (Haversine formula)।
     */
    fun calculateDistanceKm(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadiusKm * c
    }

    fun formatDistanceBn(distanceKm: Double): String {
        val formatted = if (distanceKm < 1.0) {
            val meters = (distanceKm * 1000).toInt().coerceAtLeast(50)
            "${toBengaliNumber(meters)} মিটার দূরে"
        } else {
            val rounded = String.format(Locale.US, "%.1f", distanceKm)
            "${toBengaliDigits(rounded)} কি.মি. দূরে"
        }
        return formatted
    }
}
