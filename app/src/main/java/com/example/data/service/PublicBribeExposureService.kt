package com.example.data.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class BribeReport(
    val id: String,
    val countryFlag: String = "🇧🇩",
    val countryNameBn: String = "বাংলাদেশ",
    val officerName: String,
    val designation: String,
    val ministry: String,
    val departmentOffice: String,
    val district: String,
    val upazilaThana: String,
    val bribeAmountBdt: Long,
    val incidentDescription: String,
    val reportedTimeAgo: String,
    val verifiedProofAttached: Boolean = false,
    val upvotesCount: Int = 0,
    val commentsCount: Int = 12,
    val sharesCount: Int = 45,
    val isAnonymous: Boolean = true
)

data class MinistryBribeSummary(
    val ministryName: String,
    val iconEmoji: String,
    val totalBribeAmountBdt: Long,
    val totalReportsCount: Int
)

object PublicBribeExposureRegistry {

    private val initialReports = listOf(
        BribeReport(
            id = "br_101",
            countryFlag = "🇧🇩",
            countryNameBn = "বাংলাদেশ",
            officerName = "রফিকুল ইসলাম",
            designation = "সহকারী ভূমি কর্মকর্তা (কানুনগো)",
            ministry = "ভূমি মন্ত্রণালয়",
            departmentOffice = "উপজেলা ভূমি অফিস",
            district = "ঢাকা",
            upazilaThana = "সাভার",
            bribeAmountBdt = 75000,
            incidentDescription = "জমি নামজারি (খারিজ) করার ফাইল ৩ মাস ধরে আটকে রেখে সরাসরি ৭৫,০০০ টাকা দাবি করেছেন। টাকা না দিলে জমি খাস করে দেওয়ার হুমকি দেন।",
            reportedTimeAgo = "১০ মিনিট আগে",
            verifiedProofAttached = true,
            upvotesCount = 342,
            commentsCount = 28,
            sharesCount = 94
        ),
        BribeReport(
            id = "br_102",
            countryFlag = "🇧🇩",
            countryNameBn = "বাংলাদেশ",
            officerName = "মো: জহিরুল হক",
            designation = "উপ-পরিদর্শক (এসআই)",
            ministry = "স্বরাষ্ট্র মন্ত্রণালয় (পুলিশ)",
            departmentOffice = "মডেল থানা",
            district = "চট্টগ্রাম",
            upazilaThana = "কোতোয়ালী",
            bribeAmountBdt = 120000,
            incidentDescription = "মিথ্যা মামলায় ফাঁসিয়ে দেওয়ার ভয় দেখিয়ে গভীর রাতে গাড়ি থেকে নামিয়ে ১২০,০০০ টাকা ঘুষ নেন এবং বিকাশ একাউন্টে ট্রান্সফার করান।",
            reportedTimeAgo = "৩৫ মিনিট আগে",
            verifiedProofAttached = true,
            upvotesCount = 819,
            commentsCount = 64,
            sharesCount = 230
        ),
        BribeReport(
            id = "br_103",
            countryFlag = "🇧🇩",
            countryNameBn = "বাংলাদেশ",
            officerName = "কাজী কামরুল হাসান",
            designation = "সহকারী পরিচালক (এডি)",
            ministry = "স্বরাষ্ট্র মন্ত্রণালয় (পাসপোর্ট অধিদপ্তর)",
            departmentOffice = "আঞ্চলিক পাসপোর্ট অফিস",
            district = "বগুড়া",
            upazilaThana = "বগুড়া সদর",
            bribeAmountBdt = 15000,
            incidentDescription = "জরুরি পাসপোর্ট ভেরিফিকেশন ক্লিয়ারেন্স আটকে রেখে ১৫,০০০ টাকা ঘুষের চুক্তি না করলে এনআইডি মিসম্যাচ দেখানোর হুমকি দেন।",
            reportedTimeAgo = "২ ঘণ্টা আগে",
            verifiedProofAttached = false,
            upvotesCount = 205,
            commentsCount = 19,
            sharesCount = 51
        ),
        BribeReport(
            id = "br_104",
            countryFlag = "🇧🇩",
            countryNameBn = "বাংলাদেশ",
            officerName = "মোঃ আনিসুর রহমান",
            designation = "পরিদর্শক (ট্রাফিক ও ফিটনেস)",
            ministry = "সড়ক পরিবহন ও সেতু মন্ত্রণালয় (বিআরটিএ)",
            departmentOffice = "বিআরটিএ সার্কেল অফিস",
            district = "সিলেট",
            upazilaThana = "দক্ষিণ সুরমা",
            bribeAmountBdt = 35000,
            incidentDescription = "নতুন গাড়ির রেজিস্ট্রেশন ও ফিটনেস সনদের জন্য দালালের মাধ্যমে ৩৫,০০০ টাকা না দিলে ফাইল জমা নিতে অস্বীকার করেন।",
            reportedTimeAgo = "৫ ঘণ্টা আগে",
            verifiedProofAttached = true,
            upvotesCount = 450,
            commentsCount = 31,
            sharesCount = 112
        ),
        BribeReport(
            id = "br_105",
            countryFlag = "🇧🇩",
            countryNameBn = "বাংলাদেশ",
            officerName = "আব্দুস সাত্তার",
            designation = "উপ-সহকারী প্রকৌশলী",
            ministry = "বিদ্যুৎ, জ্বালানি ও খনিজ সম্পদ মন্ত্রণালয় (পল্লী বিদ্যুৎ)",
            departmentOffice = "পল্লী বিদ্যুৎ জোনাল অফিস",
            district = "ময়মনসিংহ",
            upazilaThana = "ত্রিশাল",
            bribeAmountBdt = 50000,
            incidentDescription = "রাইসমিলের জন্য নতুন থ্রি-ফেজ বিদ্যুৎ সংযোগের মিটার পোল বসাতে ৫০,০০০ টাকা ঘুষ দাবি করেন এবং টাকা না দেওয়ায় পোল খুলে নিয়ে যান।",
            reportedTimeAgo = "১ দিন আগে",
            verifiedProofAttached = true,
            upvotesCount = 612,
            commentsCount = 42,
            sharesCount = 188
        )
    )

    private val _bribeReports = MutableStateFlow<List<BribeReport>>(initialReports)
    val bribeReports: StateFlow<List<BribeReport>> = _bribeReports.asStateFlow()

    fun submitNewReport(
        countryFlag: String = "🇧🇩",
        countryName: String = "বাংলাদেশ",
        officerName: String,
        designation: String,
        ministry: String,
        office: String,
        district: String,
        thana: String,
        amountBdt: Long,
        description: String,
        hasProof: Boolean
    ) {
        val newReport = BribeReport(
            id = "br_${System.currentTimeMillis()}",
            countryFlag = countryFlag,
            countryNameBn = countryName,
            officerName = officerName.ifBlank { "অজ্ঞাত নাম/কর্মকর্তা" },
            designation = designation.ifBlank { "কর্মকর্তা/কর্মচারী" },
            ministry = ministry,
            departmentOffice = office.ifBlank { "স্থানীয় কার্যালয়" },
            district = district.ifBlank { "ঢাকা" },
            upazilaThana = thana.ifBlank { "সদর" },
            bribeAmountBdt = amountBdt,
            incidentDescription = description,
            reportedTimeAgo = "এখনই প্রকাশিত",
            verifiedProofAttached = hasProof,
            upvotesCount = 1,
            commentsCount = 0,
            sharesCount = 0
        )
        _bribeReports.value = listOf(newReport) + _bribeReports.value
    }

    fun upvoteReport(reportId: String) {
        _bribeReports.value = _bribeReports.value.map {
            if (it.id == reportId) it.copy(upvotesCount = it.upvotesCount + 1) else it
        }
    }

    fun getMinistrySummaries(reports: List<BribeReport>): List<MinistryBribeSummary> {
        val ministryMap = reports.groupBy { it.ministry }
        
        val predefinedList = listOf(
            Triple("ভূমি মন্ত্রণালয়", "📜", 1_45_00_000L),
            Triple("স্বরাষ্ট্র মন্ত্রণালয় (পুলিশ/পাসপোর্ট)", "🚔", 2_15_00_000L),
            Triple("সড়ক পরিবহন ও সেতু (BRTA)", "🚗", 85_00_000L),
            Triple("বিদ্যুৎ ও জ্বালানি (পল্লী বিদ্যুৎ/ডেসকো)", "⚡", 62_00_000L),
            Triple("শিক্ষা মন্ত্রণালয় (স্কুল/কলেজ/বোর্ড)", "🎓", 45_00_000L),
            Triple("স্বাস্থ্য ও পরিবার কল্যাণ (হাসপাতাল)", "🏥", 78_00_000L)
        )

        return predefinedList.map { (name, emoji, baseAmt) ->
            val matching = ministryMap[name] ?: emptyList()
            val extraAmt = matching.sumOf { it.bribeAmountBdt }
            MinistryBribeSummary(
                ministryName = name,
                iconEmoji = emoji,
                totalBribeAmountBdt = baseAmt + extraAmt,
                totalReportsCount = 42 + matching.size
            )
        }
    }

    val bdDistrictsList = listOf(
        "ঢাকা", "চট্টগ্রাম", "সিলেট", "বগুড়া", "ময়মনসিংহ", "রাজশাহী", "খুলনা", "বরিশাল", 
        "রংপুর", "কুমিল্লা", "গাজীপুর", "নারায়ণগঞ্জ", "ফেনী", "কক্সবাজার", "দিনাজপুর", "যশোর"
    )

    val bdMinistriesList = listOf(
        "ভূমি মন্ত্রণালয়",
        "স্বরাষ্ট্র মন্ত্রণালয় (পুলিশ/পাসপোর্ট)",
        "সড়ক পরিবহন ও সেতু (BRTA)",
        "বিদ্যুৎ ও জ্বালানি (পল্লী বিদ্যুৎ/ডেসকো)",
        "শিক্ষা মন্ত্রণালয় (স্কুল/কলেজ/বোর্ড)",
        "স্বাস্থ্য ও পরিবার কল্যাণ (হাসপাতাল)",
        "আইন ও বিচার বিভাগ (আদালত/রেজিস্ট্রি)",
        "স্থানীয় সরকার ও পল্লী উন্নয়ন (সিটি কর্পোরেশন/ইউপি)"
    )

    val countriesList = listOf(
        Pair("🇧🇩", "বাংলাদেশ"),
        Pair("🇮🇳", "ভারত"),
        Pair("🇺🇸", "যুক্তরাষ্ট্র"),
        Pair("🇲🇾", "মালয়েশিয়া"),
        Pair("🇸🇦", "সৌদি আরব"),
        Pair("🇦🇪", "সংযুক্ত আরব আমিরাত")
    )
}
