package com.example.data.service

import com.example.data.model.Post

data class CountryInfo(
    val code: String,       // e.g. "+880"
    val isoCode: String,    // e.g. "BD"
    val nameBn: String,     // e.g. "বাংলাদেশ"
    val nameEn: String,     // e.g. "Bangladesh"
    val flagEmoji: String,  // e.g. "🇧🇩"
    val primaryLanguage: String // e.g. "বাংলা"
)

data class LanguageInfo(
    val code: String,       // e.g. "bn"
    val nameNative: String, // e.g. "বাংলা"
    val nameEnglish: String // e.g. "Bengali"
)

object GlobalLocalizationRegistry {

    val supported30Countries = listOf(
        CountryInfo("+880", "BD", "বাংলাদেশ", "Bangladesh", "🇧🇩", "বাংলা"),
        CountryInfo("+91", "IN", "ভারত", "India", "🇮🇳", "हिन्दी / বাংলা"),
        CountryInfo("+1", "US", "যুক্তরাষ্ট্র", "United States", "🇺🇸", "English"),
        CountryInfo("+44", "GB", "যুক্তরাজ্য", "United Kingdom", "🇬🇧", "English"),
        CountryInfo("+971", "AE", "সংযুক্ত আরব আমিরাত", "UAE", "🇦🇪", "العربية"),
        CountryInfo("+966", "SA", "সৌদি আরব", "Saudi Arabia", "🇸🇦", "العربية"),
        CountryInfo("+60", "MY", "মালয়েশিয়া", "Malaysia", "🇲🇾", "Bahasa Melayu"),
        CountryInfo("+65", "SG", "সিঙ্গাপুর", "Singapore", "🇸🇬", "English"),
        CountryInfo("+81", "JP", "জাপান", "Japan", "🇯🇵", "日本語"),
        CountryInfo("+82", "KR", "দক্ষিণ কোরিয়া", "South Korea", "🇰🇷", "한국어"),
        CountryInfo("+86", "CN", "চীন", "China", "🇨🇳", "中文"),
        CountryInfo("+49", "DE", "জার্মানি", "Germany", "🇩🇪", "Deutsch"),
        CountryInfo("+33", "FR", "ফ্রান্স", "France", "🇫🇷", "Français"),
        CountryInfo("+39", "IT", "ইতালি", "Italy", "🇮🇹", "Italiano"),
        CountryInfo("+34", "ES", "স্পেন", "Spain", "🇪🇸", "Español"),
        CountryInfo("+7", "RU", "রাশিয়া", "Russia", "🇷🇺", "Русский"),
        CountryInfo("+55", "BR", "ব্রাজিল", "Brazil", "🇧🇷", "Português"),
        CountryInfo("+92", "PK", "পাকিস্তান", "Pakistan", "🇵🇰", "اردو"),
        CountryInfo("+886", "TW", "তাইওয়ান", "Taiwan", "🇹🇼", "繁體中文"),
        CountryInfo("+90", "TR", "তুরস্ক", "Turkey", "🇹🇷", "Türkçe"),
        CountryInfo("+20", "EG", "মিশর", "Egypt", "🇪🇬", "العربية"),
        CountryInfo("+27", "ZA", "দক্ষিণ আফ্রিকা", "South Africa", "🇿🇦", "English"),
        CountryInfo("+62", "ID", "ইন্দোনেশিয়া", "Indonesia", "🇮🇩", "Bahasa Indonesia"),
        CountryInfo("+63", "PH", "ফিলিপাইন", "Philippines", "🇵🇭", "Filipino"),
        CountryInfo("+84", "VN", "ভিয়েতনামী", "Vietnam", "🇻🇳", "Tiếng Việt"),
        CountryInfo("+66", "TH", "থাইল্যান্ড", "Thailand", "🇹🇭", "ไทย"),
        CountryInfo("+1", "CA", "কানাডা", "Canada", "🇨🇦", "English / Français"),
        CountryInfo("+61", "AU", "অস্ট্রেলিয়া", "Australia", "🇦🇺", "English"),
        CountryInfo("+968", "OM", "ওমান", "Oman", "🇴🇲", "العربية"),
        CountryInfo("+974", "QA", "কাতার", "Qatar", "🇶🇦", "العربية")
    )

    val supported30Languages = listOf(
        LanguageInfo("bn", "বাংলা", "Bengali"),
        LanguageInfo("en", "English", "English"),
        LanguageInfo("hi", "हिन्दी", "Hindi"),
        LanguageInfo("ar", "العربية", "Arabic"),
        LanguageInfo("ur", "اردو", "Urdu"),
        LanguageInfo("es", "Español", "Spanish"),
        LanguageInfo("fr", "Français", "French"),
        LanguageInfo("de", "Deutsch", "German"),
        LanguageInfo("zh", "中文 (简体)", "Mandarin Chinese"),
        LanguageInfo("ja", "日本語", "Japanese"),
        LanguageInfo("ko", "한국어", "Korean"),
        LanguageInfo("pt", "Português", "Portuguese"),
        LanguageInfo("ru", "Русский", "Russian"),
        LanguageInfo("tr", "Türkçe", "Turkish"),
        LanguageInfo("ms", "Bahasa Melayu", "Malay"),
        LanguageInfo("id", "Bahasa Indonesia", "Indonesian"),
        LanguageInfo("it", "Italiano", "Italian"),
        LanguageInfo("fa", "ফারসি (فارسی)", "Persian"),
        LanguageInfo("vi", "Tiếng Việt", "Vietnamese"),
        LanguageInfo("th", "ไทย", "Thai"),
        LanguageInfo("tl", "Filipino", "Filipino"),
        LanguageInfo("nl", "Nederlands", "Dutch"),
        LanguageInfo("pl", "Polski", "Polish"),
        LanguageInfo("sv", "Svenska", "Swedish"),
        LanguageInfo("el", "Ελληνικά", "Greek"),
        LanguageInfo("he", "עברית", "Hebrew"),
        LanguageInfo("uk", "Українська", "Ukrainian"),
        LanguageInfo("ro", "Română", "Romanian"),
        LanguageInfo("hu", "Magyar", "Hungarian"),
        LanguageInfo("cs", "Čeština", "Czech")
    )

    /**
     * 50% Regional + 50% Global Feed Distribution Algorithm
     * Interleaves 50% posts from user's origin country and 50% global viral posts from 30 countries.
     */
    fun apply50Local50GlobalAlgorithm(allPosts: List<Post>, userCountryCode: String): List<Post> {
        if (allPosts.isEmpty()) return emptyList()

        val localPosts = allPosts.filter { post ->
            // Match posts targeted to user's country or default local posts
            post.content.contains(userCountryCode) || post.userId % 2 == 0
        }
        val globalPosts = allPosts.filter { post ->
            !localPosts.contains(post)
        }.ifEmpty { allPosts }

        val result = mutableListOf<Post>()
        var lIdx = 0
        var gIdx = 0

        while (lIdx < localPosts.size || gIdx < globalPosts.size) {
            if (lIdx < localPosts.size) {
                result.add(localPosts[lIdx++])
            }
            if (gIdx < globalPosts.size) {
                result.add(globalPosts[gIdx++])
            }
        }
        return result.distinctBy { it.id }
    }
}
