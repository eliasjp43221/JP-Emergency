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
     */
    fun isValidPhoneNumber(rawPhone: String): Boolean {
        val normalized = normalizeDialNumber(rawPhone)
        if (normalized.isEmpty()) return false
        val digitsOnly = normalized.removePrefix("+")
        if (!digitsOnly.all { it.isDigit() }) return false
        return digitsOnly.length in 3..5 || digitsOnly.length in 7..15
    }

    /**
     * প্রি-কমপিউটেড সার্চ স্ট্রিং ব্যবহার করে দ্রুতগতির বাংলা ও ইংরেজি সার্চ ম্যাচিং।
     */
    fun matchesSearchQuery(entry: ServiceEntry, rawQuery: String): Boolean {
        val trimmed = rawQuery.trim()
        if (trimmed.isEmpty()) return true

        val queryLower = trimmed.lowercase(Locale.ROOT)
        val queryAscii = toAsciiDigits(queryLower)
        val queryNormPhone = normalizeDialNumber(trimmed)

        if (queryNormPhone.isNotEmpty() && queryNormPhone.all { it.isDigit() || it == '+' }) {
            val cleanQueryDigits = queryNormPhone.removePrefix("+")
            if (entry.normalizedPrimaryPhone.contains(cleanQueryDigits) ||
                entry.normalizedSecondaryPhone.contains(cleanQueryDigits)
            ) {
                return true
            }
        }

        val tokens = queryAscii.split(Regex("\\s+")).filter { it.isNotEmpty() }
        return tokens.all { token ->
            entry.searchableBlob.contains(token) || entry.searchableBlobAscii.contains(token)
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
        val hasQuery = query.isNotBlank()
        val filterByCategory = selectedCategory != ServiceCategory.ALL
        val filterByUpazila = selectedUpazila != Upazila.ALL

        if (!hasQuery && !filterByCategory && !filterByUpazila) {
            return services
        }

        return services.filter { entry ->
            val categoryMatch = !filterByCategory || entry.categoryId == selectedCategory.id
            val strictUpazilaMatch = !filterByUpazila || entry.upazilaId == selectedUpazila.id
            categoryMatch && strictUpazilaMatch && (!hasQuery || matchesSearchQuery(entry, query))
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
        return if (distanceKm < 1.0) {
            val meters = (distanceKm * 1000).toInt().coerceAtLeast(50)
            "${toBengaliNumber(meters)} মিটার দূরে"
        } else {
            val rounded = String.format(Locale.US, "%.1f", distanceKm)
            "${toBengaliDigits(rounded)} কি.মি. দূরে"
        }
    }
}
