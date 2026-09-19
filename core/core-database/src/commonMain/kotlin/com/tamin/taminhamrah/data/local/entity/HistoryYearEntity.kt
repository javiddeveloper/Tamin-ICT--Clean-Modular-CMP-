package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * One year from `talfighinfos`, cached so «کلیه سوابق» opens on what it last knew.
 *
 * The twelve-month values are stored as one delimited string rather than twelve columns or a
 * converter: they are only ever read as a set, the wire already sends them as strings, and a column
 * per month would have to be migrated every time the shape moved.
 */
@Entity(tableName = "history_years")
data class HistoryYearEntity(
    /** Jalali year — the identity of the row, as it is everywhere else in this feature. */
    @PrimaryKey val hisYear: String,
    val months: String,
    val historyYears: Int,
    val historyMonths: Int,
    val historyDays: Int,
    val sumHistoryYears: Int,
    val sumYear: Int,
    /** Keeps the server's order, which the screen preserves rather than sorting. */
    val position: Int,
)

/**
 * One employer's year from `dastmozdinfos`.
 *
 * Days and wages are kept as two delimited strings for the same reason as [HistoryYearEntity].
 */
@Entity(tableName = "history_wage_rows")
data class HistoryWageRowEntity(
    @PrimaryKey val id: Int,
    val hisYear: String,
    val months: String,
    val wages: String,
    val workshopName: String,
    val branchName: String,
    val historyTypeDesc: String,
    val workshopId: String,
    val position: Int,
)
