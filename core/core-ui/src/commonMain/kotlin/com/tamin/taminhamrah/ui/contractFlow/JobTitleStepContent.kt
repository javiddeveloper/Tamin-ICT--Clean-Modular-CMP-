package com.tamin.taminhamrah.ui.contractFlow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_field_job_title_required
import taminx.core.core_ui.contract_job_code_label
import taminx.core.core_ui.contract_job_title_info
import taminx.core.core_ui.ic_tamin_search

@Composable
fun JobTitleStepContent(
    freeJobs: List<FreeJobDN>,
    selectedFreeJobCode: String?,
    selectedFreeJobName: String?,
    isFreeJobsLoading: Boolean,
    onFreeJobSelected: (FreeJobDN) -> Unit,
    modifier: Modifier = Modifier,
) {
    val jobCodeSubtitle = selectedFreeJobCode
        ?.takeIf { it.isNotBlank() }
        ?.let { stringResource(Res.string.contract_job_code_label, it.toPersianDigits()) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        BannerCard(
            message = stringResource(Res.string.contract_job_title_info),
            type = BannerType.Info,
        )

        SelectableField(
            label = stringResource(Res.string.contract_field_job_title_required),
            options = freeJobs,
            selectedCode = selectedFreeJobCode.orEmpty(),
            selectedName = selectedFreeJobName.orEmpty(),
            optionCode = { it.jobCode.orEmpty() },
            optionName = { it.discrioption.orEmpty() },
            isLoading = isFreeJobsLoading,
            selectedSubtitle = jobCodeSubtitle,
            icon = vectorResource(Res.drawable.ic_tamin_search),
            showChevron = true,
            onSelected = onFreeJobSelected,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun JobTitleStepContentPreview() {
    PreviewRtlThemeContent {
        JobTitleStepContent(
            freeJobs = listOf(
                FreeJobDN(
                    discrioption = "برنامه‌نویس و توسعه‌دهنده نرم‌افزار",
                    endDate = null,
                    fixRank = null,
                    id = 1,
                    iscoCode = null,
                    jobCode = "010203",
                    startDate = null,
                    status = null,
                ),
            ),
            selectedFreeJobCode = "010203",
            selectedFreeJobName = "برنامه‌نویس و توسعه‌دهنده نرم‌افزار",
            isFreeJobsLoading = false,
            onFreeJobSelected = {},
        )
    }
}
