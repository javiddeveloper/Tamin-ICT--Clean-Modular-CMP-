package com.tamin.taminhamrah.feature.workshops.ui.model

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.workshop.BaseDocumentCategory
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.abs_doc_01
import taminx.core.core_ui.abs_doc_02
import taminx.core.core_ui.abs_doc_03
import taminx.core.core_ui.abs_doc_04
import taminx.core.core_ui.abs_doc_05
import taminx.core.core_ui.abs_doc_06
import taminx.core.core_ui.abs_doc_07
import taminx.core.core_ui.abs_doc_08
import taminx.core.core_ui.settlement_subject_image
import taminx.core.core_ui.ws_article_sixteen_doc_1
import taminx.core.core_ui.ws_article_sixteen_doc_10
import taminx.core.core_ui.ws_article_sixteen_doc_2
import taminx.core.core_ui.ws_article_sixteen_doc_3
import taminx.core.core_ui.ws_article_sixteen_doc_4
import taminx.core.core_ui.ws_article_sixteen_doc_5
import taminx.core.core_ui.ws_article_sixteen_doc_6
import taminx.core.core_ui.ws_article_sixteen_doc_7
import taminx.core.core_ui.ws_article_sixteen_doc_8
import taminx.core.core_ui.ws_article_sixteen_doc_9
import taminx.core.core_ui.ws_obj_doc_1
import taminx.core.core_ui.ws_obj_doc_10
import taminx.core.core_ui.ws_obj_doc_11
import taminx.core.core_ui.ws_obj_doc_12
import taminx.core.core_ui.ws_obj_doc_13
import taminx.core.core_ui.ws_obj_doc_14
import taminx.core.core_ui.ws_obj_doc_15
import taminx.core.core_ui.ws_obj_doc_16
import taminx.core.core_ui.ws_obj_doc_17
import taminx.core.core_ui.ws_obj_doc_18
import taminx.core.core_ui.ws_obj_doc_2
import taminx.core.core_ui.ws_obj_doc_3
import taminx.core.core_ui.ws_obj_doc_4
import taminx.core.core_ui.ws_obj_doc_5
import taminx.core.core_ui.ws_obj_doc_6
import taminx.core.core_ui.ws_obj_doc_7
import taminx.core.core_ui.ws_obj_doc_8
import taminx.core.core_ui.ws_obj_doc_9

/**
 * One kind of document a form may carry, and the code the service files it under.
 *
 * The codes are the service's own. The old app ships them as an `objection-type.json` asset and
 * reads it at runtime; here they are a table, because a code and the wording that explains it are
 * one fact and drift the moment they live in two places. The wording is a string resource, since
 * it is what the picker sheet shows.
 */
@Immutable
data class WorkshopDocumentType(val code: String, val label: StringResource)

/**
 * انواع مدرک اعتراض — what may be attached to a ثبت اعتراض به بدهی.
 *
 * The design's mock lists five generic names; these are the eighteen the service actually
 * accepts, taken from the old app's asset.
 */
val ObjectionDocumentTypes: ImmutableList<WorkshopDocumentType> = persistentListOf(
    WorkshopDocumentType("1", Res.string.ws_obj_doc_1),
    WorkshopDocumentType("2", Res.string.ws_obj_doc_2),
    WorkshopDocumentType("3", Res.string.ws_obj_doc_3),
    WorkshopDocumentType("4", Res.string.ws_obj_doc_4),
    WorkshopDocumentType("5", Res.string.ws_obj_doc_5),
    WorkshopDocumentType("6", Res.string.ws_obj_doc_6),
    WorkshopDocumentType("7", Res.string.ws_obj_doc_7),
    WorkshopDocumentType("8", Res.string.ws_obj_doc_8),
    WorkshopDocumentType("9", Res.string.ws_obj_doc_9),
    WorkshopDocumentType("10", Res.string.ws_obj_doc_10),
    WorkshopDocumentType("11", Res.string.ws_obj_doc_11),
    WorkshopDocumentType("12", Res.string.ws_obj_doc_12),
    WorkshopDocumentType("13", Res.string.ws_obj_doc_13),
    WorkshopDocumentType("14", Res.string.ws_obj_doc_14),
    WorkshopDocumentType("15", Res.string.ws_obj_doc_15),
    WorkshopDocumentType("16", Res.string.ws_obj_doc_16),
    WorkshopDocumentType("17", Res.string.ws_obj_doc_17),
    WorkshopDocumentType("18", Res.string.ws_obj_doc_18),
)

/**
 * انواع رسیدگی ماده ۱۶ — what may be attached to a درخواست رسیدگی به بدهی.
 *
 * The same asset's `investigationItems`: the grounds a ماده ۱۶ review can be asked for, which is
 * what the old app files this request's documents under.
 */
val ArticleSixteenDocumentTypes: ImmutableList<WorkshopDocumentType> = persistentListOf(
    WorkshopDocumentType("1", Res.string.ws_article_sixteen_doc_1),
    WorkshopDocumentType("2", Res.string.ws_article_sixteen_doc_2),
    WorkshopDocumentType("3", Res.string.ws_article_sixteen_doc_3),
    WorkshopDocumentType("4", Res.string.ws_article_sixteen_doc_4),
    WorkshopDocumentType("5", Res.string.ws_article_sixteen_doc_5),
    WorkshopDocumentType("6", Res.string.ws_article_sixteen_doc_6),
    WorkshopDocumentType("7", Res.string.ws_article_sixteen_doc_7),
    WorkshopDocumentType("8", Res.string.ws_article_sixteen_doc_8),
    WorkshopDocumentType("9", Res.string.ws_article_sixteen_doc_9),
    WorkshopDocumentType("10", Res.string.ws_article_sixteen_doc_10),
)

/** The upload ceiling each form enforces — the add control disappears at this many files. */
const val OBJECTION_MAX_DOCUMENTS = 5

/**
 * انواع تصویر نام‌نویسی — what a نام‌نویسی غیرحضوری registration attaches.
 *
 * The order is the design's and the old app's; the codes are the service's, and the two do not
 * agree — «تصویر دوم اظهارنامه» is third in the list but code `08`. Listing them as one table
 * keeps that mismatch stated rather than re-derived, which is how it survives an edit.
 */
val RegistrationDocumentTypes: ImmutableList<WorkshopDocumentType> = persistentListOf(
    WorkshopDocumentType("01", Res.string.abs_doc_01),
    WorkshopDocumentType("02", Res.string.abs_doc_02),
    WorkshopDocumentType("08", Res.string.abs_doc_08),
    WorkshopDocumentType("03", Res.string.abs_doc_03),
    WorkshopDocumentType("04", Res.string.abs_doc_04),
    WorkshopDocumentType("07", Res.string.abs_doc_07),
    WorkshopDocumentType("05", Res.string.abs_doc_05),
    WorkshopDocumentType("06", Res.string.abs_doc_06),
)

/** The upload ceiling نام‌نویسی enforces — one image per type. */
const val REGISTRATION_MAX_DOCUMENTS = 8

/**
 * انواع مستندات مفاصاحساب — what a درخواست مفاصاحساب files its documents under, in the old app's order.
 *
 * The codes and headings are [BaseDocumentCategory]'s: a request files under exactly the four headings
 * a filed مبنا later lists its documents by, so the two are one table. «پیمانکاری فرعی» (`2`) is only
 * offered once the پیمانکار says subcontractors were used — [SettlementDocumentTypes] is the list
 * without it.
 */
val SettlementDocumentTypesWithSubcontractor: ImmutableList<WorkshopDocumentType> = listOf(
    BaseDocumentCategory.LETTER,
    BaseDocumentCategory.SUPPLEMENT,
    BaseDocumentCategory.SUBCONTRACTOR,
    BaseDocumentCategory.FINAL_STATUS,
).map { WorkshopDocumentType(it.code, it.title) }.toImmutableList()

val SettlementDocumentTypes: ImmutableList<WorkshopDocumentType> =
    SettlementDocumentTypesWithSubcontractor
        .filterNot { it.code == BaseDocumentCategory.SUBCONTRACTOR.code }
        .toImmutableList()

/**
 * The ceiling درخواست مفاصاحساب enforces. The old app set none; the upload box needs one, and ten is
 * more than two files under each of its four headings.
 */
const val SETTLEMENT_MAX_DOCUMENTS = 10

/** The conditions' own image (subjects 01 and 29) — one file, with nothing to ask about its type. */
val SettlementSubjectImageTypes: ImmutableList<WorkshopDocumentType> = persistentListOf(
    WorkshopDocumentType("subject", Res.string.settlement_subject_image),
)
