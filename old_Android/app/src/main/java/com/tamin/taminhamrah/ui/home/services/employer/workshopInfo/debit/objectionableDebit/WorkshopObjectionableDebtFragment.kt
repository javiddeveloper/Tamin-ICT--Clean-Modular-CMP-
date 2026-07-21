package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.debit.objectionableDebit

import android.os.Bundle
import android.provider.Browser
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsSession
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.PaymentModel
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDebt
import com.tamin.taminhamrah.databinding.FragmentWorkshopInfoBinding
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.WorkshopInfoViewModel
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.WorkshopSearchDialogFragment
import com.tamin.taminhamrah.utils.EventObserver
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class WorkshopObjectionableDebtFragment :
    BaseFragment<FragmentWorkshopInfoBinding, WorkshopInfoViewModel>(),
    AdapterInterface.OnItemClickListener<WorkShopDebt>,
    DialogResultInterface.OnResultListener<Map<String, String>> {

    lateinit var listAdapter: WorkshopObjectionableDeptAdapter

    override val mViewModel: WorkshopInfoViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_workshop_info
    }

    override fun setupObserver() {
        mViewModel.mldPaymentPreCheck.observe(this, EventObserver {showPaymentPreCheckResult(it)})
        mViewModel.mldPayment.observe(this, EventObserver {showPaymentResult(it)})
        mViewModel.mldObjectionPermission.observe(this, ::showPermissionResult)
        mViewModel.mldPdf.observe(this, ::showPDFResult)
    }

    override fun initView() {
        listAdapter = WorkshopObjectionableDeptAdapter(true, this)
        viewDataBinding?.apply {
            setupRecycler(recycler, listAdapter)

            setupToolbar(
                appBar,
                appbarBackgroundImage.imageBackground
            )
        }
    }

    override fun getData() {
        getWorkshopId()?.let { workshopId ->
            getBranchCode()?.let { branchCode ->
                this@WorkshopObjectionableDebtFragment.lifecycleScope.launchWhenCreated {
                    mViewModel.getWorkshopObjectionableDebitList(
                        workshopId,
                        branchCode
                    ).collectLatest { pagingData ->
                        listAdapter.submitData(pagingData)
                    }
                }
            }
        }
    }

    override fun onClick() {
        viewDataBinding?.let {
            it.appBar.toolbar.apply {
                imgAction.setOnClickListener {

                    val dialog = WorkshopSearchDialogFragment()
                    dialog.setListener(this@WorkshopObjectionableDebtFragment)
                    dialog.show(childFragmentManager, "jfhskljkjllkljl")
                }
            }
        }
    }

    private fun getWorkshopId(): String? {
        return arguments?.getString(Constants.WORKSHOP_ID)
    }

    private fun getBranchCode(): String? {
        return arguments?.getString(Constants.BRANCH_ID)
    }

    private fun showPermissionResult(result: WorkShopDebt) {

        if (result.hasPermission == true) {
            // for baravordi and badiv

            val bundle = Bundle()
            bundle.putParcelable(mViewModel.ARG_WORKSHOP_DEBIT, result)
            bundle.putString(Constants.WORKSHOP_ID, getWorkshopId())
            bundle.putString(Constants.BRANCH_ID, getBranchCode())

            bundle.putString(
                Constants.TOOLBAR_TITLE, viewDataBinding?.appBar?.tvSubTitle?.text.toString()
            )
            bundle.putString(
                Constants.TOOLBAR_SUBTITLE, viewDataBinding?.appBar?.tvSubSubTitle?.text.toString()
            )
            bundle.putString(
                Constants.TOOLBAR_SUB_SUBTITLE, "${getString(R.string.label_debit_code)} : ${result.debitNumber}"
            )
            bundle.putString(Constants.TOOLBAR_ICON_IMAGE, Utility.getToolbarIconImage(arguments))

            bundle.putParcelable(DebitObjectionFragment.DEBIT_ITEM, result)

            handlePageDestination(R.id.action_workshopObjection_to_objection_request, bundle)
        } else {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.ERROR,
                requireContext().getString(R.string.error_expire_time_of_objection_to_debit)
            )
        }
    }

    private fun showPaymentPreCheckResult(result: PaymentModel) {

            result.apply {
                /* val dialog = PaymentDialogFragment()
                 val bundle=Bundle()
                 bundle.putParcelable(PaymentDialogFragment.ARG_PAYMENT, result.data)
                 dialog.arguments=bundle
                 dialog.show(childFragmentManager, "79898")*/

                mViewModel.normalDebitPayment(
                    branchCode,
                    workshopId,
                    debitNumber,
                    peymanSequence,
                    seporde
                )
            }
        }

    private fun showPaymentResult(result: PaymentResponse) {

        if (result.isSuccess) {

      /*      val url = result.paymentURL ?: ""

            *//*  val bundle = Bundle()
              bundle.putString("url", url)
              bundle.putString("token", mViewModel.getToken())
              handlePageDestination(R.id.action_workshopInfo_debt_to_webview, bundle)
  *//*
//            Utility.openLink(requireActivity(), url, mViewModel.getToken() )

            val URI = Uri.parse(url)

            mSession!!.validateRelationship(
                CustomTabsService.RELATION_USE_AS_ORIGIN,
                URI, null
            )

            val intent: CustomTabsIntent? =
                mSession?.let { constructExtraHeadersIntent(it, mViewModel.getToken()) }
            intent?.launchUrl(
                requireContext(),
                URI
            )*/


            /*   val builderCustomTabs = CustomTabsIntent.Builder()
               val intentCustomTabs = builderCustomTabs.build()
               intentCustomTabs.intent.setPackage("com.android.chrome")
               intentCustomTabs.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)


               val headers = Bundle()
               headers.putString("bearer-token", mViewModel.getToken())
               intentCustomTabs.intent.putExtra(Browser.EXTRA_HEADERS, headers)

               intentCustomTabs.launchUrl(requireContext(), Uri.parse(result.data?.paymentURL))


   */

//            Utility.openLink(requireActivity(), result.data?.paymentURL, mViewModel.getToken())
        }
    }

    private fun constructExtraHeadersIntent(
        session: CustomTabsSession,
        token: String
    ): CustomTabsIntent? {
        val intent = CustomTabsIntent.Builder(session).build()

        // Example non-cors-whitelisted headers.
        val headers = Bundle()
        headers.putString("bearer-token", token)
        headers.putString("redirect-url", "Some redirect url")
//        headers.putString("redirect-url", "Some redirect url")
        intent.intent.putExtra(Browser.EXTRA_HEADERS, headers)


        return intent
    }


    override fun onItemClick(item: WorkShopDebt, transitionView: View?, tag: String?) {

        if (item.getObjectionType() == WorkShopDebt.ObjectionType.DEFAULT) {
            item.seqNo?.let { mViewModel.getDebitObjectionPdf(it) }
        } else {
            mViewModel.checkWorkshopDebitObjectionPermission(item)
        }
    }

    override fun onDialogResult(item: Map<String, String>) {
        val workshopId = item[Constants.WORKSHOP_ID] ?: ""
        val branchCode = item[Constants.BRANCH_ID] ?: ""
//        mViewModel.getWorkshopList(workshopId = workshopId, branchCode = branchCode)
    }

    private fun showPDFResult(result: PdfDownloadResponse) {
        if (result.isSuccess) {
            val file = Utility.writeByteStreamToDisk(Utility.getToolbarTitle(arguments), requireContext(), result.pdf)
            if (file == null) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_file)
                )
                return
            }
            val bundle = Bundle()
            bundle.putString(PdfViewerActivity.ARG_TITLE,Utility.getToolbarTitle(arguments))
            bundle.putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file.path)
            handlePageDestination(R.id.action_workshop_docs_to_pdf_viewer, bundle)
        }

    }

}