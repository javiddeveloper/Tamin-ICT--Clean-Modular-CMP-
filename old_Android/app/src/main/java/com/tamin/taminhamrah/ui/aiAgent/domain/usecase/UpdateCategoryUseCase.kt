package com.tamin.taminhamrah.ui.aiAgent.domain.usecase

import com.tamin.taminhamrah.data.repository.ai.model.AiHistoryCategory
import com.tamin.taminhamrah.ui.aiAgent.domain.repository.AiHistoryRepository
import javax.inject.Inject

class UpdateCategoryUseCase @Inject constructor(
    private val aiHistoryRepository: AiHistoryRepository
)  {

     suspend fun execute(entity: AiHistoryCategory): Boolean {
          return try {
              aiHistoryRepository.updateCategory(entity)
                true
          } catch (e : Exception) {
              false
          }
      }
}
