package com.tamin.taminhamrah.ui.treatment

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.electronicPrescription.DependantUserUnder18Response
import com.tamin.taminhamrah.data.remote.models.services.treatmentServices.deserved.EligibilityTreatmentResponse
import com.tamin.taminhamrah.data.remote.models.user.LackEntitlementResponse
import com.tamin.taminhamrah.data.remote.models.user.toTreatmentCardDataModel
import com.tamin.taminhamrah.databinding.FragmentTreatmentRefactoredBinding
import com.tamin.taminhamrah.ui.NavigatorAdapter
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.treatment.adapter.ViewPagerAdapter
import com.tamin.taminhamrah.ui.treatment.model.TreatmentCardDataModel
import com.tamin.taminhamrah.ui.treatment.treatmentCard.TreatmentCardFragment
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.extentions.dpToPx
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TreatmentFragment : BaseFragment<FragmentTreatmentRefactoredBinding, TreatmentViewModel>(),
    AdapterInterface.OnItemClickListener<MenuModel> {

    // region Variables
    override val mViewModel: TreatmentViewModel by viewModels()
    private val slides = ArrayList<TreatmentCardFragment>()
    private val usersInfo: MutableSet<TreatmentCardDataModel> = mutableSetOf()
    private var currentUser = TreatmentCardDataModel()
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)
    private lateinit var listAdapter: NavigatorAdapter
    private val cardAdapter by lazy { ViewPagerAdapter(requireActivity()) }
    //endregion

    //region Base methods
    override fun getLayoutId() = R.layout.fragment_treatment_refactored
    override fun setupObserver() {
        mViewModel.mldDeservedMainUser.observe(this, ::onDeservedMainUserResponse)
        mViewModel.mldDependents18.observe(this, ::onDependentsInfoResponse)
        mViewModel.mldEligibility.observe(this, ::onEligibilityResponse)
    }

    override fun initView() {

        listAdapter = NavigatorAdapter()
        viewDataBinding?.recycler?.apply {
            this.adapter = listAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(UiUtils.createDivider(this.context))
            }
        }
        listAdapter.setItems(mViewModel.getItemsList(), this)
        viewDataBinding?.apply {

            viewPager.adapter = cardAdapter
            viewPager.isSaveEnabled = false
            tabLayout.tabMode = TabLayout.MODE_FIXED
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    if (position > usersInfo.size)
                        return
                    currentUser = usersInfo.toList()[position]
                }
            })
            TabLayoutMediator(tabLayout, viewPager) { _, _ ->
                for (i in 0 until tabLayout.tabCount) {
                    val tab = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
                    val p = tab.layoutParams as ViewGroup.MarginLayoutParams
                    p.setMargins(0, 0, 4.dpToPx(requireContext()), 0)
                    tab.requestLayout()
                }
            }.attach()
        }
    }

    override fun getData() {
        mViewModel.getUserAndDependentsInfo()
    }

    override fun onClick() {}
    //endregion

    //region Listeners
    private fun onDependentsInfoResponse(result: DependantUserUnder18Response) {
        if (result.isSuccess) {
            if (result.data?.list?.isNotEmpty() == true) {
                result.data?.list?.forEach { dependent ->
                    dependent.relationWithTamin?.apply {
                        personal?.nationalId?.let {
                            val treatmentCard = treatmentCardsInfo(
                                encodeAsBitmap(personal?.nationalId),
                                requireContext(),
                                usersInfo.first().isTreatmentSupport
                            )
                            usersInfo.add(treatmentCard)
                        }
                    }
                    slides.clear()
                    usersInfo.forEach { user ->
                        slides.add(TreatmentCardFragment.newInstance(user))
                    }
                    currentUser = usersInfo.first()
                    cardAdapter.submitList(slides.toList())

                }
            } else {
                slides.clear()
                usersInfo.forEach { user ->
                    slides.add(TreatmentCardFragment.newInstance(user))
                }
                currentUser = usersInfo.first()
                cardAdapter.submitList(slides.toList())
            }
        }
    }

    private fun onDeservedMainUserResponse(result: LackEntitlementResponse) {
        if (result.isSuccess && result.data?.list?.isNotEmpty() == true) {
            usersInfo.clear()
            val list = result.data?.list
            if (!list.isNullOrEmpty()) {
                list[0].natCode?.let { natCode ->
                    val bitmap =encodeAsBitmap(natCode)
                      val qrCodeFilePath=  UiUtils.setQrCodeBitmap(bitmap, requireContext())
                    val treatmentCard =
                        list[0].toTreatmentCardDataModel(qrCodeFilePath)
                    usersInfo.add(treatmentCard)
                    //  slides.plus(TreatmentCardFragment.newInstance(treatmentCard))
                }
                //   cardAdapter.submitList(slides)
            }
        }
    }

    private fun onEligibilityResponse(result: EligibilityTreatmentResponse) {
        var isSupport = false
        if (result.isSuccess) {
            if (result.data?.result == true) {
                isSupport = true
            }
            usersInfo.forEach { user ->
                if (user.nationalCode == result.data?.nationalId) {
                    user.isChecked = true
                    user.isTreatmentSupport = isSupport
                }
            }
            // Find the index of the current user in the slides list
            val userIndex = usersInfo.indexOfFirst { it.nationalCode == result.data?.nationalId }
            // Get the corresponding TreatmentCardFragment
            val fragment = slides.getOrNull(userIndex)
            // Update the UI if the fragment exists
            fragment?.updateUI(isSupport)
            // Notify the adapter about the data change
            cardAdapter.notifyItemChanged(userIndex)
        }
    }

    override fun onItemClick(item: MenuModel, transitionView: View?, tag: String?) {
        when (item.id) {
            "1" -> {
                handlePageDestination(
                    R.id.action_treatment_to_electronic_prescription,
                    createToolbarBundle(item)
                )
            }
            "2" -> {
                handlePageDestination(
                    R.id.action_to_medicalAuthoritiesFragment,
                    createToolbarBundle(item)
                )
            }
            "3" -> {
                handlePageDestination(
                    R.id.action_to_treatmentCostsFragment,
                    createToolbarBundle(item)
                )
            }
        }
    }
    //endregion

    //region Utils
    fun createToolbarBundle(item: MenuModel): Bundle {
        return Bundle().apply {
            if (item.titleStringResId != 0) {
                putString(Constants.TOOLBAR_TITLE, getString(item.titleStringResId))
            }
            putString(Constants.TOOLBAR_SUBTITLE, item.description)
            putInt(Constants.TOOLBAR_ICON_IMAGE, item.iconRes)
            putString(Constants.SELECTED_USER, currentUser.nationalCode)
            putParcelableArrayList(Constants.USERS,mViewModel.usersInfo)
        }
    }

    @Throws(WriterException::class)
    fun encodeAsBitmap(nationalId: String?): Bitmap? {
        val content = "https://eservices.tamin.ir/pwa/#/medical/detail/$nationalId"
        val bitMatrix: BitMatrix = try {
            MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE, 512, 512, null
            )
        } catch (iae: IllegalArgumentException) {
            // Unsupported format
            return null
        }

        val width = bitMatrix.width
        val height = bitMatrix.height

        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }
    //endregion
}