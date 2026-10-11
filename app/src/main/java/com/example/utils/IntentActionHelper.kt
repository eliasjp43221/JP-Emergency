package com.example.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.models.IssueReportEntry
import com.example.models.ServiceEntry

/**
 * কল করা (ACTION_DIAL), ম্যাপে দেখা (geo URI) এবং শেয়ার করার ইন্টেন্ট হেল্পার।
 */
object IntentActionHelper {

    /**
     * নিরাপদভাবে সিস্টেম ডায়ালার খোলার ইন্টেন্ট তৈরি করে (স্বয়ংক্রিয় কল করে না)।
     * নম্বর অবৈধ বা খালি হলে null রিটার্ন করে।
     */
    fun createDialIntent(rawPhone: String): Intent? {
        if (!BengaliSearchAndFormatUtils.isValidPhoneNumber(rawPhone)) {
            return null
        }
        val normalized = BengaliSearchAndFormatUtils.normalizeDialNumber(rawPhone)
        return Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$normalized")
        }
    }

    /**
     * ম্যাপ অ্যাপ্লিকেশনে লোকেশন বা ঠিকানা দেখার ইন্টেন্ট তৈরি করে।
     */
    fun createMapIntent(entry: ServiceEntry): Intent {
        val queryText = "${entry.nameBn}, ${entry.addressBn}, জামালপুর"
        val encodedQuery = Uri.encode(queryText)
        val geoUri = if (entry.latitude != null && entry.longitude != null) {
            Uri.parse("geo:${entry.latitude},${entry.longitude}?q=${entry.latitude},${entry.longitude}($encodedQuery)")
        } else {
            Uri.parse("geo:0,0?q=$encodedQuery")
        }
        return Intent(Intent.ACTION_VIEW, geoUri)
    }

    /**
     * সেবার তথ্য শেয়ার করার ইন্টেন্ট তৈরি করে।
     */
    fun createShareServiceIntent(entry: ServiceEntry): Intent {
        val shareText = buildString {
            appendLine("📌 ${entry.nameBn}")
            if (entry.officialNameEn.isNotBlank()) {
                appendLine("(${entry.officialNameEn})")
            }
            appendLine("বিভাগ: ${entry.category.titleBn}")
            appendLine("এলাকা: ${entry.upazila.nameBn}")
            appendLine("ঠিকানা: ${entry.addressBn}")
            if (entry.primaryPhone.isNotBlank()) {
                appendLine("ফোন: ${entry.primaryPhone}")
            }
            if (entry.secondaryPhone.isNotBlank()) {
                appendLine("বিকল্প ফোন: ${entry.secondaryPhone}")
            }
            appendLine("যাচাই স্ট্যাটাস: ${entry.status.labelBn} (${entry.lastVerifiedDateBn})")
            appendLine("সূত্র: ${entry.sourceNameBn}")
            append("— জামালপুর সেবা অ্যাপ থেকে শেয়ারকৃত")
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, entry.nameBn)
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        return Intent.createChooser(sendIntent, "সেবার তথ্য শেয়ার করুন")
    }

    /**
     * তথ্যে ভুল বা পরিবর্তনের রিপোর্ট শেয়ার করার ইন্টেন্ট তৈরি করে।
     */
    fun createReportShareIntent(report: IssueReportEntry): Intent {
        val reportText = buildString {
            appendLine("🛠️ তথ্য সংশোধন রিপোর্ট — জামালপুর সেবা")
            appendLine("প্রতিষ্ঠান: ${report.serviceNameBn} (ID: ${report.serviceId})")
            appendLine("সমস্যার ধরন: ${report.issueTypeBn}")
            appendLine("বিবরণ: ${report.detailsBn}")
            if (report.suggestedCorrectionBn.isNotBlank()) {
                appendLine("সঠিক তথ্য/প্রস্তাবনা: ${report.suggestedCorrectionBn}")
            }
        }
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "তথ্য সংশোধন রিপোর্ট: ${report.serviceNameBn}")
            putExtra(Intent.EXTRA_TEXT, reportText)
        }
        return Intent.createChooser(sendIntent, "রিপোর্টটি পাঠান বা সংরক্ষণ করুন")
    }

    fun launchDialer(context: Context, rawPhone: String): Boolean {
        val intent = createDialIntent(rawPhone) ?: return false
        return try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun launchMap(context: Context, entry: ServiceEntry): Boolean {
        val intent = createMapIntent(entry)
        return try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            // ম্যাপ অ্যাপ না থাকলে ব্রাউজারে ওপেন করার ফলব্যাক
            try {
                val query = Uri.encode("${entry.nameBn}, ${entry.addressBn}, Jamalpur")
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=$query")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
                true
            } catch (ex: Exception) {
                false
            }
        }
    }

    fun launchShare(context: Context, entry: ServiceEntry): Boolean {
        return try {
            val chooser = createShareServiceIntent(entry).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            false
        }
    }
}
