package com.tamin.taminhamrah.data.repository.common

import com.tamin.taminhamrah.dataSource.commonSource.MenuLocalDataSource
import com.tamin.taminhamrah.model.common.MainServiceDto

class FakeMenuLocalDataSource : MenuLocalDataSource {
    var menu: List<MainServiceDto> = emptyList()

    override suspend fun getOfflineMenu(): List<MainServiceDto> = menu
}
