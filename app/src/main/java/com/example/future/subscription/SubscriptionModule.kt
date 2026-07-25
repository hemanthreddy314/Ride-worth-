package com.example.future.subscription

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class SubscriptionTier {
    FREE,
    PRO_MONTHLY,
    PRO_ANNUAL
}

interface SubscriptionManager {
    val currentTier: Flow<SubscriptionTier>
    fun isProUnlocked(): Boolean
}

class SubscriptionModulePlaceholder : SubscriptionManager {
    private val _tier = MutableStateFlow(SubscriptionTier.FREE)
    override val currentTier: Flow<SubscriptionTier> = _tier.asStateFlow()

    override fun isProUnlocked(): Boolean = false
}
