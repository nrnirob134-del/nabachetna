package com.example.data.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AdCampaign(
    val id: String,
    val sponsorName: String,
    val sponsorLogoEmoji: String = "📢",
    val title: String,
    val description: String,
    val mediaType: String = "IMAGE", // "IMAGE" or "VIDEO"
    val mediaUrl: String,
    val ctaText: String = "আরও জানুন",
    val targetUrl: String = "https://nabachetna.com",
    val isGoogleAdSense: Boolean = false,
    val isActive: Boolean = true,
    val impressionCount: Int = 1240,
    val clickCount: Int = 185
)

object AdManagerRegistry {

    private val defaultAds = listOf(
        AdCampaign(
            id = "ad_101",
            sponsorName = "Google AdSense",
            sponsorLogoEmoji = "🌐",
            title = "স্মার্ট অনলাইন বিজনেস গ্রোথ প্রোগ্রাম 🚀",
            description = "আপনার ব্যবসা বাড়িয়ে নিন ১০ গুণ দ্রুত। গুগল এডসেন্স রেজিস্টার্ড স্পন্সরড অফার। আজই সাইন-আপ করুন।",
            mediaType = "IMAGE",
            mediaUrl = "https://picsum.photos/seed/googleads1/800/450",
            ctaText = "ওয়েবসাইট দেখুন",
            targetUrl = "https://ads.google.com",
            isGoogleAdSense = true,
            isActive = true,
            impressionCount = 4520,
            clickCount = 620
        ),
        AdCampaign(
            id = "ad_102",
            sponsorName = "নবচেতনা প্রিমিয়াম স্পন্সর",
            sponsorLogoEmoji = "🛡️",
            title = "নবচেতনা সুপার ওয়ালেট & আনলিমিটেড ক্লাউড ব্যাকআপ",
            description = "কোনো স্প্যাম ছাড়াই নিরাপদ অভিজ্ঞতা উপভোগ করুন। ৬-বট ক্লাস্টার ফাস্ট ক্লাউড ভিআইপি প্যাকেজ চালু করুন।",
            mediaType = "IMAGE",
            mediaUrl = "https://picsum.photos/seed/nababanner/800/450",
            ctaText = "ভিআইপি চালু করুন",
            targetUrl = "https://nabachetna.com/vip",
            isGoogleAdSense = false,
            isActive = true,
            impressionCount = 3100,
            clickCount = 480
        ),
        AdCampaign(
            id = "ad_103",
            sponsorName = "Samsung Galaxy BD",
            sponsorLogoEmoji = "📱",
            title = "স্যামসাং গ্যালাক্সি আল্ট্রা সিরেজ—বিশেষ মূল্যছাড় 🎁",
            description = "অফিসিয়াল ওয়ারেন্টি সহ জিরো পারসেন্ট ইএমআই সুবিধায় কিনুন নতুন গ্যালাক্সি স্মার্টফোন। লিমিটেড স্টক!",
            mediaType = "IMAGE",
            mediaUrl = "https://picsum.photos/seed/samsungphone/800/450",
            ctaText = "এখনই কিনুন",
            targetUrl = "https://samsung.com/bd",
            isGoogleAdSense = false,
            isActive = true,
            impressionCount = 2890,
            clickCount = 310
        )
    )

    private val _adCampaigns = MutableStateFlow<List<AdCampaign>>(defaultAds)
    val adCampaigns: StateFlow<List<AdCampaign>> = _adCampaigns.asStateFlow()

    private val _adFrequencyInterval = MutableStateFlow(4) // Insert Ad every 4 posts/products
    val adFrequencyInterval: StateFlow<Int> = _adFrequencyInterval.asStateFlow()

    fun updateFrequencyInterval(newInterval: Int) {
        if (newInterval in 2..10) {
            _adFrequencyInterval.value = newInterval
        }
    }

    fun addAdCampaign(
        sponsorName: String,
        title: String,
        description: String,
        mediaType: String,
        mediaUrl: String,
        ctaText: String,
        targetUrl: String,
        isGoogleAdSense: Boolean
    ) {
        val newAd = AdCampaign(
            id = "ad_${System.currentTimeMillis()}",
            sponsorName = sponsorName.ifBlank { "স্পন্সরড এডস" },
            sponsorLogoEmoji = if (isGoogleAdSense) "🌐" else "📢",
            title = title.ifBlank { "বিশেষ স্পন্সরড অফার" },
            description = description,
            mediaType = mediaType,
            mediaUrl = mediaUrl.ifBlank { "https://picsum.photos/seed/defaultad/800/450" },
            ctaText = ctaText.ifBlank { "আরও জানুন" },
            targetUrl = targetUrl.ifBlank { "https://nabachetna.com" },
            isGoogleAdSense = isGoogleAdSense,
            isActive = true,
            impressionCount = 0,
            clickCount = 0
        )
        _adCampaigns.value = listOf(newAd) + _adCampaigns.value
    }

    fun toggleAdActiveStatus(adId: String) {
        _adCampaigns.value = _adCampaigns.value.map {
            if (it.id == adId) it.copy(isActive = !it.isActive) else it
        }
    }

    fun deleteAdCampaign(adId: String) {
        _adCampaigns.value = _adCampaigns.value.filter { it.id != adId }
    }

    fun recordClick(adId: String) {
        _adCampaigns.value = _adCampaigns.value.map {
            if (it.id == adId) it.copy(clickCount = it.clickCount + 1) else it
        }
    }

    fun recordImpression(adId: String) {
        _adCampaigns.value = _adCampaigns.value.map {
            if (it.id == adId) it.copy(impressionCount = it.impressionCount + 1) else it
        }
    }
}
