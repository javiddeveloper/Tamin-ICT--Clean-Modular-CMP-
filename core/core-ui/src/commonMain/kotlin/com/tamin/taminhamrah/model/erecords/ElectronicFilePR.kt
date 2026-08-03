package com.tamin.taminhamrah.model.erecords

import androidx.compose.runtime.Immutable

/**
 * One document held for the insured person.
 *
 * Every field is non-null: the endpoint returns nulls freely and a card has nowhere to put one.
 * [thumb] is the URL of the small preview and also the seed for the full document's URL — see
 * `DocumentTarget`.
 */
@Immutable
data class ElectronicFilePR(
    val id: String,
    val name: String,
    val categoryName: String,
    val thumb: String,
    val type: String,
)
