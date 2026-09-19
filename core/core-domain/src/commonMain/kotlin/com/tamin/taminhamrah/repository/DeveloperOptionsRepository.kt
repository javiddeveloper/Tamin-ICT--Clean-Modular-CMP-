package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.BaseUrlKey
import com.tamin.taminhamrah.model.payment.PaymentMockMode
import kotlinx.coroutines.flow.Flow

interface DeveloperOptionsRepository {
    fun getEffectiveBaseUrl(key: BaseUrlKey): String
    fun observeOverrides(): Flow<Map<BaseUrlKey, String>>
    fun setOverride(key: BaseUrlKey, url: String)
    fun clearOverride(key: BaseUrlKey)

    /**
     * Whether the payment gateway is being stood in for, and with which answer.
     *
     * Always [PaymentMockMode.DISABLED] in a release build, whatever is stored.
     */
    fun getPaymentMockMode(): PaymentMockMode

    fun observePaymentMockMode(): Flow<PaymentMockMode>

    fun setPaymentMockMode(mode: PaymentMockMode)
}
