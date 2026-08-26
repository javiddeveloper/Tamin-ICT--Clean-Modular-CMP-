package com.tamin.taminhamrah.model.common

/** [isStale] is true when the network refresh failed and [cities] is the local cache served instead. */
data class CityListResultDN(
    val cities: List<CityDN>,
    val isStale: Boolean = false,
)
