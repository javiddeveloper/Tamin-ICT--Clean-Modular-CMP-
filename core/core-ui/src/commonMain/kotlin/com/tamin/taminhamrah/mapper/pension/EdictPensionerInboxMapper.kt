package com.tamin.taminhamrah.mapper.pension

import com.tamin.taminhamrah.model.pension.EdictPensionerInboxDN
import com.tamin.taminhamrah.model.pension.EdictPensionerInboxPR

fun EdictPensionerInboxDN.toPR(): EdictPensionerInboxPR {
    return EdictPensionerInboxPR(
        message = message.orEmpty()
    )
}
