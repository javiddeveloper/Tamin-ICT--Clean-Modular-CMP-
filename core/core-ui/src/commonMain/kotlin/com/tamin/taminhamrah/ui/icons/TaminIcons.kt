package com.tamin.taminhamrah.ui.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.ic_tamin_health_profile
import taminx.core.core_ui.ic_tamin_medical_approvals
import taminx.core.core_ui.ic_tamin_medical_centers
import taminx.core.core_ui.ic_tamin_medical_records
import taminx.core.core_ui.ic_tamin_misc_claims
import taminx.core.core_ui.ic_tamin_prescriptions
import taminx.core.core_ui.ic_tamin_print
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.ic_tamin_verified

/**
 * The app's icon set, taken verbatim from the Tamin Man App design rather than
 * approximated with Material equivalents.
 *
 * Each glyph is a vector drawable under `composeResources/drawable/ic_tamin_*.xml`, so it
 * renders in the IDE's asset preview before anyone writes a line of code against it. The
 * XML carries the design's original path data unchanged.
 *
 * They are stroke-only outlines on a 24x24 viewport; [androidx.compose.material3.Icon]
 * recolours them via its `tint`, so the stroke colour in the XML is irrelevant.
 *
 * The chevrons set `android:autoMirrored="true"` and are declared in their left-to-right
 * form, so they flip automatically under a right-to-left layout.
 *
 * Every entry is a composable property because resource loading needs composition — use
 * them anywhere inside a `@Composable`. Add new glyphs by dropping the XML alongside the
 * others and declaring one more property here, so every module draws from the same set.
 */
object TaminIcons {

    /** Search affordance. */
    val Search: ImageVector
        @Composable get() = vectorResource(Res.drawable.ic_tamin_search)

    /** "سوابق درمانی من" — a document with lines. */
    val MedicalRecords: ImageVector
        @Composable get() = vectorResource(Res.drawable.ic_tamin_medical_records)

    /** "پروندهٔ سلامت من" — a heart with a medical cross. */
    val HealthProfile: ImageVector
        @Composable get() = vectorResource(Res.drawable.ic_tamin_health_profile)

    /** "مراکز درمانی طرف قرارداد" — a map pin. */
    val MedicalCenters: ImageVector
        @Composable get() = vectorResource(Res.drawable.ic_tamin_medical_centers)

    /** "نسخه‌های الکترونیک" — a rounded card with lines. */
    val Prescriptions: ImageVector
        @Composable get() = vectorResource(Res.drawable.ic_tamin_prescriptions)

    /** "تاییدیه‌های پزشکی" — a shield with a tick. */
    val MedicalApprovals: ImageVector
        @Composable get() = vectorResource(Res.drawable.ic_tamin_medical_approvals)

    /** "خسارت متفرقه" — a bank card. */
    val MiscClaims: ImageVector
        @Composable get() = vectorResource(Res.drawable.ic_tamin_misc_claims)

    /** Chevron pointing onward into a destination; mirrors to point left under RTL. */
    val ChevronForward: ImageVector
        @Composable get() = vectorResource(Res.drawable.ic_tamin_chevron_forward)

    /** Chevron returning to the previous screen; mirrors to point right under RTL. */
    val ChevronBack: ImageVector
        @Composable get() = vectorResource(Res.drawable.ic_tamin_chevron_back)

    /** Single tick. */
    val Check: ImageVector
        @Composable get() = vectorResource(Res.drawable.ic_tamin_check)

    /** Double tick, marking an active entitlement. */
    val Verified: ImageVector
        @Composable get() = vectorResource(Res.drawable.ic_tamin_verified)

    /** Cross, marking an absent entitlement. */
    val Cross: ImageVector
        @Composable get() = vectorResource(Res.drawable.ic_tamin_cross)

    /** Print / share action. */
    val Print: ImageVector
        @Composable get() = vectorResource(Res.drawable.ic_tamin_print)
}
