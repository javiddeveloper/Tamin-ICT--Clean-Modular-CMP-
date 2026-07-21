package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param

data class Wage(
    val year: Int,
    val monthsWage: MutableMap<Int, Long>
)