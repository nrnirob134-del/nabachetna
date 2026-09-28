package com.example.data.service

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

data class WellbeingState(
    val continuousUsageSeconds: Long = 0L,
    val isWarningActive: Boolean = false, // True when 3h 55m (14,100s) reached
    val isLockedActive: Boolean = false,  // True when 4h (14,400s) reached
    val lockRemainingSeconds: Long = 900L // 15 mins = 900 seconds
)

object DigitalWellbeingManager {
    // 4 hours = 14,400 seconds
    const val MAX_CONTINUOUS_USAGE_SECONDS = 14400L
    // 3 hours 55 minutes = 14,100 seconds (5 minutes advance warning)
    const val WARNING_THRESHOLD_SECONDS = 14100L
    // 15 minutes lock = 900 seconds
    const val MANDATORY_LOCK_DURATION_SECONDS = 900L

    private val _wellbeingState = MutableStateFlow(WellbeingState())
    val wellbeingState: StateFlow<WellbeingState> = _wellbeingState

    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun startTracking() {
        if (timerJob?.isActive == true) return

        timerJob = scope.launch {
            while (isActive) {
                delay(1000L)
                val current = _wellbeingState.value

                if (current.isLockedActive) {
                    // During 15-minute mandatory lock countdown
                    val remaining = current.lockRemainingSeconds - 1
                    if (remaining <= 0) {
                        // Reset lock and usage tracker
                        _wellbeingState.value = WellbeingState(
                            continuousUsageSeconds = 0L,
                            isWarningActive = false,
                            isLockedActive = false,
                            lockRemainingSeconds = MANDATORY_LOCK_DURATION_SECONDS
                        )
                    } else {
                        _wellbeingState.value = current.copy(lockRemainingSeconds = remaining)
                    }
                } else {
                    // Normal app usage time tracking
                    val newUsage = current.continuousUsageSeconds + 1
                    val isWarn = newUsage >= WARNING_THRESHOLD_SECONDS
                    val isLock = newUsage >= MAX_CONTINUOUS_USAGE_SECONDS

                    _wellbeingState.value = current.copy(
                        continuousUsageSeconds = newUsage,
                        isWarningActive = isWarn,
                        isLockedActive = isLock,
                        lockRemainingSeconds = if (isLock) MANDATORY_LOCK_DURATION_SECONDS else current.lockRemainingSeconds
                    )
                }
            }
        }
    }

    fun dismissWarningBanner() {
        _wellbeingState.value = _wellbeingState.value.copy(isWarningActive = false)
    }

    // Demo/Admin Fast Forward helper for testing 4-hour limit in UI
    fun simulateUsageTime(usageSeconds: Long) {
        val isWarn = usageSeconds >= WARNING_THRESHOLD_SECONDS
        val isLock = usageSeconds >= MAX_CONTINUOUS_USAGE_SECONDS
        _wellbeingState.value = WellbeingState(
            continuousUsageSeconds = usageSeconds,
            isWarningActive = isWarn && !isLock,
            isLockedActive = isLock,
            lockRemainingSeconds = if (isLock) MANDATORY_LOCK_DURATION_SECONDS else 900L
        )
    }

    fun stopTracking() {
        timerJob?.cancel()
        timerJob = null
    }
}
