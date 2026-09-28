package com.example.data.repository

import com.example.data.model.AdminSponsoredAd
import com.example.data.model.Language
import com.example.data.model.LanguageCertificate
import com.example.data.model.UserScore

object LanguageSeedData {
    val SUPPORTED_LANGUAGES = listOf(
        Language(id = 1, name = "English (ইংরেজি)", nativeName = "English", code = "en", flagEmoji = "🇬🇧", totalLevels = 200),
        Language(id = 2, name = "Arabic (আরবি)", nativeName = "العربية", code = "ar", flagEmoji = "🇸🇦", totalLevels = 200),
        Language(id = 3, name = "Spanish (স্প্যানিশ)", nativeName = "Español", code = "es", flagEmoji = "🇪🇸", totalLevels = 200),
        Language(id = 4, name = "French (ফরাসি)", nativeName = "Français", code = "fr", flagEmoji = "🇫🇷", totalLevels = 200),
        Language(id = 5, name = "German (জার্মান)", nativeName = "Deutsch", code = "de", flagEmoji = "🇩🇪", totalLevels = 200),
        Language(id = 6, name = "Japanese (জাপানি)", nativeName = "日本語", code = "ja", flagEmoji = "🇯🇵", totalLevels = 200),
        Language(id = 7, name = "Korean (কোরিয়ান)", nativeName = "한국어", code = "ko", flagEmoji = "🇰🇷", totalLevels = 200),
        Language(id = 8, name = "Chinese (চাইনিজ)", nativeName = "中文", code = "zh", flagEmoji = "🇨🇳", totalLevels = 200),
        Language(id = 9, name = "Russian (রাশিয়ান)", nativeName = "Русский", code = "ru", flagEmoji = "🇷🇺", totalLevels = 200),
        Language(id = 10, name = "Turkish (তুর্কি)", nativeName = "Türkçe", code = "tr", flagEmoji = "🇹🇷", totalLevels = 200)
    )

    data class VocabTemplate(
        val word: String,
        val translation: String,
        val pronunciation: String = "",
        val example: String = ""
    )

    val REAL_VOCAB_MAP: Map<String, List<VocabTemplate>> = mapOf(
        "en" to listOf(
            VocabTemplate("Hello", "হ্যালো / নমস্কার", "হ্যালো", "Hello, how are you?"),
            VocabTemplate("Thank you", "ধন্যবাদ", "থ্যাঙ্ক ইউ", "Thank you very much."),
            VocabTemplate("Good morning", "শুভ সকাল", "গুড মর্নিং", "Good morning, friend."),
            VocabTemplate("Welcome", "স্বাগতম", "ওয়েলকাম", "Welcome to our class."),
            VocabTemplate("Friend", "বন্ধু", "ফ্রেন্ড", "He is my best friend."),
            VocabTemplate("Family", "পরিবার", "ফ্যামিলি", "I love my family."),
            VocabTemplate("Water", "পানি / জল", "ওয়াটার", "Drink clean water."),
            VocabTemplate("Book", "বই", "বুক", "Read a good book."),
            VocabTemplate("Success", "সাফল্য", "সাকসেস", "Hard work brings success."),
            VocabTemplate("Knowledge", "জ্ঞান", "নলেজ", "Knowledge is power.")
        ),
        "ar" to listOf(
            VocabTemplate("مرحبا (Marhaban)", "স্বাগতম / হ্যালো", "মারহাবান", "Marhaban ya sadiqi"),
            VocabTemplate("شكرا (Shukran)", "ধন্যবাদ", "শুকরান", "Shukran jazeelan"),
            VocabTemplate("صباح الخير (Sabah al-khayr)", "শুভ সকাল", "সাবাহ আল খাইর", "Sabah al-khayr ya akhi"),
            VocabTemplate("سلام (Salam)", "শান্তি / সালাম", "সালাম", "As-salamu alaykum"),
            VocabTemplate("صديق (Sadiq)", "বন্ধু", "সাদিক", "Huwa sadiqi al-mufaddal"),
            VocabTemplate("كتاب (Kitab)", "বই", "কিতাব", "Hadha kitabun jameel"),
            VocabTemplate("ماء (Ma'a)", "পানি", "মা-আ", "Ureedu ma'an baridan"),
            VocabTemplate("علم (Ilm)", "জ্ঞান / বিদ্যা", "ইলম", "Al-ilmu noor"),
            VocabTemplate("بيت (Bayt)", "ঘর / বাড়ি", "বাইত", "Hadha bayti"),
            VocabTemplate("جميل (Jameel)", "সুন্দর", "জামিল", "Hadha al-yawm jameel")
        ),
        "es" to listOf(
            VocabTemplate("Hola", "হ্যালো / হাই", "ওলা", "¡Hola! ¿Cómo estás?"),
            VocabTemplate("Gracias", "ধন্যবাদ", "গ্রাসিয়াস", "Muchas gracias por tu ayuda."),
            VocabTemplate("Buenos días", "শুভ সকাল", "বুয়েনোস দিয়াস", "Buenos días a todos."),
            VocabTemplate("Amigo", "বন্ধু", "আমিগো", "Él es mi buen amigo."),
            VocabTemplate("Familia", "পরিবার", "ফামিলিয়া", "La familia es lo primero."),
            VocabTemplate("Agua", "পানি", "আগুয়া", "Bebo mucha agua fresca."),
            VocabTemplate("Libro", "বই", "লিব্রো", "Este libro es fascinante."),
            VocabTemplate("Éxito", "সাফল্য", "এক্সিটো", "El esfuerzo trae éxito."),
            VocabTemplate("Comida", "খাবার", "কমিদা", "La comida está deliciosa."),
            VocabTemplate("Hermoso", "সুন্দর", "এরমোসো", "Un día muy hermoso.")
        ),
        "fr" to listOf(
            VocabTemplate("Bonjour", "শুভ দিন / হ্যালো", "বঁজুর্", "Bonjour tout le monde!"),
            VocabTemplate("Merci", "ধন্যবাদ", "মের্সি", "Merci beaucoup pour tout."),
            VocabTemplate("Bonsoir", "শুভ সন্ধ্যা", "বঁসোয়ার্", "Bonsoir mes chers amis."),
            VocabTemplate("Ami", "বন্ধু", "আমি", "C'est mon meilleur ami."),
            VocabTemplate("Famille", "পরিবার", "ফামিয়্য", "J'aime ma grande famille."),
            VocabTemplate("Eau", "পানি", "ও", "Un verre d'eau fraîche, s'il vous plaît."),
            VocabTemplate("Livre", "বই", "লিভ্র্", "Ce livre est passionnant."),
            VocabTemplate("Succès", "সাফল্য", "সুক্সে", "Le travail mène au succès."),
            VocabTemplate("Amour", "ভালোবাসা", "আমুর", "L'amour est la vie."),
            VocabTemplate("Magnifique", "চমৎকার / সুন্দর", "মানিফিক", "C'est absolument magnifique.")
        ),
        "de" to listOf(
            VocabTemplate("Hallo", "হ্যালো", "হালো", "Hallo, wie geht es dir?"),
            VocabTemplate("Danke", "ধন্যবাদ", "ডাঙ্কে", "Vielen Dank für Ihre Hilfe."),
            VocabTemplate("Guten Morgen", "শুভ সকাল", "গুটেন মর্গেন", "Guten Morgen zusammen."),
            VocabTemplate("Freund", "বন্ধু", "ফ্রয়ন্ড", "Er ist mein bester Freund."),
            VocabTemplate("Familie", "পরিবার", "ফামিলিয়ে", "Meine Familie ist mir wichtig."),
            VocabTemplate("Wasser", "পানি", "ভাসার", "Ein Glas frisches Wasser."),
            VocabTemplate("Buch", "বই", "বুখ", "Ich lese gerne ein Buch."),
            VocabTemplate("Erfolg", "সাফল্য", "এরফোল্গ", "Harte Arbeit bringt Erfolg."),
            VocabTemplate("Wissen", "জ্ঞান", "ভিসেন", "Wissen ist echte Macht."),
            VocabTemplate("Schön", "সুন্দর", "শোন", "Das Wetter ist heute schön.")
        ),
        "ja" to listOf(
            VocabTemplate("こんにちは (Konnichiwa)", "হ্যালো / শুভ দিন", "কন্নিচিওয়া", "Konnichiwa, genki desu ka?"),
            VocabTemplate("ありがとう (Arigatou)", "ধন্যবাদ", "আরিগাতো", "Doumo arigatou gozaimasu."),
            VocabTemplate("おはよう (Ohayou)", "শুভ সকাল", "ওহায়ো", "Ohayou gozaimasu!"),
            VocabTemplate("友達 (Tomodachi)", "বন্ধু", "তোমোদাচি", "Kare wa watashi no tomodachi desu."),
            VocabTemplate("家族 (Kazoku)", "পরিবার", "কাজোকু", "Watashi no kazoku wa taisetsu desu."),
            VocabTemplate("水 (Mizu)", "পানি", "মিজু", "Tsumetai mizu o kudasai."),
            VocabTemplate("本 (Hon)", "বই", "হোন", "Kono hon wa omoshiroi desu."),
            VocabTemplate("成功 (Seikou)", "সাফল্য", "সেইকো", "Doryoku wa seikou o motarasu."),
            VocabTemplate("知識 (Chishiki)", "জ্ঞান", "চিশিকি", "Chishiki wa chikara desu."),
            VocabTemplate("美しい (Utsukushii)", "সুন্দর", "উৎসুকুশি", "Totemo utsukushii keshiki desu.")
        ),
        "ko" to listOf(
            VocabTemplate("안녕하세요 (Annyeonghaseyo)", "হ্যালো / আদাব", "আন্নিয়ংহাসেয়ো", "Annyeonghaseyo!"),
            VocabTemplate("감사합니다 (Gamsahamnida)", "ধন্যবাদ", "খামসাহামনিদা", "Jeongmal gamsahamnida."),
            VocabTemplate("좋ুন 아침 (Joeun achim)", "শুভ সকাল", "জোহুন আচিম", "Joeun achim imnida!"),
            VocabTemplate("친구 (Chingu)", "বন্ধু", "ছিঙ্গু", "Geuneun nae chingu imnida."),
            VocabTemplate("가족 (Gajok)", "পরিবার", "খাজক", "Uli gajog-eun haengboghada."),
            VocabTemplate("물 (Mul)", "পানি", "মুল", "Mul hanjan juseyo."),
            VocabTemplate("책 (Chaek)", "বই", "ছেক", "I chaeg-eun jaemiiss-eoyo."),
            VocabTemplate("성공 (Seonggong)", "সাফল্য", "সংগং", "Noryeog-eun seonggong-eul mandeunda."),
            VocabTemplate("지혜 (Jihye)", "প্রজ্ঞা / জ্ঞান", "জিহে", "Jihyeneun sojunghada."),
            VocabTemplate("아름다운 (Areumdaun)", "সুন্দর", "আরেউমদাউন", "Areumdaun haru imnida.")
        ),
        "zh" to listOf(
            VocabTemplate("你好 (Nǐ hǎo)", "হ্যালো / নমস্কার", "নি হাও", "Nǐ hǎo, péngyǒu!"),
            VocabTemplate("谢谢 (Xièxiè)", "ধন্যবাদ", "শিয়ে শিয়ে", "Fēicháng xièxiè nǐ."),
            VocabTemplate("早上好 (Zǎoshang hǎo)", "শুভ সকাল", "যাওসাং হাও", "Dàjiā zǎoshang hǎo."),
            VocabTemplate("朋友 (Péngyǒu)", "বন্ধু", "ফেংইয়ো", "Tā shì wǒ de péngyǒu."),
            VocabTemplate("家庭 (Jiātíng)", "পরিবার", "জিয়াতিং", "Wǒ ài wǒ de jiātíng."),
            VocabTemplate("水 (Shuǐ)", "পানি", "শুই", "Qǐng gěi wǒ yī bēi shuǐ."),
            VocabTemplate("书 (Shū)", "বই", "শু", "Zhè běn shū hěn yǒu yìsi."),
            VocabTemplate("成功 (Chénggōng)", "সাফল্য", "ছেংকং", "Nǔlì huì dài lái chénggōng."),
            VocabTemplate("知识 (Zhīshì)", "জ্ঞান", "ঝিশি", "Zhīshì jiùshì lìliàng."),
            VocabTemplate("美丽 (Měilì)", "সুন্দর", "মেই লি", "Fēicháng měilì de fēngjǐng.")
        ),
        "ru" to listOf(
            VocabTemplate("Привет (Privet)", "হ্যালো / সালাম", "প্রিভিয়েত", "Privet, kak dela?"),
            VocabTemplate("Спасибо (Spasibo)", "ধন্যবাদ", "স্পাসিবা", "Bol'shoye spasibo vam."),
            VocabTemplate("Доброе утро (Dobroye utro)", "শুভ সকাল", "দব্রয়ে উত্রো", "Dobroye utro, vsem!"),
            VocabTemplate("Друг (Drug)", "বন্ধু", "দ্রুগ", "On moy luchshiy drug."),
            VocabTemplate("Семья (Sem'ya)", "পরিবার", "সেমিয়া", "Moya sem'ya ochen' druzhnaya."),
            VocabTemplate("Вода (Voda)", "পানি", "ভাদা", "Dayte, pozhaluysta, vody."),
            VocabTemplate("Книга (Kniga)", "বই", "কনিগা", "Eta kniga ochen' interesnaya."),
            VocabTemplate("Успех (Uspekh)", "সাফল্য", "উসপেক", "Uspekh prikhodit s trudom."),
            VocabTemplate("Знание (Znaniye)", "জ্ঞান", "জনানিয়ে", "Znaniye — eto sila."),
            VocabTemplate("Красивый (Krasivyy)", "সুন্দর", "ক্রাসিভিয়", "Krasivyy segodnya den'.")
        ),
        "tr" to listOf(
            VocabTemplate("Merhaba", "হ্যালো / শুভেচ্ছা", "মারহাবা", "Merhaba, nasılsınız?"),
            VocabTemplate("Teşekkürler", "ধন্যবাদ", "তেশেক্কুরলার", "Çok teşekkür ederim."),
            VocabTemplate("Günaydın", "শুভ সকাল", "গুনায়দিন", "Herkese günaydın!"),
            VocabTemplate("Arkadaş", "বন্ধু", "আরকাদাশ", "O benim en iyi arkadaşım."),
            VocabTemplate("Aile", "পরিবার", "আইলে", "Ailem benim জন্য her şeydir."),
            VocabTemplate("Su", "পানি", "সু", "Lütfen bir bardak su verin."),
            VocabTemplate("Kitap", "বই", "কিতাপ", "Bu kitap çok eğitici."),
            VocabTemplate("Başarı", "সাফল্য", "বাশারি", "Çalışmak başarı getirir."),
            VocabTemplate("Bilgi", "জ্ঞান", "বিলগি", "Bilgi en büyük güçtür."),
            VocabTemplate("Güzel", "সুন্দর", "গুজেল", "Bugün hava çok güzel.")
        )
    )

    fun getInitialLeaderboard(languageId: Int, languageName: String): List<UserScore> {
        val baseUsers = listOf(
            Triple("Tanjim Hasan", 125, "🥇 Master"),
            Triple("Sarah Jenkins", 98, "🥈 Expert"),
            Triple("Rahim Chowdhury", 74, "🥉 Advanced"),
            Triple("Nabila Sultana", 62, "⭐ Intermediate"),
            Triple("Farhan Kabir", 45, "⭐ Beginner")
        )
        return baseUsers.mapIndexed { index, user ->
            UserScore(
                id = "seed_${languageId}_${index + 1}",
                userId = "user_seed_${languageId}_${index + 1}",
                languageId = languageId,
                languageName = languageName,
                userName = user.first,
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100",
                totalPoints = user.second + (languageId * 3),
                levelsCompleted = (user.second / 5) + 1,
                rankBadge = user.third
            )
        }
    }

    fun getInitialSponsoredAds(): List<AdminSponsoredAd> {
        return listOf(
            AdminSponsoredAd(
                id = 1,
                sponsorName = "British Council & EduWings",
                sponsorLogoEmoji = "🎓",
                title = "IELTS & স্পোকেন ইংলিশ মাস্টারক্লাস - ৫০% স্কলারশিপ!",
                description = "যুক্তরাজ্য ও কানাডা উচ্চশিক্ষায় সেরা প্রস্তুতি নিন। আজই ফ্রি অ্যাসেসমেন্টে অংশ নিয়ে বিশেষ রিওয়ার্ড কোড সংগ্রহ করুন।",
                bannerImageUrl = "https://images.unsplash.com/photo-1523240795612-9a054b0db644?w=600",
                targetUrl = "https://nabachetna.com/ielts-scholarship",
                ctaText = "৫০% ছাড় উপভোগ করুন ➔",
                countdownSeconds = 5,
                rewardPointsGranted = 1,
                isActive = true,
                impressions = 240,
                clicks = 68,
                category = "LANGUAGE_REWARD",
                revenueEarnedBdt = 1200.0
            ),
            AdminSponsoredAd(
                id = 2,
                sponsorName = "Japan IT Career & Embassy Prep",
                sponsorLogoEmoji = "🗼",
                title = "জাপানে আইটি ইঞ্জিনিয়ার ও কেয়ারগিভার ভিসা প্রজেক্ট",
                description = "N5/N4 জাপানি ভাষা শিখে জাপান যাওয়ার সুবর্ণ সুযোগ। ভিসা প্রসেসিং ও জব ম্যাচিং সহ বিশেষ সাপোর্ট।",
                bannerImageUrl = "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=600",
                targetUrl = "https://nabachetna.com/japan-career",
                ctaText = "ফ্রি কাউন্সেলিং বুক করুন ➔",
                countdownSeconds = 5,
                rewardPointsGranted = 1,
                isActive = true,
                impressions = 185,
                clicks = 42,
                category = "LANGUAGE_REWARD",
                revenueEarnedBdt = 950.0
            ),
            AdminSponsoredAd(
                id = 3,
                sponsorName = "Goethe German Institute Dhaka",
                sponsorLogoEmoji = "🇩🇪",
                title = "জার্মানিতে টিউশন ফি মুক্ত উচ্চশিক্ষা ও Ausbildung",
                description = "A1 থেকে B2 লেভেলের জার্মান ভাষা শিখুন স্বল্প সময়ে। নিশ্চিত ভিসা স্পন্সরশিপ গাইডলাইন।",
                bannerImageUrl = "https://images.unsplash.com/photo-1467269204594-9661b134dd2b?w=600",
                targetUrl = "https://nabachetna.com/germany-study",
                ctaText = "সিলেবাস ডাউনলোড করুন ➔",
                countdownSeconds = 5,
                rewardPointsGranted = 1,
                isActive = true,
                impressions = 310,
                clicks = 89,
                category = "LANGUAGE_REWARD",
                revenueEarnedBdt = 1600.0
            ),
            AdminSponsoredAd(
                id = 4,
                sponsorName = "Gulf Horizon Manpower & Language",
                sponsorLogoEmoji = "🇸🇦",
                title = "সৌদি ও আরব আমিরাতে হাই-স্যালারি প্রফেশনাল জব",
                description = "আরবি কথ্য ভাষা ও প্রফেশনাল ড্রাইভিং/ইলেক্ট্রিক্যাল কাজের জন্য স্পেশাল ট্রেনিং প্যাকেজ।",
                bannerImageUrl = "https://images.unsplash.com/photo-1512453979798-5ea266f8880c?w=600",
                targetUrl = "https://nabachetna.com/middle-east-jobs",
                ctaText = "আবেদন ফরম পূরণ করুন ➔",
                countdownSeconds = 5,
                rewardPointsGranted = 1,
                isActive = true,
                impressions = 150,
                clicks = 35,
                category = "LANGUAGE_REWARD",
                revenueEarnedBdt = 750.0
            )
        )
    }

    fun getInitialCertificates(): List<LanguageCertificate> {
        return listOf(
            LanguageCertificate(
                certificateId = "NC-EN-2026-8819",
                userId = "seed_1",
                userName = "Tanjim Hasan",
                userAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100",
                languageId = 1,
                languageName = "English (ইংরেজি)",
                levelReached = 25,
                totalPoints = 125,
                issueDate = "25 Sep 2026",
                status = "VERIFIED",
                feeAmountBdt = 150.0,
                paymentMethod = "bKash",
                transactionId = "9J8K2LA190",
                qrCodePayload = "NABACHETNA-CERT-VERIFIED:NC-EN-2026-8819",
                grade = "A+ (Master)"
            ),
            LanguageCertificate(
                certificateId = "NC-AR-2026-4421",
                userId = "seed_2",
                userName = "Sarah Jenkins",
                userAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100",
                languageId = 2,
                languageName = "Arabic (আরবি)",
                levelReached = 20,
                totalPoints = 98,
                issueDate = "26 Sep 2026",
                status = "VERIFIED",
                feeAmountBdt = 150.0,
                paymentMethod = "Nagad",
                transactionId = "NGD8842191",
                qrCodePayload = "NABACHETNA-CERT-VERIFIED:NC-AR-2026-4421",
                grade = "A (Expert)"
            )
        )
    }
}
