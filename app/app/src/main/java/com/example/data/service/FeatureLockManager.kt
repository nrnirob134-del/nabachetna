package com.example.data.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FeatureLockState(
    val isLocked: Boolean = false,
    val noticeMessage: String = "সার্ভার লোড নিয়ন্ত্রণের স্বার্থে এই ফিচারটি সাময়িকভাবে বন্ধ রাখা হয়েছে।"
)

object FeatureLockManager {
    // Features: "marketplace", "pages", "groups", "voice_room", "video_meet", "stories"
    private val _featureLocks = MutableStateFlow<Map<String, FeatureLockState>>(emptyMap())
    val featureLocks: StateFlow<Map<String, FeatureLockState>> = _featureLocks.asStateFlow()

    fun setFeatureLock(featureKey: String, isLocked: Boolean, notice: String) {
        val current = _featureLocks.value.toMutableMap()
        current[featureKey] = FeatureLockState(isLocked = isLocked, noticeMessage = notice)
        _featureLocks.value = current
    }

    fun isLocked(featureKey: String): Boolean {
        return _featureLocks.value[featureKey]?.isLocked == true
    }

    fun getNotice(featureKey: String): String {
        return _featureLocks.value[featureKey]?.noticeMessage 
            ?: "সার্ভার লোড নিয়ন্ত্রণের স্বার্থে এই ফিচারটি সাময়িকভাবে বন্ধ রাখা হয়েছে।"
    }
}
