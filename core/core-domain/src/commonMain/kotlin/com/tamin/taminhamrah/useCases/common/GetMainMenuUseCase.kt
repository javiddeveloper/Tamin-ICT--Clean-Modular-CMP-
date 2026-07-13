package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.repository.common.CommonRepository
import kotlinx.coroutines.flow.Flow

class GetMainMenuUseCase(
    private val commonRepository: CommonRepository
) {
    operator fun invoke(versionCode: String, forceUpdate: Boolean): Flow<List<MainServiceDN>> {
        return commonRepository.getMainMenu(versionCode, forceUpdate)
    }
}
