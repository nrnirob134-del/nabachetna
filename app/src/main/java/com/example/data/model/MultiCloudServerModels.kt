package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class CloudEngineType(
    val title: String,
    val banglaTitle: String,
    val provider: String,
    val freeTierDetails: String,
    val primaryRole: String,
    val brandColor: Color
) {
    TELEGRAM_CLUSTER(
        title = "Telegram Bot Storage Cluster",
        banglaTitle = "টেলিগ্রাম ৬-বট ক্লাস্টার",
        provider = "Telegram Cloud API",
        freeTierDetails = "১০০% আজীবন ফ্রি আনলিমিটেড স্টোরেজ (৬টি বট নোড)",
        primaryRole = "ছবি, রিলস, হাই-রেজুলেশন ভিডিও ও মিডিয়া সিডিএন",
        brandColor = Color(0xFF0088CC)
    ),
    CLOUDFLARE_EDGE(
        title = "Cloudflare Workers & R2 Edge",
        banglaTitle = "ক্লাউডফ্লেয়ার এজ ও R2 প্রক্সি",
        provider = "Cloudflare Inc.",
        freeTierDetails = "১০০,০০০ রিকোয়েস্ট/দিন ফ্রি, জিরো-এগ্রেস ক্যাশিং",
        primaryRole = "গ্লোবাল এজ ক্যাশিং, DDoS সুরক্ষা ও সুপারফাস্ট রাউটিং",
        brandColor = Color(0xFFF38020)
    ),
    SUPABASE_POSTGRES(
        title = "Supabase PostgreSQL & Realtime",
        banglaTitle = "সুপাবেস রিয়েলটাইম ও SQL",
        provider = "Supabase Cloud",
        freeTierDetails = "৫০০ MB ডেটাবেস, রিয়েলটাইম ব্রডকাস্ট ও এজ ফাংশন ফ্রি",
        primaryRole = "পোস্ট ব্যাকআপ, লাইভ চ্যাট ব্রডকাস্ট ও সিকিউর কোয়েরি",
        brandColor = Color(0xFF3ECF8E)
    ),
    APPWRITE_BAAS(
        title = "Appwrite Cloud Backend",
        banglaTitle = "অ্যাপরাইট মাইক্রোসার্ভিস BaaS",
        provider = "Appwrite Cloud",
        freeTierDetails = "২ GB ফ্রি ফাইল বাকেট ও ইউজার সেশন ম্যানেজমেন্ট",
        primaryRole = "ফাইল আর্চিভ, ইউজার সেশন ও অফলাইন সিঙ্ক কিউ",
        brandColor = Color(0xFFFD366E)
    ),
    GITHUB_JSDELIVR(
        title = "GitHub Raw & jsDelivr CDN",
        banglaTitle = "গিটহাব / jsDelivr সিডিএন",
        provider = "GitHub + jsDelivr",
        freeTierDetails = "আনলিমিটেড ফ্রি গ্লোবাল ওপেন-সোর্স CDN ও স্ন্যাপশট",
        primaryRole = "কনফিগারেশন ব্যাকআপ, স্ট্যাটিক অ্যাসেট ও স্ন্যাপশট সিঙ্ক",
        brandColor = Color(0xFF24292E)
    )
}

enum class ServerNodeStatus(val label: String, val badgeColor: Color) {
    ACTIVE_OPTIMAL("সক্রিয় ও দ্রুততম 🟢", Color(0xFF2E7D32)),
    ACTIVE_NORMAL("সক্রিয় 🔵", Color(0xFF1976D2)),
    THROTTLED_COOLDOWN("কোল্ডাউন/লিমিট 🟠", Color(0xFFF57C00)),
    SYNCING("ডেটা সিঙ্ক চলছে 🔄", Color(0xFF7B1FA2)),
    STANDBY("স্ট্যান্ডবাই 🟡", Color(0xFFFBC02D)),
    OFFLINE("অফলাইন 🔴", Color(0xFFD32F2F))
}

data class FreeCloudEngineNode(
    val engineType: CloudEngineType,
    val name: String,
    val endpointOrIdentifier: String,
    val assignedFunction: String,
    val status: ServerNodeStatus = ServerNodeStatus.ACTIVE_OPTIMAL,
    val isEnabled: Boolean = true,
    val latencyMs: Long = 35L,
    val uptimePercent: Double = 99.98,
    val freeQuotaUsedText: String,
    val freeQuotaLimitText: String,
    val bandwidthUsedFormatted: String,
    val costSavedUsd: Double,
    val healthScore: Int = 100,
    val lastSyncTimestamp: String = "এইমাত্র"
)

data class MultiCloudSystemConfig(
    val isMultiCloudActive: Boolean = true,
    val primaryStorageEngine: CloudEngineType = CloudEngineType.TELEGRAM_CLUSTER,
    val primaryEdgeCdnEngine: CloudEngineType = CloudEngineType.CLOUDFLARE_EDGE,
    val primaryBackupEngine: CloudEngineType = CloudEngineType.SUPABASE_POSTGRES,
    val autoFailoverEnabled: Boolean = true,
    val multiMirrorSyncEnabled: Boolean = true, // Copies media/posts across multiple free engines
    val smartTrafficRouting: Boolean = true,
    val totalFreeEnginesConnected: Int = 5,
    val totalCombinedFreeStorageGb: Double = 1000.0, // Practically unlimited via Telegram + GitHub + Supabase + Appwrite
    val totalCombinedBandwidthTb: Double = 3.42,
    val totalEstimatedMonthlySavingsUsd: Double = 248.50
)

data class MultiCloudPingResult(
    val engineType: CloudEngineType,
    val pingMs: Long,
    val isSuccess: Boolean,
    val message: String,
    val timestamp: String
)
