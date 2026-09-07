package com.tamin.taminhamrah.model.campaign

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.ui.theme.CampaignFreelanceEnd
import com.tamin.taminhamrah.ui.theme.CampaignFreelanceMid
import com.tamin.taminhamrah.ui.theme.CampaignFreelanceShadow
import com.tamin.taminhamrah.ui.theme.CampaignFreelanceStart
import com.tamin.taminhamrah.ui.theme.CampaignHousewifeEnd
import com.tamin.taminhamrah.ui.theme.CampaignHousewifeMid
import com.tamin.taminhamrah.ui.theme.CampaignHousewifeShadow
import com.tamin.taminhamrah.ui.theme.CampaignHousewifeStart
import com.tamin.taminhamrah.ui.theme.CampaignStudentEnd
import com.tamin.taminhamrah.ui.theme.CampaignStudentMid
import com.tamin.taminhamrah.ui.theme.CampaignStudentShadow
import com.tamin.taminhamrah.ui.theme.CampaignStudentStart

/**
 * The campaign catalogue: one row per promo card on the home page.
 *
 * A single table on purpose. A card's gradient, the shadow it casts, the dark tone its white CTA
 * pill prints in, how wide its body copy runs and where tapping it goes all vary by the same key,
 * so they are declared together — parallel tables drift the first time one of them is edited.
 *
 * **Declaration order is the order the carousel shows them in.** That order is the design's; it is
 * neither alphabetical nor by [FeatureFlag] id.
 *
 * All three destinations already exist and are already routed by
 * `FeatureNavigation.navigateToFeature`. Nothing here decides whether a card is reachable — that is
 * the server's answer, read through `FeatureManager` before the card is ever built.
 */
enum class CampaignKind(
    val flag: FeatureFlag,
    val gradientStart: Color,
    val gradientMid: Color,
    /** Where [gradientMid] sits along the gradient line. The design sets it per card. */
    val gradientMidStop: Float,
    val gradientEnd: Color,
    val shadowTint: Color,
    val ctaTone: Color,
    /**
     * How much of the card width the body copy runs to. The design gives the first card 92% and
     * the other two 82%; reproduced rather than normalized.
     */
    val bodyWidthFraction: Float,
) {
    HOUSEWIFE(
        flag = FeatureFlag.HOUSEWIFE_INSURANCE,
        gradientStart = CampaignHousewifeStart,
        gradientMid = CampaignHousewifeMid,
        gradientMidStop = 0.60f,
        gradientEnd = CampaignHousewifeEnd,
        shadowTint = CampaignHousewifeShadow,
        ctaTone = CampaignHousewifeStart,
        bodyWidthFraction = 0.92f,
    ),
    FREELANCE(
        flag = FeatureFlag.FREELANCE_INSURANCE,
        gradientStart = CampaignFreelanceStart,
        gradientMid = CampaignFreelanceMid,
        gradientMidStop = 0.55f,
        gradientEnd = CampaignFreelanceEnd,
        shadowTint = CampaignFreelanceShadow,
        ctaTone = CampaignFreelanceStart,
        bodyWidthFraction = 0.82f,
    ),
    STUDENT(
        flag = FeatureFlag.STUDENT_INSURANCE,
        gradientStart = CampaignStudentStart,
        gradientMid = CampaignStudentMid,
        gradientMidStop = 0.58f,
        gradientEnd = CampaignStudentEnd,
        shadowTint = CampaignStudentShadow,
        ctaTone = CampaignStudentStart,
        bodyWidthFraction = 0.82f,
    ),
}

/**
 * One rendered campaign card: [kind] carries the look and the destination, the rest is the copy as
 * the card prints it.
 *
 * The copy is held as plain [String] rather than as string resources so that this model does not
 * change shape when campaigns start arriving from a web service. Today
 * `CampaignKind.toPresentation()` fills it in from `strings.xml`; when the endpoint exists, the
 * ViewModel fills it in from the wire instead and nothing below this model moves.
 */
@Immutable
data class CampaignPR(
    val kind: CampaignKind,
    val badge: String,
    val title: String,
    val body: String,
    val ctaLabel: String,
    val caption: String,
)
