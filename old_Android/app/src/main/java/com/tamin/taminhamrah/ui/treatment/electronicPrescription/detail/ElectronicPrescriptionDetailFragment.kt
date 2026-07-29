package com.tamin.taminhamrah.ui.treatment.electronicPrescription.detail

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.electronicPrescription.ElectronicPrescriptionPriceResponse
import com.tamin.taminhamrah.databinding.FragmentMyElectronicPrescriptionDetailBinding
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.treatment.TreatmentViewModel
import com.tamin.taminhamrah.ui.treatment.electronicPrescription.adapter.ElectronicPrescriptionDetailAdapter
import com.tamin.taminhamrah.ui.treatment.electronicPrescription.model.AllPrescriptionDetailEnumClass
import com.tamin.taminhamrah.ui.treatment.electronicPrescription.model.PrescriptionType
import com.tamin.taminhamrah.utils.ImageUtils
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.abs


@AndroidEntryPoint
class ElectronicPrescriptionDetailFragment :
    BaseFragment<FragmentMyElectronicPrescriptionDetailBinding, TreatmentViewModel>() {

    companion object {
        const val ARG_NOTE_HEAD_ELECTRONIC_PRESCRIPTION = "ARG_NOTE_HEAD_ELECTRONIC_PRESCRIPTION"
        const val ARG_REQUEST_TYPE = "ARG_REQUEST_TYPE"
        const val ARG_NATIONAL_CODE = "ARG_NATIONAL_CODE"
        const val ARG_CHILD_NATIONAL_CODE = "ARG_CHILD_NATIONAL_CODE"
        const val ARG_FLAG_SATA = "ARG_FLAG_SATA"
        const val ARG_REGISTER_DATE = "ARG_REGISTER_DATE"
        const val PRES_TYPE = "PRES_TYPE"
        const val PRES_NAME = "PRES_NAME"
        const val HIDE_CAST_LAYOUT = 0.0f
    }

    val listAdapter: ElectronicPrescriptionDetailAdapter by lazy {
        ElectronicPrescriptionDetailAdapter()
    }
    override val mViewModel: TreatmentViewModel by viewModels()

    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_my_electronic_prescription_detail

    override fun setupObserver() {
        mViewModel.mldPrice.observe(this, ::showResultPrice)
        mViewModel.mldPDF.observe(this, ::onPdfResponse)
    }

    private fun getArgNoteHeadElectronicPrescription() =
        arguments?.getLong(ARG_NOTE_HEAD_ELECTRONIC_PRESCRIPTION) ?: 0L

    private fun getAgrRequestType() =
        arguments?.getString(ARG_REQUEST_TYPE) ?: ""

    private fun getAgrNationalCode() =
        arguments?.getString(ARG_NATIONAL_CODE) ?: ""

    private fun getAgrChildNationalCode() =
        arguments?.getString(ARG_CHILD_NATIONAL_CODE) ?: ""

    private fun getAgrFlagSata() =
        arguments?.getString(ARG_FLAG_SATA) ?: ""

    private fun getPrescriptionType() =
        arguments?.getString(PRES_TYPE) ?: ""

    private fun getPrescriptionName() =
        arguments?.getString(PRES_NAME)

    private fun getRegisterDate() =
        arguments?.getString(ARG_REGISTER_DATE) ?: "-"

    override fun initView() {
        viewDataBinding?.apply {
            initialCastsView()
            recycler.apply {
                this.adapter = listAdapter
                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
                }
            }
            setupToolbar()
        }
    }

    fun setupToolbar() {
        viewDataBinding?.let { binding ->
            binding.appBar.apply {
                var toolbarTitle = ""
                arguments?.let { args ->
                    ImageUtils.imageDrawable(imgIcon, Utility.getToolbarIconDrawable(args))
                    toolbarTitle = Utility.getToolbarTitle(args)
                }
                //    tvTitle.text=toolbarTitle
                toolbar.imgInfo.visibility = View.GONE
                appBarView.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                    val maxScroll = appBarLayout.totalScrollRange
                    val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()
                    handleAlphaOnTitle(percentage, containerAppbarTitle)
                    handleToolbarTitleVisibility(
                        percentage,
                        toolbar.tvToolbarTitle,
                        toolbarTitle,
                        binding.appbarBackgroundImage.imageBackground,
                        null
                    )
                    binding.layoutCast.visibility = if (percentage == HIDE_CAST_LAYOUT)
                        View.VISIBLE
                    else
                        View.INVISIBLE
                }
                tvTitle.text = toolbarTitle
            }
        }
    }

    private fun initialCastsView() {
        viewDataBinding?.insuredShareView?.apply {
            root.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    AllPrescriptionDetailEnumClass.PRIMARY.idBgColor
                )
            )
            tvTitle.text = getString(AllPrescriptionDetailEnumClass.PRIMARY.title)
            tvPrice.setTextColor(
                ContextCompat.getColor(
                    requireContext(), AllPrescriptionDetailEnumClass.PRIMARY.idTextColor
                )
            )
        }

        viewDataBinding?.prescriptionCastView?.apply {
            root.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    AllPrescriptionDetailEnumClass.ORANGE.idBgColor
                )
            )
            tvTitle.text = getString(AllPrescriptionDetailEnumClass.ORANGE.title)
            tvPrice.setTextColor(
                ContextCompat.getColor(
                    requireContext(), AllPrescriptionDetailEnumClass.ORANGE.idTextColor
                )
            )
        }

        viewDataBinding?.organizationShareView?.apply {
            root.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    AllPrescriptionDetailEnumClass.GREEN.idBgColor
                )
            )
            tvTitle.text = getString(AllPrescriptionDetailEnumClass.GREEN.title)
            tvPrice.setTextColor(
                ContextCompat.getColor(
                    requireContext(), AllPrescriptionDetailEnumClass.GREEN.idTextColor
                )
            )
        }
    }

    override fun getData() {
        this@ElectronicPrescriptionDetailFragment.lifecycleScope.launchWhenCreated {
            mViewModel.getElectronicPrescriptionDetail(
                getArgNoteHeadElectronicPrescription(),
                getAgrRequestType(),
                getAgrNationalCode(),
                getAgrChildNationalCode(),
                getAgrFlagSata(),
            ).collectLatest { pagingData ->
                listAdapter.submitData(pagingData.map {
                    val date = if (it.registerDate == null) getRegisterDate() else it.registerDate
                    viewDataBinding?.tvVisitDateValue?.text =
                        if (date != null && date.contains("/")) date else Utility.getDateSeparator(
                            date
                        )
                    var title = getPrescriptionName()
                    if (title == null) {
                        val typePres = getTypePrescription(getPrescriptionType())?.title
                        title = typePres?.let { it1 -> getString(it1) }
                    }
                    viewDataBinding?.tvType?.text =
                        getString(R.string.prescription_with_value, title)
                    it.prescriptionType = getPrescriptionType()
                    it
                })
            }
        }
        mViewModel.getElectronicPrescriptionPrice(
            getArgNoteHeadElectronicPrescription(),
            getAgrNationalCode()
        )
    }

    private fun getTypePrescription(id: String): PrescriptionType? {
        return when (id) {
            "0" -> {
                PrescriptionType.PHARMACY
            }

            "1" -> {
                PrescriptionType.MEDICAL
            }

            "2" -> {
                PrescriptionType.PARA_CLINIC
            }

            "3" -> {
                PrescriptionType.VISIT
            }

            "5" -> {
                PrescriptionType.MEDICAL_SERVICE
            }

            else -> {
                null
            }
        }
    }

    override fun onClick() {
        viewDataBinding?.appBar?.toolbar?.apply {
            imgAction.setOnClickListener {
                mViewModel.getPrescriptionPdfFile(
                    getArgNoteHeadElectronicPrescription().toString(),
                    true
                )
            }
            imageBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun showResultPrice(result: ElectronicPrescriptionPriceResponse) {
        if (result.isSuccess) {
            val list = result.data?.list ?: emptyList()
            if (list.isNotEmpty()) {
                viewDataBinding?.apply {
                    prescriptionCastView.tvPrice.text =
                        Utility.getRialWithSeparator(list[0].requestPrice)
                    organizationShareView.tvPrice.text =
                        Utility.getRialWithSeparator(list[0].headInsuPayment)
                    insuredShareView.tvPrice.text =
                        Utility.getRialWithSeparator(list[0].headSsoPayment)
                }
            }
        }
    }

    private fun onPdfResponse(result: PdfDownloadResponse) {
        if (result.isSuccess) {
            val title = Utility.getToolbarTitle(arguments)
            val file = Utility.writeByteStreamToDisk(title, requireContext(), result.pdf)
            if (file == null) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_file)
                )
                return
            } else {
                handlePageDestination(
                    R.id.action_prescription_detail_to_pdf_viewer,
                    Bundle().apply {
                        putString(PdfViewerActivity.ARG_TITLE, title)
                        putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file.path)
                    })
            }
        }
    }

}


