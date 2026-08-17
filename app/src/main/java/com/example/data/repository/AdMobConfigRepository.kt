package com.example.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AdMobConfig(
    val appId: String = "ca-app-pub-3940256099942544~3347511713", // Test App ID
    val bannerAdUnitId: String = "ca-app-pub-3940256099942544/6300978111", // Test Banner
    val interstitialAdUnitId: String = "ca-app-pub-3940256099942544/1033173712", // Test Interstitial
    val nativeAdUnitId: String = "ca-app-pub-3940256099942544/2247696110", // Test Native
    val isTestMode: Boolean = true,
    val isAdsEnabled: Boolean = true,
    val interstitialFrequencyClicks: Int = 5 // Show interstitial only after 5 screen transitions
)

class AdMobConfigRepository {
    private val _config = MutableStateFlow(AdMobConfig())
    val config: StateFlow<AdMobConfig> = _config.asStateFlow()

    private var clickCounter = 0

    fun shouldShowInterstitial(): Boolean {
        if (!_config.value.isAdsEnabled) return false
        clickCounter++
        if (clickCounter >= _config.value.interstitialFrequencyClicks) {
            clickCounter = 0
            return true
        }
        return false
    }

    fun updateProductionIds(bannerId: String, interstitialId: String, nativeId: String) {
        _config.value = _config.value.copy(
            bannerAdUnitId = bannerId.ifBlank { "ca-app-pub-3940256099942544/6300978111" },
            interstitialAdUnitId = interstitialId.ifBlank { "ca-app-pub-3940256099942544/1033173712" },
            nativeAdUnitId = nativeId.ifBlank { "ca-app-pub-3940256099942544/2247696110" },
            isTestMode = false
        )
    }

    fun toggleAdsEnabled(enabled: Boolean) {
        _config.value = _config.value.copy(isAdsEnabled = enabled)
    }
}
