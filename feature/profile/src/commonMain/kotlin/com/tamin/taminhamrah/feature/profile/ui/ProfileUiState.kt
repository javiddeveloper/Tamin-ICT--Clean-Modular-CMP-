package com.tamin.taminhamrah.feature.profile.ui

import com.tamin.taminhamrah.core.model.common.IdentityInfoDN

data class ProfileUiState(
    val profileImageBase64: String? = null,
    val isLoadingImage: Boolean = false,
    val imageError: String? = null,
    val isLoadingIdentity: Boolean = false,
    val identityError: String? = null,
    val identityInfo: IdentityInfoDN? = null,
)
