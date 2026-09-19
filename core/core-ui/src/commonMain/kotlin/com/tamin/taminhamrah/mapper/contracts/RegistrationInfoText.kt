package com.tamin.taminhamrah.mapper.contracts

import androidx.compose.runtime.Composable
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_gender_honorific_female
import taminx.core.core_ui.contract_gender_honorific_male

@Composable
fun RegistrationInfoPR.genderHonorific(): String = stringResource(
    if (isFemale) {
        Res.string.contract_gender_honorific_female
    } else {
        Res.string.contract_gender_honorific_male
    },
)
