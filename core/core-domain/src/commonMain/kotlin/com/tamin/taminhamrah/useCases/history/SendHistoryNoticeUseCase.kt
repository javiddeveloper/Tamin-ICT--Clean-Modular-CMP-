package com.tamin.taminhamrah.useCases.history

import com.tamin.taminhamrah.repository.HistoryRepository

class SendHistoryNoticeUseCase(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke(): String? = repository.sendHistoryNotice()
}
