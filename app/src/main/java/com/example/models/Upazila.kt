package com.example.models

/**
 * জামালপুর জেলার ৭টি উপজেলা এবং জেলাব্যাপী সেবার ফিল্টার মডেল।
 */
enum class Upazila(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val centerLat: Double,
    val centerLng: Double,
    val descriptionBn: String
) {
    ALL(
        id = "ALL",
        nameBn = "সকল এলাকা",
        nameEn = "All Areas",
        centerLat = 24.9375,
        centerLng = 89.9378,
        descriptionBn = "জামালপুর জেলার সকল উপজেলা ও জেলাব্যাপী সেবা"
    ),
    DISTRICT_WIDE(
        id = "DISTRICT_WIDE",
        nameBn = "জেলাব্যাপী সেবা",
        nameEn = "District-wide Services",
        centerLat = 24.9375,
        centerLng = 89.9378,
        descriptionBn = "পুরো জামালপুর জেলার জন্য প্রযোজ্য সরকারি ও জরুরি সেবাসমূহ"
    ),
    JAMALPUR_SADAR(
        id = "JAMALPUR_SADAR",
        nameBn = "জামালপুর সদর",
        nameEn = "Jamalpur Sadar",
        centerLat = 24.9375,
        centerLng = 89.9378,
        descriptionBn = "জামালপুর সদর উপজেলা ও পৌরসভা এলাকা"
    ),
    MELANDAHA(
        id = "MELANDAHA",
        nameBn = "মেলান্দহ",
        nameEn = "Melandaha",
        centerLat = 24.9708,
        centerLng = 89.8383,
        descriptionBn = "মেলান্দহ উপজেলা ও পৌর এলাকা"
    ),
    ISLAMPUR(
        id = "ISLAMPUR",
        nameBn = "ইসলামপুর",
        nameEn = "Islampur",
        centerLat = 25.0833,
        centerLng = 89.7917,
        descriptionBn = "ইসলামপুর উপজেলা ও পৌর এলাকা"
    ),
    DEWANGANJ(
        id = "DEWANGANJ",
        nameBn = "দেওয়ানগঞ্জ",
        nameEn = "Dewanganj",
        centerLat = 25.1458,
        centerLng = 89.7681,
        descriptionBn = "দেওয়ানগঞ্জ উপজেলা ও বাহাদুরাবাদ এলাকা"
    ),
    SARISHABARI(
        id = "SARISHABARI",
        nameBn = "সরিষাবাড়ী",
        nameEn = "Sarishabari",
        centerLat = 24.7417,
        centerLng = 89.8333,
        descriptionBn = "সরিষাবাড়ী উপজেলা ও তারাকান্দি এলাকা"
    ),
    MADARGANJ(
        id = "MADARGANJ",
        nameBn = "মাদারগঞ্জ",
        nameEn = "Madarganj",
        centerLat = 24.8917,
        centerLng = 89.7500,
        descriptionBn = "মাদারগঞ্জ উপজেলা ও বালিজুড়ী এলাকা"
    ),
    BAKSHIGANJ(
        id = "BAKSHIGANJ",
        nameBn = "বকশীগঞ্জ",
        nameEn = "Bakshiganj",
        centerLat = 25.1833,
        centerLng = 89.8722,
        descriptionBn = "বকশীগঞ্জ উপজেলা ও কামালপুর সীমান্ত এলাকা"
    );

    companion object {
        fun fromId(id: String): Upazila {
            return entries.find { it.id == id } ?: ALL
        }

        val sevenUpazilas: List<Upazila> = listOf(
            JAMALPUR_SADAR,
            MELANDAHA,
            ISLAMPUR,
            DEWANGANJ,
            SARISHABARI,
            MADARGANJ,
            BAKSHIGANJ
        )
    }
}
