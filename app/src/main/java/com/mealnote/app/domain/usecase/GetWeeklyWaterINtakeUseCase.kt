package com.mealnote.app.domain.usecase

import com.mealnote.app.data.repository.WaterRepository
import kotlinx.coroutines.flow.Flow

class GetWeeklyWaterIntakeUseCase(
    private val waterRepository: WaterRepository
) {
    operator fun invoke(): Flow<List<Int>> {
        return waterRepository.getDailyTotalsForWeek()
    }
}