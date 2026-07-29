package com.tamin.taminhamrah.ui.home.services.employer.debt.bottomSheet

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.InstallmentBottomSheetBinding
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.home.services.employer.debt.viewModel.InstallmentDebtViewModel
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.UiUtils
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class InstallmentBottomSheet private constructor(val listener: InstallmentClickListener? = null) :
    BaseBottomSheetDialogFragment<InstallmentBottomSheetBinding, InstallmentDebtViewModel>() {


    override val mViewModelDialog: InstallmentDebtViewModel by viewModels()
    override fun getLayoutId() = R.layout.installment_bottom_sheet


    val debtAmount by lazy {
        arguments?.getLong(DEBT_AMOUNT_TAG, -1) ?: -1
    }

    var selectedInstallmentCount: TextView? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (debtAmount < 0L) {
            Toast.makeText(requireContext(), "خطا در دریافت میزان بدهی", Toast.LENGTH_LONG).show()
            dismiss()
        }

        viewBinding?.apply {
            clickListener = installmentCountClickListener

            tvDebtAmount.text = HtmlCompat.fromHtml(
                getString(
                    R.string.price,
                    UiUtils.createTextColorOrangeAndBold(formatter.format(debtAmount))
                ),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            btnInstallment.setOnClickListener {
                listener?.onInstallmentClick(selectedInstallmentCount?.text.toString())
                dismiss()
            }
            selectedInstallmentCount = tv2
            refreshInstallmentData()
        }

    }

    val formatter by lazy { DecimalFormat("#,###") }
/*


{"debitInstallmentDetail":[{"debitNumber":"0070930254741"}],"firstInstallmentPer":50,"workshopId":"0077120092","branchCode":"0070","peymanSequence":"00700386"}
 */

    //webpack:///node_modules/zone.js/dist/zone.js
    private val installmentCountClickListener by lazy {
        object : ItemViewClickListener {
            override fun onInstallmentClick(view: View) {
                if (view is TextView) {
                    selectedInstallmentCount?.apply {
                        setTextColor(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.debt_percent_color,
                                null
                            )
                        )
                        setBackgroundResource(R.drawable.bg_debt_percent)
                    }
                    selectedInstallmentCount = null
                    selectedInstallmentCount = view
                    refreshInstallmentData()
                }
            }
        }
    }
    /*val installmentCountByPrePayment =
        listOf(Pair(50, 12), Pair(40, 10), Pair(30, 8), Pair(20, 6), Pair(10, 4))
*/
    private lateinit var installmentPair:String

    private fun refreshInstallmentData() {
        viewBinding?.apply {
            installmentPair = "25"
            selectedInstallmentCount?.apply {
                setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.debt_percent_selected_color,
                        null
                    )
                )
                setBackgroundResource(R.drawable.bg_debt_percent_selected)
            }
            tvPercentValue.text = installmentPair
            val percent = installmentPair.toInt() / 100f
            val prePaymentAmount = (percent * debtAmount).toLong()
            tvPrePayment.text = formatter.format(prePaymentAmount)
            val remind = debtAmount - prePaymentAmount
            tvInstallmentAmount.text = formatter.format(remind / selectedInstallmentCount?.text.toString().toInt())
            val date7dayLater = (System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000))
            tvPrePaymentDate.text = ConvertDate.convertTimestampToPersianDate(date7dayLater)
        }
    }


    companion object {
        const val DEBT_AMOUNT_TAG = "DEBT_AMOUNT_TAG"
       // const val MIN_AMOUNT_FOR_10_PERCENT = 100000001L

        @JvmStatic
        fun getInstance(
            listener: InstallmentClickListener? = null,
            bundle: Bundle
        ): InstallmentBottomSheet {
            val dialog = InstallmentBottomSheet(listener)
            dialog.arguments = bundle
            return dialog
        }
    }

    interface ItemViewClickListener {
        fun onInstallmentClick(view: View)
    }

    interface InstallmentClickListener {
        fun onInstallmentClick(installmentNumber: String)
    }
}
