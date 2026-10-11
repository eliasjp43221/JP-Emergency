package com.example.models

/**
 * জামালপুর সেবা অ্যাপের সকল সেবার ক্যাটাগরি।
 * প্রতিটি ক্যাটাগরির বাংলা নাম, ইংরেজি নাম এবং সংক্ষিপ্ত বিবরণ সংরক্ষিত।
 */
enum class ServiceCategory(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val descriptionBn: String,
    val isEmergencyPriority: Boolean = false
) {
    ALL(
        id = "ALL",
        titleBn = "সকল সেবা",
        titleEn = "All Services",
        descriptionBn = "সকল বিভাগের যাচাইকৃত ও নথিভুক্ত সেবাসমূহ"
    ),
    HOSPITAL_HEALTH(
        id = "HOSPITAL_HEALTH",
        titleBn = "হাসপাতাল ও স্বাস্থ্যসেবা",
        titleEn = "Hospitals & Healthcare",
        descriptionBn = "সরকারি হাসপাতাল, উপজেলা স্বাস্থ্য কমপ্লেক্স, মেডিকেল কলেজ ও ক্লিনিক",
        isEmergencyPriority = true
    ),
    AMBULANCE(
        id = "AMBULANCE",
        titleBn = "অ্যাম্বুলেন্স",
        titleEn = "Ambulance Services",
        descriptionBn = "জরুরি রোগী পরিবহনে সরকারি ও প্রাতিষ্ঠানিক অ্যাম্বুলেন্স সহায়তা",
        isEmergencyPriority = true
    ),
    POLICE_LAW(
        id = "POLICE_LAW",
        titleBn = "পুলিশ ও আইনশৃঙ্খলা",
        titleEn = "Police & Law Enforcement",
        descriptionBn = "জেলা পুলিশ সুপার কার্যালয় ও ৭টি উপজেলার থানার ওসি ও ডিউটি নম্বর",
        isEmergencyPriority = true
    ),
    FIRE_SERVICE(
        id = "FIRE_SERVICE",
        titleBn = "ফায়ার সার্ভিস",
        titleEn = "Fire Service & Civil Defence",
        descriptionBn = "অগ্নিনির্বাপণ ও জরুরি উদ্ধার স্টেশনের নম্বরসমূহ",
        isEmergencyPriority = true
    ),
    BLOOD_DONOR(
        id = "BLOOD_DONOR",
        titleBn = "রক্তদাতা",
        titleEn = "Blood Bank & Voluntary Donors",
        descriptionBn = "ব্লাড ব্যাংক ও স্বেচ্ছাসেবী রক্তদাতা ডিরেক্টরি",
        isEmergencyPriority = true
    ),
    PHARMACY(
        id = "PHARMACY",
        titleBn = "ফার্মেসি",
        titleEn = "Pharmacy & Medicine",
        descriptionBn = "হাসপাতাল সংলগ্ন ও জরুরি ওষুধ প্রাপ্তির তথ্য"
    ),
    GOVT_OFFICE(
        id = "GOVT_OFFICE",
        titleBn = "সরকারি অফিস",
        titleEn = "Government Offices",
        descriptionBn = "জেলা প্রশাসক, উপজেলা নির্বাহী অফিসার (UNO) ও সিভিল সার্জন কার্যালয়"
    ),
    ELECTRICITY_UTILITY(
        id = "ELECTRICITY_UTILITY",
        titleBn = "বিদ্যুৎ ও ইউটিলিটি সেবা",
        titleEn = "Electricity & Utilities",
        descriptionBn = "জামালপুর পল্লী বিদ্যুৎ সমিতি ও বিপিডিবি (BPDB) অভিযোগ কেন্দ্র"
    ),
    GAS_UTILITY(
        id = "GAS_UTILITY",
        titleBn = "গ্যাস ও অন্যান্য ইউটিলিটি",
        titleEn = "Gas & Other Utilities",
        descriptionBn = "তিতাস গ্যাস ও পৌর পানি সরবরাহ সংক্রান্ত তথ্য"
    ),
    EDUCATION(
        id = "EDUCATION",
        titleBn = "শিক্ষা প্রতিষ্ঠান",
        titleEn = "Educational Institutions",
        descriptionBn = "সরকারি আশেক মাহমুদ কলেজ, মেডিকেল কলেজ ও জেলা শিক্ষা অফিস"
    ),
    TRANSPORT_BUS(
        id = "TRANSPORT_BUS",
        titleBn = "পরিবহন ও বাস কাউন্টার",
        titleEn = "Transport & Bus Terminals",
        descriptionBn = "জামালপুর কেন্দ্রীয় বাস টার্মিনাল ও আন্তঃজেলা পরিবহন তথ্য"
    ),
    RAILWAY(
        id = "RAILWAY",
        titleBn = "রেলওয়ে",
        titleEn = "Railway Services",
        descriptionBn = "জামালপুর টাউন জংশন, দেওয়ানগঞ্জ বাজার ও সরিষাবাড়ী রেলস্টেশন"
    ),
    POST_COURIER(
        id = "POST_COURIER",
        titleBn = "ডাক ও কুরিয়ার",
        titleEn = "Post Office & Courier",
        descriptionBn = "জামালপুর প্রধান ডাকঘর (পোস্ট কোড ২০০০) ও ডাক সেবা"
    ),
    DISASTER_RELIEF(
        id = "DISASTER_RELIEF",
        titleBn = "দুর্যোগ ও ত্রাণ সহায়তা",
        titleEn = "Disaster & Relief Support",
        descriptionBn = "জেলা ত্রাণ ও পুনর্বাসন কর্মকর্তা ও বন্যা নিয়ন্ত্রণ কন্ট্রোল রুম"
    ),
    VETERINARY(
        id = "VETERINARY",
        titleBn = "পশু চিকিৎসা",
        titleEn = "Veterinary & Livestock",
        descriptionBn = "জেলা প্রাণিসম্পদ দপ্তর ও উপজেলা ভেটেরিনারি হাসপাতাল"
    ),
    AGRICULTURE_FISHERIES(
        id = "AGRICULTURE_FISHERIES",
        titleBn = "কৃষি ও মৎস্য সেবা",
        titleEn = "Agriculture & Fisheries",
        descriptionBn = "কৃষি সম্প্রসারণ অধিদপ্তর ও জেলা মৎস্য কর্মকর্তার কার্যালয়"
    ),
    TOURISM(
        id = "TOURISM",
        titleBn = "পর্যটন ও দর্শনীয় স্থান",
        titleEn = "Tourism & Landmarks",
        descriptionBn = "হযরত শাহ জামাল (রহ.) মাজার শরীফ, লাউচাপড়া ও যমুনা ফার্টিলাইজার এলাকা"
    ),
    OTHER_ESSENTIAL(
        id = "OTHER_ESSENTIAL",
        titleBn = "অন্যান্য গুরুত্বপূর্ণ সেবা",
        titleEn = "Other Essential Services",
        descriptionBn = "জাতীয় হেল্পলাইন ও জনগুরুত্বপূর্ণ নাগরিক সেবা"
    );

    companion object {
        fun fromId(id: String): ServiceCategory {
            return entries.find { it.id == id } ?: OTHER_ESSENTIAL
        }

        val displayCategories: List<ServiceCategory> = entries.filter { it != ALL }
    }
}
