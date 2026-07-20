package com.tamin.taminhamrah.feature.taminServices.model

import com.tamin.taminhamrah.model.common.RoleDN

fun RoleDN.toPR(): RolePR {
    return RolePR(
        roleId = this.id,
        title = this.title
    )
}
