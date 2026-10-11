package com.example.models

/**
 * তথ্যের যাচাইকরণ স্ট্যাটাস (Verified Data Policy অনুযায়ী)।
 */
enum class VerificationStatus(
    val id: String,
    val labelBn: String,
    val descriptionBn: String
) {
    VERIFIED(
        id = "VERIFIED",
        labelBn = "যাচাইকৃত",
        descriptionBn = "সরকারি পোর্টাল বা প্রাতিষ্ঠানিক ডিরেক্টরি থেকে যাচাইকৃত তথ্য"
    ),
    NEEDS_REVERIFICATION(
        id = "NEEDS_REVERIFICATION",
        labelBn = "পুনরায় যাচাই প্রয়োজন",
        descriptionBn = "তথ্যটি প্রকাশিত উৎস থেকে সংগৃহীত হলেও সাম্প্রতিক পরিবর্তনের জন্য পুনরায় যাচাই প্রয়োজন"
    ),
    INCOMPLETE(
        id = "INCOMPLETE",
        labelBn = "তথ্য অসম্পূর্ণ",
        descriptionBn = "কিছু তথ্য (যেমন সরাসরি ল্যান্ডলাইন বা সময়সূচি) সরকারি ওয়েবসাইটে সম্পূর্ণ উল্লেখ নেই"
    );

    companion object {
        fun fromId(id: String): VerificationStatus {
            return entries.find { it.id == id } ?: NEEDS_REVERIFICATION
        }
    }
}
