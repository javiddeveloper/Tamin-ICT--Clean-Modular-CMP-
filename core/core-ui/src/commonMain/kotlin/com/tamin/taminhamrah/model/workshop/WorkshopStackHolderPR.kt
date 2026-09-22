package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.stackholder_role_board_member
import taminx.core.core_ui.stackholder_role_ceo
import taminx.core.core_ui.stackholder_role_representative
import taminx.core.core_ui.stackholder_role_signatory

/** One ذینفع row. */
@Immutable
data class WorkshopStackHolderPR(
    val nationalId: String = "",
    val fullName: String = "",
    val fatherName: String = "",
    val birthDate: String = "",
    /** The role the service coded, or null for a code outside [StakeHolderRole] — see [stackType]. */
    val role: StakeHolderRole? = null,
    /**
     * The service's own `stackType`, dashed when blank. Shown only when [role] is null: the old app
     * prints an unknown code as it came rather than hiding it.
     */
    val stackType: String = "",
)

/**
 * A ذینفع's role, keyed by the `stackType` code the service sends.
 *
 * One table: the code and its wording are columns of the same row. The four codes and their wording
 * are the old app's own (`WorkshopStackHolderModel.getStackTypeDescription`), the only place they are
 * written down — the service sends the digit and nothing else.
 */
enum class StakeHolderRole(val code: String, val title: StringResource) {
    BOARD_MEMBER("1", Res.string.stackholder_role_board_member),
    SIGNATORY("2", Res.string.stackholder_role_signatory),
    CEO("3", Res.string.stackholder_role_ceo),
    REPRESENTATIVE("4", Res.string.stackholder_role_representative);

    companion object {
        fun fromCode(code: String?): StakeHolderRole? = entries.firstOrNull { it.code == code?.trim() }
    }
}
