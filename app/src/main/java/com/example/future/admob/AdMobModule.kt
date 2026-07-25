package com.example.future.admob

/**
 * Interface contract for future AdMob monetization support.
 */
interface AdMobManager {
    fun isAdMobInitialized(): Boolean
    fun showInterstitialAd(onAdClosed: () -> Unit)
}

class AdMobModulePlaceholder : AdMobManager {
    override fun isAdMobInitialized(): Boolean = false
    override fun showInterstitialAd(onAdClosed: () -> Unit) {
        // Ads disabled in current build
        onAdClosed()
    }
}
