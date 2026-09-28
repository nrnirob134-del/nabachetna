package com.example.data.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AdminPaymentGatewayInfo(
    val id: String,
    val gatewayName: String,
    val isInternational: Boolean = false,
    val isEnabled: Boolean = true,
    val currency: String = "BDT",
    val supportedCurrencies: List<String> = listOf("BDT"),
    val accountNumber: String, // Public Key / Client ID / Merchant ID
    val accountType: String, // "Personal", "Merchant API", "OAuth2", "Card Processing"
    val apiKeyOrSecret: String = "",
    val webhookUrl: String = "",
    val instructions: String,
    val iconEmoji: String,
    val colorHex: Long,
    val isLiveMode: Boolean = true
)

class WalletSecurityService {

    companion object {
        private const val SECURITY_SALT = "NABACHETNA_MILITARY_GRADE_WALLET_LEDGER_SALT_2026"
    }

    private val _officialPaymentGateways = MutableStateFlow(
        listOf(
            AdminPaymentGatewayInfo(
                id = "bkash",
                gatewayName = "bKash (বিকাশ)",
                isInternational = false,
                isEnabled = true,
                currency = "BDT",
                accountNumber = "01881052993",
                accountType = "Personal / Send Money",
                instructions = "বিকাশ অ্যাপ বা *247# ডায়াল করে 'Send Money' করুন। এরপর TrxID ও প্রেরক নম্বর সাবমিট করুন।",
                iconEmoji = "🌸",
                colorHex = 0xFFD81B60
            ),
            AdminPaymentGatewayInfo(
                id = "nagad",
                gatewayName = "Nagad (নগদ)",
                isInternational = false,
                isEnabled = true,
                currency = "BDT",
                accountNumber = "01881052993",
                accountType = "Personal / Send Money",
                instructions = "নগদ অ্যাপ থেকে 'Send Money' করে প্রাপ্ত Transaction ID (TrxID) ফর্মে লিখুন।",
                iconEmoji = "🟠",
                colorHex = 0xFFFB8C00
            ),
            AdminPaymentGatewayInfo(
                id = "rocket",
                gatewayName = "Rocket (রকেট)",
                isInternational = false,
                isEnabled = true,
                currency = "BDT",
                accountNumber = "01912-345678-9",
                accountType = "Personal",
                instructions = "রকেট অ্যাপ বা *322# ডায়াল করে টাকা পাঠান ও ট্রানজেকশন আইডি প্রদান করুন।",
                iconEmoji = "🟣",
                colorHex = 0xFF8E24AA
            ),
            AdminPaymentGatewayInfo(
                id = "upay",
                gatewayName = "Upay (উপায়)",
                isInternational = false,
                isEnabled = true,
                currency = "BDT",
                accountNumber = "01612-345678",
                accountType = "Personal",
                instructions = "উপায় অ্যাপ থেকে ফান্ড ট্রান্সফার করে TrxID ও নম্বর দিন।",
                iconEmoji = "🔵",
                colorHex = 0xFF0288D1
            ),
            AdminPaymentGatewayInfo(
                id = "bank",
                gatewayName = "Bank Transfer (ব্যাংক)",
                isInternational = false,
                isEnabled = true,
                currency = "BDT",
                accountNumber = "2050-1234-5678-01 (DBBL/Islami Bank)",
                accountType = "Current Account / BEFTN",
                instructions = "অনলাইন ব্যাংকিং বা ব্রাঞ্চ ডিপোজিট করে ডিপোজিট স্লিপ নম্বর বা রেফারেন্স দিন।",
                iconEmoji = "🏦",
                colorHex = 0xFF2E7D32
            ),
            // International Gateways (Dynamic - Can be switched without app updates)
            AdminPaymentGatewayInfo(
                id = "stripe",
                gatewayName = "Stripe Global (Visa/Mastercard/Apple Pay)",
                isInternational = true,
                isEnabled = true,
                currency = "USD",
                supportedCurrencies = listOf("USD", "EUR", "GBP", "CAD", "AUD"),
                accountNumber = "pk_live_51Mxb8...90Ak2",
                accountType = "Direct Card & Apple Pay",
                apiKeyOrSecret = "sk_live_99xAbC...",
                webhookUrl = "https://api.nobocetona.com/v1/webhook/stripe",
                instructions = "আন্তর্জাতিক ভিসা, মাস্টারকার্ড বা অ্যাপল পে দিয়ে ১-ক্লিকে তাৎক্ষণিক ডলার বা আন্তর্জাতিক কারেন্সিতে পেমেন্ট সম্পন্ন করুন।",
                iconEmoji = "💳",
                colorHex = 0xFF635BFF
            ),
            AdminPaymentGatewayInfo(
                id = "paypal",
                gatewayName = "PayPal International",
                isInternational = true,
                isEnabled = true,
                currency = "USD",
                supportedCurrencies = listOf("USD", "EUR", "GBP"),
                accountNumber = "client_id_live_nab_paypal_99",
                accountType = "PayPal Express Checkout",
                apiKeyOrSecret = "secret_live_paypal_88",
                webhookUrl = "https://api.nobocetona.com/v1/webhook/paypal",
                instructions = "পেপাল অ্যাকাউন্ট বা পেপাল ব্যালেন্স দিয়ে বিশ্বব্যাপী যে কোনো দেশ থেকে পেমেন্ট করুন।",
                iconEmoji = "🅿️",
                colorHex = 0xFF003087
            ),
            AdminPaymentGatewayInfo(
                id = "sslcommerz",
                gatewayName = "SSLCommerz Global Gateway",
                isInternational = true,
                isEnabled = true,
                currency = "BDT",
                supportedCurrencies = listOf("BDT", "USD"),
                accountNumber = "nobocetona_merchant_live",
                accountType = "All Cards, MFS & Net Banking",
                apiKeyOrSecret = "ssl_secret_key_888",
                webhookUrl = "https://api.nobocetona.com/v1/webhook/sslcommerz",
                instructions = "যেকোনো আন্তর্জাতিক ও দেশীয় কার্ড (Visa, Mastercard, Amex, Nexus) দিয়ে পেমেন্ট করুন।",
                iconEmoji = "🌐",
                colorHex = 0xFF1565C0
            ),
            AdminPaymentGatewayInfo(
                id = "crypto",
                gatewayName = "Binance Pay / Crypto (USDT/BTC)",
                isInternational = true,
                isEnabled = true,
                currency = "USDT",
                supportedCurrencies = listOf("USDT", "BTC", "ETH"),
                accountNumber = "binance_merchant_id_77019",
                accountType = "Crypto Web3 / TRC20",
                apiKeyOrSecret = "binance_api_key_live",
                webhookUrl = "https://api.nobocetona.com/v1/webhook/binance",
                instructions = "বাইনান্স পে বা ক্রিপ্টো ওয়ালেট দিয়ে সরাসরি কিউআর কোড স্ক্যান করে পেমেন্ট করুন।",
                iconEmoji = "🪙",
                colorHex = 0xFFF0B90B
            )
        )
    )
    val officialPaymentGateways: StateFlow<List<AdminPaymentGatewayInfo>> = _officialPaymentGateways.asStateFlow()

    fun toggleGatewayEnabled(id: String) {
        _officialPaymentGateways.value = _officialPaymentGateways.value.map {
            if (it.id == id) it.copy(isEnabled = !it.isEnabled) else it
        }
    }

    fun updateGatewayConfig(id: String, accountNumber: String, apiKey: String, isLive: Boolean) {
        _officialPaymentGateways.value = _officialPaymentGateways.value.map {
            if (it.id == id) it.copy(accountNumber = accountNumber, apiKeyOrSecret = apiKey, isLiveMode = isLive) else it
        }
    }

    fun switchGatewayOperatingMode(mode: String) {
        // "LOCAL_ONLY", "INTERNATIONAL_ONLY", "HYBRID"
        _officialPaymentGateways.value = _officialPaymentGateways.value.map { gateway ->
            when (mode) {
                "LOCAL_ONLY" -> gateway.copy(isEnabled = !gateway.isInternational)
                "INTERNATIONAL_ONLY" -> gateway.copy(isEnabled = gateway.isInternational)
                else -> gateway.copy(isEnabled = true)
            }
        }
    }

    /**
     * Creates an immutable SHA-256 Anti-Tamper Checksum for every recharge request
     */
    fun generateRechargeChecksum(
        userId: Int,
        amount: Double,
        trxId: String,
        senderPhone: String,
        timestamp: String
    ): String {
        val raw = "$userId|$amount|${trxId.trim().uppercase(Locale.ROOT)}|${senderPhone.trim()}|$timestamp|$SECURITY_SALT"
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(raw.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    /**
     * Verifies that the recharge request has NOT been modified by any local memory tamper/hacker tool
     */
    fun verifyChecksumIntegrity(
        userId: Int,
        amount: Double,
        trxId: String,
        senderPhone: String,
        timestamp: String,
        claimedChecksum: String
    ): Boolean {
        val expected = generateRechargeChecksum(userId, amount, trxId, senderPhone, timestamp)
        return expected.equals(claimedChecksum, ignoreCase = true)
    }
}
