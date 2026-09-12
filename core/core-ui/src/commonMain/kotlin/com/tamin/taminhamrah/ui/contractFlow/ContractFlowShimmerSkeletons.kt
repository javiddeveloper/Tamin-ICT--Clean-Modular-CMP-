package com.tamin.taminhamrah.ui.contractFlow

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.theme.ButtonDimens
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerSize
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminOnAccentInkFaint
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.ui.theme.shimmer

/**
 * Full-screen shimmer skeleton displayed when loading the initial contract registration info and flow config.
 */
@Composable
fun ContractFlowScreenShimmerSkeleton(
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TaminTopAppBar(
                title = "",
                bottomPadding = Spacing.xl,
                shape = RoundedCornerShape(
                    bottomStart = CornerRadius.x3l,
                    bottomEnd = CornerRadius.x3l,
                ),
            ) {
                ContractHeroStepProgressShimmer()
            }
        },
        bottomBar = {
            TaminBottomBar(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(CornerRadius.md))
                        .shimmer(),
                )
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = Spacing.page,
                end = Spacing.page,
                top = Spacing.md,
                bottom = Spacing.xl,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            item {
                ContractRegistrationStepShimmerSkeleton()
            }
        }
    }
}

/**
 * Shimmer progress bar for hero top app bar.
 */
@Composable
fun ContractHeroStepProgressShimmer(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(18.dp)
                    .clip(RoundedCornerShape(CornerRadius.xs))
                    .shimmer(
                        colorBase = TaminOnAccentInkFaint,
                        colorHighlight = TaminOnAccentInkFaint.copy(alpha = 0.6f),
                    ),
            )
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(CornerRadius.xs))
                    .shimmer(
                        colorBase = TaminOnAccentInkFaint,
                        colorHighlight = TaminOnAccentInkFaint.copy(alpha = 0.6f),
                    ),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            repeat(9) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(CircleShape)
                        .shimmer(
                            colorBase = TaminOnAccentInkFaint,
                            colorHighlight = TaminOnAccentInkFaint.copy(alpha = 0.6f),
                        ),
                )
            }
        }

        Box(
            modifier = Modifier
                .width(220.dp)
                .height(12.dp)
                .clip(RoundedCornerShape(CornerRadius.xs))
                .shimmer(
                    colorBase = TaminOnAccentInkFaint,
                    colorHighlight = TaminOnAccentInkFaint.copy(alpha = 0.6f),
                ),
        )
    }
}

/**
 * Step 1: Registration & Eligibility Shimmer Skeleton.
 */
@Composable
fun ContractRegistrationStepShimmerSkeleton(
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        // Banner 1: Registration Status Skeleton
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CornerRadius.lg),
            colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(taminColors.border)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .shimmer(),
                    )
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(CornerRadius.xs))
                            .shimmer(),
                    )
                }
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(CornerRadius.xs))
                        .shimmer(),
                )
            }
        }

        // Banner 2: Eligibility Status Skeleton
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CornerRadius.lg),
            colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(taminColors.border)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .shimmer(),
                    )
                    Box(
                        modifier = Modifier
                            .width(110.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(CornerRadius.xs))
                            .shimmer(),
                    )
                }
                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(CornerRadius.xs))
                        .shimmer(),
                )
            }
        }

        // 2x2 User Details Grid Skeleton Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CornerRadius.xl),
            colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(taminColors.border)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                    ) {
                        Box(modifier = Modifier.width(90.dp).height(12.dp).clip(RoundedCornerShape(CornerRadius.xs)).shimmer())
                        Box(modifier = Modifier.width(120.dp).height(16.dp).clip(RoundedCornerShape(CornerRadius.xs)).shimmer())
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                    ) {
                        Box(modifier = Modifier.width(50.dp).height(12.dp).clip(RoundedCornerShape(CornerRadius.xs)).shimmer())
                        Box(modifier = Modifier.width(90.dp).height(16.dp).clip(RoundedCornerShape(CornerRadius.xs)).shimmer())
                    }
                }

                HorizontalDivider(color = taminColors.border)

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                    ) {
                        Box(modifier = Modifier.width(60.dp).height(12.dp).clip(RoundedCornerShape(CornerRadius.xs)).shimmer())
                        Box(modifier = Modifier.width(80.dp).height(16.dp).clip(RoundedCornerShape(CornerRadius.xs)).shimmer())
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                    ) {
                        Box(modifier = Modifier.width(55.dp).height(12.dp).clip(RoundedCornerShape(CornerRadius.xs)).shimmer())
                        Box(modifier = Modifier.width(100.dp).height(16.dp).clip(RoundedCornerShape(CornerRadius.xs)).shimmer())
                    }
                }
            }
        }
    }
}

/**
 * Edit-contract first step: registration banner + contract number/type + identity + branch.
 */
@Composable
fun EditRegistrationStepShimmerSkeleton(
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val cardShape = RoundedCornerShape(CornerRadius.x2l)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(taminColors.bgSurface)
            .border(Thickness.border, taminColors.border, cardShape)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ShimmerSize.bannerHeight)
                .clip(RoundedCornerShape(CornerRadius.card))
                .shimmer(),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            EditInfoTileShimmer(modifier = Modifier.weight(1f))
            EditInfoTileShimmer(modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            EditInfoTileShimmer(modifier = Modifier.weight(1f))
            EditInfoTileShimmer(modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            EditInfoTileShimmer(modifier = Modifier.weight(1f))
        }
        EditInfoTileShimmer(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun EditInfoTileShimmer(
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(CornerRadius.card))
            .background(colors.bgPage)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .width(ShimmerSize.labelWidth)
                .height(ShimmerSize.subtitleHeight)
                .clip(RoundedCornerShape(CornerRadius.xs))
                .shimmer(),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(ShimmerSize.titleHeight)
                .clip(RoundedCornerShape(CornerRadius.xs))
                .shimmer(),
        )
    }
}

/**
 * Step 3: User Info Form Shimmer Skeleton.
 */
@Composable
fun UserInfoStepShimmerSkeleton(
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        // Info banner skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(CornerRadius.lg))
                .shimmer(),
        )

        // Card form skeleton
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CornerRadius.xl),
            colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(taminColors.border)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                // City & Zip code
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    Box(modifier = Modifier.weight(1f).height(52.dp).clip(RoundedCornerShape(CornerRadius.md)).shimmer())
                    Box(modifier = Modifier.weight(1f).height(52.dp).clip(RoundedCornerShape(CornerRadius.md)).shimmer())
                }

                // Address
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(CornerRadius.md))
                        .shimmer(),
                )

                // Phone & Mobile
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    Box(modifier = Modifier.weight(1f).height(52.dp).clip(RoundedCornerShape(CornerRadius.md)).shimmer())
                    Box(modifier = Modifier.weight(1f).height(52.dp).clip(RoundedCornerShape(CornerRadius.md)).shimmer())
                }
            }
        }
    }
}

/**
 * Step 8: Insurance premium determination shimmer (rate chips + wage + calculate).
 */
@Composable
fun InsurancePremiumStepShimmerSkeleton(
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val cardShape = RoundedCornerShape(CornerRadius.x2l)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(taminColors.bgSurface)
            .border(Thickness.border, taminColors.border, cardShape)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .width(ShimmerSize.sectionLabelWidth)
                .height(ShimmerSize.titleHeight)
                .clip(RoundedCornerShape(CornerRadius.xs))
                .shimmer(),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(ShimmerSize.rateChipHeight)
                        .clip(RoundedCornerShape(CornerRadius.lg))
                        .shimmer(),
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(ShimmerSize.rateChipHeight)
                        .clip(RoundedCornerShape(CornerRadius.lg))
                        .shimmer(),
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ShimmerSize.infoBodyHeight)
                .clip(RoundedCornerShape(CornerRadius.listRow))
                .shimmer(),
        )
        Box(
            modifier = Modifier
                .width(ShimmerSize.sectionLabelWidth)
                .height(ShimmerSize.titleHeight)
                .clip(RoundedCornerShape(CornerRadius.xs))
                .shimmer(),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(ShimmerSize.stepperButtonSize)
                    .clip(RoundedCornerShape(CornerRadius.md))
                    .shimmer(),
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(ShimmerSize.wageValueHeight)
                    .clip(RoundedCornerShape(CornerRadius.md))
                    .shimmer(),
            )
            Box(
                modifier = Modifier
                    .size(ShimmerSize.stepperButtonSize)
                    .clip(RoundedCornerShape(CornerRadius.md))
                    .shimmer(),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ShimmerSize.sliderTrackHeight)
                .clip(RoundedCornerShape(CornerRadius.full))
                .shimmer(),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .width(ShimmerSize.valueWidth)
                    .height(ShimmerSize.valueHeight)
                    .clip(RoundedCornerShape(CornerRadius.xs))
                    .shimmer(),
            )
            Box(
                modifier = Modifier
                    .width(ShimmerSize.valueWidth)
                    .height(ShimmerSize.valueHeight)
                    .clip(RoundedCornerShape(CornerRadius.xs))
                    .shimmer(),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ButtonDimens.height)
                .clip(RoundedCornerShape(CornerRadius.xl))
                .shimmer(),
        )
    }
}

/**
 * Step 8: Premium & Salary Calculation Shimmer Skeleton.
 */
@Composable
fun PremiumSalaryStepShimmerSkeleton(
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CornerRadius.xl),
            colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(taminColors.border)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Box(modifier = Modifier.width(120.dp).height(14.dp).clip(RoundedCornerShape(CornerRadius.xs)).shimmer())
                Box(modifier = Modifier.fillMaxWidth().height(32.dp).clip(RoundedCornerShape(CornerRadius.md)).shimmer())
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Box(modifier = Modifier.width(80.dp).height(12.dp).clip(RoundedCornerShape(CornerRadius.xs)).shimmer())
                    Box(modifier = Modifier.width(80.dp).height(12.dp).clip(RoundedCornerShape(CornerRadius.xs)).shimmer())
                }
            }
        }

        // Calculation result card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CornerRadius.xl),
            colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(taminColors.border)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.width(100.dp).height(16.dp).clip(RoundedCornerShape(CornerRadius.xs)).shimmer())
                Box(modifier = Modifier.width(120.dp).height(20.dp).clip(RoundedCornerShape(CornerRadius.xs)).shimmer())
            }
        }
    }
}

/**
 * Step 5: Select Branch Shimmer Skeleton.
 */
@Composable
fun SelectBranchStepShimmerSkeleton(
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        repeat(2) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .shimmer(),
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CornerRadius.xl),
            colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(taminColors.border)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(CornerRadius.md))
                            .shimmer(),
                    )
                }
            }
        }
    }
}

/**
 * Step 4: Contract Applicant & Guardian Shimmer Skeleton.
 */
@Composable
fun ContractApplicantStepShimmerSkeleton(
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.x2l),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(taminColors.border)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            // Option 1
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .shimmer(),
            )

            // Option 2
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .shimmer(),
            )

            // Guardian Inputs Skeleton
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(CornerRadius.lg))
                        .shimmer(),
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(CornerRadius.lg))
                        .shimmer(),
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .shimmer(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(CornerRadius.lg))
                        .shimmer(),
                )
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(52.dp)
                        .clip(RoundedCornerShape(CornerRadius.lg))
                        .shimmer(),
                )
            }
        }
    }
}

/**
 * Step 6: Document upload shimmer skeleton.
 */
@Composable
fun UploadImageStepShimmerSkeleton(
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.x2l),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(taminColors.border),
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ShimmerSize.bannerHeight)
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .shimmer(),
            )

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Box(
                    modifier = Modifier
                        .width(ShimmerSize.labelWidth)
                        .height(ShimmerSize.titleHeight)
                        .clip(RoundedCornerShape(CornerRadius.xs))
                        .shimmer(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ShimmerSize.fieldHeight)
                        .clip(RoundedCornerShape(CornerRadius.lg))
                        .shimmer(),
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ShimmerSize.uploadCardHeight)
                    .clip(RoundedCornerShape(CornerRadius.card))
                    .shimmer(),
            )
        }
    }
}

/**
 * Step 7: Treatment support shimmer skeleton.
 */
@Composable
fun TreatmentSupportStepShimmerSkeleton(
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.x2l),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(taminColors.border),
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ShimmerSize.fieldHeight)
                        .clip(RoundedCornerShape(CornerRadius.lg))
                        .shimmer(),
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ShimmerSize.bannerHeight)
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .shimmer(),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ShimmerSize.fieldHeight)
                    .clip(RoundedCornerShape(CornerRadius.xl))
                    .shimmer(),
            )
        }
    }
}

/**
 * Dependents bottom-sheet loading shimmer (name + relation pill + two info tiles).
 */
@Composable
fun ContractDependentsSheetShimmerSkeleton(
    modifier: Modifier = Modifier,
    cardCount: Int = 3,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        repeat(cardCount) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(CornerRadius.xl),
                colors = CardDefaults.cardColors(
                    containerColor = colors.bgSurface,
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(colors.border),
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(ShimmerSize.titleHeight)
                                .clip(RoundedCornerShape(CornerRadius.xs))
                                .shimmer(),
                        )
                        Box(
                            modifier = Modifier
                                .width(ShimmerSize.chipWidth)
                                .height(ShimmerSize.badgeHeight)
                                .clip(RoundedCornerShape(CornerRadius.chip))
                                .shimmer(),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(ShimmerSize.sonCardHeight)
                                .clip(RoundedCornerShape(CornerRadius.card))
                                .shimmer(),
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(ShimmerSize.sonCardHeight)
                                .clip(RoundedCornerShape(CornerRadius.card))
                                .shimmer(),
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Previews
// -------------------------------------------------------------------------

@PreviewRtlTheme
@Composable
private fun ContractFlowScreenShimmerSkeletonPreview() {
    PreviewRtlThemeContent {
        ContractFlowScreenShimmerSkeleton()
    }
}

@PreviewRtlTheme
@Composable
private fun ContractRegistrationStepShimmerSkeletonPreview() {
    PreviewRtlThemeContent {
        ContractRegistrationStepShimmerSkeleton()
    }
}

@PreviewRtlTheme
@Composable
private fun EditRegistrationStepShimmerSkeletonPreview() {
    PreviewRtlThemeContent {
        EditRegistrationStepShimmerSkeleton()
    }
}

@PreviewRtlTheme
@Composable
private fun UserInfoStepShimmerSkeletonPreview() {
    PreviewRtlThemeContent {
        UserInfoStepShimmerSkeleton()
    }
}

@PreviewRtlTheme
@Composable
private fun ContractApplicantStepShimmerSkeletonPreview() {
    PreviewRtlThemeContent {
        ContractApplicantStepShimmerSkeleton()
    }
}

@PreviewRtlTheme
@Composable
private fun UploadImageStepShimmerSkeletonPreview() {
    PreviewRtlThemeContent {
        UploadImageStepShimmerSkeleton()
    }
}

@PreviewRtlTheme
@Composable
private fun TreatmentSupportStepShimmerSkeletonPreview() {
    PreviewRtlThemeContent {
        TreatmentSupportStepShimmerSkeleton()
    }
}

@PreviewRtlTheme
@Composable
private fun ContractDependentsSheetShimmerSkeletonPreview() {
    PreviewRtlThemeContent {
        ContractDependentsSheetShimmerSkeleton()
    }
}

@PreviewRtlTheme
@Composable
private fun InsurancePremiumStepShimmerSkeletonPreview() {
    PreviewRtlThemeContent {
        InsurancePremiumStepShimmerSkeleton()
    }
}

@PreviewRtlTheme
@Composable
private fun PremiumSalaryStepShimmerSkeletonPreview() {
    PreviewRtlThemeContent {
        PremiumSalaryStepShimmerSkeleton()
    }
}

@PreviewRtlTheme
@Composable
private fun SelectBranchStepShimmerSkeletonPreview() {
    PreviewRtlThemeContent {
        SelectBranchStepShimmerSkeleton()
    }
}
