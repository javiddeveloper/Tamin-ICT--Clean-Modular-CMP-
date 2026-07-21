package com.tamin.taminhamrah.ui.home.services.objectionInsuranceHistory.detailInfo

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.ObjectionInsuranceHistoryModel
import com.tamin.taminhamrah.databinding.DialogDetailObjectionInsuranceHistoryByMonthBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.home.services.objectionInsuranceHistory.adapter.SeasonAdapter
import com.tamin.taminhamrah.ui.home.services.objectionInsuranceHistory.model.MonthModel
import com.tamin.taminhamrah.ui.home.services.objectionInsuranceHistory.model.SeasonEnumClass
import com.tamin.taminhamrah.ui.home.services.objectionInsuranceHistory.model.SeasonModel
import com.tamin.taminhamrah.utils.UiUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ObjectionInsuranceHistoryDetailDialog :
    BaseBottomSheetDialogFragment<DialogDetailObjectionInsuranceHistoryByMonthBinding, BaseViewModel>(),
    AdapterInterface.OnObjectionInsuranceHistoryListener,
    AdapterInterface.OnDeleteClickListener<ObjectionInsuranceHistoryModel> {

    //region Variables

    private var onResultListener: AdapterInterface.OnObjectionInsuranceHistoryListener? = null
    private var mainInfo: ObjectionInsuranceHistoryModel? = null
    private var seasonList: ArrayList<SeasonModel> = ArrayList()
    var onCleanEditedListener: AdapterInterface.OnDeleteClickListener<ObjectionInsuranceHistoryModel>? =
        null
    private val seasonAdapter by lazy {
        SeasonAdapter()
    }
    //endregion

    //region Base methods
    override val mViewModelDialog: BaseViewModel by viewModels()
    override fun getLayoutId() = R.layout.dialog_detail_objection_insurance_history_by_month
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
        onClick()
    }
    //endregion

    //region Listeners
    override fun sendObjectionInsurance(item: ObjectionInsuranceHistoryModel?) {
        onResultListener?.let {
            it.sendObjectionInsurance(item)
            dismiss()
        }
    }

    override fun onDelete(item: ObjectionInsuranceHistoryModel) {
        onCleanEditedListener?.onDelete(item)
        dismiss()
    }
    //endregion

    //region Utils
    private fun initView() {
        viewBinding?.apply {
            tvYear.text = mainInfo?.year ?: "-"
            tvValueWorkshop.text = mainInfo?.workShopName ?: "-"
            tvValueHistoryType.text = mainInfo?.historyTypeName ?: "-"
            tvValueBranchName.text = mainInfo?.branchname ?: "-"

            recyclerSeason.apply {
                adapter = seasonAdapter
                layoutManager = LinearLayoutManager(context)
                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.createDivider(requireContext()))
                }
            }
        }
    }

    private fun onClick() {
        viewBinding?.apply {
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnClean.setOnClickListener {

                val seasonsInfo = seasonAdapter.getItems()
                Log.i("9696: ", " Enter : ----${seasonsInfo.toString()}")
                if (checkInfoEdited(seasonsInfo)) {
                    mainInfo?.apply {
                        seasonList = cleanEditedInfo(seasonsInfo)
                        Log.i("9696: ", " EXit : ----${seasonList.toString()}")
                        updateMainInfo(seasonList)
                        onCleanEditedListener?.onDelete(item = this)
                        seasonAdapter.resetInfo()
                        recyclerSeason.adapter = seasonAdapter
                        isEdited = false
                    }
                }
            }

            btnOk.setOnClickListener {
                val seasonsInfo = seasonAdapter.getItems()
                if (checkInfoEdited(seasonsInfo)) {
                    updateMainInfo(seasonsInfo)
                    seasonList.clear()
                    seasonList.addAll(seasonsInfo)
                    onResultListener?.sendObjectionInsurance(mainInfo)

                }
                dismiss()
            }
        }
    }

    private fun checkInfoEdited(list: List<SeasonModel>): Boolean {
        list.forEach { season ->
            season.monthValues.forEach { month ->
                if (month.editedValue.isNotBlank() && month.editedValue != "00") {
                    return true
                }
            }
        }
        return false
    }

    private fun updateMainInfo(seasonList: ArrayList<SeasonModel>) {
        mainInfo?.apply {
            seasonList.forEach { season ->
                season.monthValues.forEach { month ->
                    if (month.editedValue.isNotBlank() && month.editedValue != "0") {
                        isEdited = true
                        return@forEach
                    }
                }
            }
            if (seasonList.size == 4 && isEdited) {
                newMonth1 = seasonList[0].monthValues[0].editedValue
                newMonth2 = seasonList[0].monthValues[1].editedValue
                newMonth3 = seasonList[0].monthValues[2].editedValue

                newMonth4 = seasonList[1].monthValues[0].editedValue
                newMonth5 = seasonList[1].monthValues[1].editedValue
                newMonth6 = seasonList[1].monthValues[2].editedValue

                newMonth7 = seasonList[2].monthValues[0].editedValue
                newMonth8 = seasonList[2].monthValues[1].editedValue
                newMonth9 = seasonList[2].monthValues[2].editedValue

                newMonth10 = seasonList[3].monthValues[0].editedValue
                newMonth11 = seasonList[3].monthValues[1].editedValue
                newMonth12 = seasonList[3].monthValues[2].editedValue
            }
        }
    }

    private fun cleanEditedInfo(list: ArrayList<SeasonModel>): ArrayList<SeasonModel> {
        list.forEach { season ->
            season.monthValues.forEach { month ->
                month.editedValue = "00"
            }
        }
        return list
    }

    fun setListener(
        listener: AdapterInterface.OnObjectionInsuranceHistoryListener,
        cleanListener: AdapterInterface.OnDeleteClickListener<ObjectionInsuranceHistoryModel>
    ) {
        onResultListener = listener
        onCleanEditedListener = cleanListener
    }

    private fun initData() {
        (arguments?.getParcelable(Constants.DATA_CLASS) as? ObjectionInsuranceHistoryModel)?.let {
            mainInfo = it
            seasonList = getSeasonInfo(it)
            seasonAdapter.setItems(seasonList)
        }
    }

    private fun getSeasonInfo(info: ObjectionInsuranceHistoryModel) =
        arrayListOf<SeasonModel>(
            SeasonModel(
                season = SeasonEnumClass.SPRING,
                monthValues = getMonthsSeason(season = SeasonEnumClass.SPRING, info = info)
            ),
            SeasonModel(
                season = SeasonEnumClass.SUMMER,
                monthValues = getMonthsSeason(season = SeasonEnumClass.SUMMER, info = info)
            ),
            SeasonModel(
                season = SeasonEnumClass.FALL,
                monthValues = getMonthsSeason(season = SeasonEnumClass.FALL, info = info)
            ),
            SeasonModel(
                season = SeasonEnumClass.WINTER,
                monthValues = getMonthsSeason(season = SeasonEnumClass.WINTER, info = info)
            )
        )

    private fun getMonthsSeason(
        season: SeasonEnumClass,
        info: ObjectionInsuranceHistoryModel
    ): ArrayList<MonthModel> {
        var month1: MonthModel? = null
        var month2: MonthModel? = null
        var month3: MonthModel? = null

        when (season) {
            SeasonEnumClass.SPRING -> {
                month1 = MonthModel(
                    monthName = getString(R.string.farvardin),
                    registeredValue = info.oldMonth1 ?: "0",
                    editedValue = info.newMonth1 ?: "0"
                )
                month2 = MonthModel(
                    monthName = getString(R.string.label_ordibehesht),
                    registeredValue = info.oldMonth2 ?: "0",
                    editedValue = info.newMonth2 ?: "0"
                )
                month3 = MonthModel(
                    monthName = getString(R.string.label_khordad),
                    registeredValue = info.oldMonth3 ?: "0",
                    editedValue = info.newMonth3 ?: "0"
                )
            }

            SeasonEnumClass.SUMMER -> {
                month1 = MonthModel(
                    monthName = getString(R.string.label_tir),
                    registeredValue = info.oldMonth4 ?: "0",
                    editedValue = info.newMonth4 ?: "0"
                )
                month2 = MonthModel(
                    monthName = getString(R.string.label_mordad),
                    registeredValue = info.oldMonth5 ?: "0",
                    editedValue = info.newMonth5 ?: "0"
                )
                month3 = MonthModel(
                    monthName = getString(R.string.label_shahrivar),
                    registeredValue = info.oldMonth6 ?: "0",
                    editedValue = info.newMonth6 ?: "0"
                )
            }

            SeasonEnumClass.FALL -> {
                month1 = MonthModel(
                    monthName = getString(R.string.label_mehr),
                    registeredValue = info.oldMonth7 ?: "0",
                    editedValue = info.newMonth7 ?: "0",
                    maxDayAvailable = 30
                )
                month2 = MonthModel(
                    monthName = getString(R.string.label_aban),
                    registeredValue = info.oldMonth8 ?: "0",
                    editedValue = info.newMonth8 ?: "0",
                    maxDayAvailable = 30
                )
                month3 = MonthModel(
                    monthName = getString(R.string.label_azar),
                    registeredValue = info.oldMonth9 ?: "0",
                    editedValue = info.newMonth9 ?: "0",
                    maxDayAvailable = 30
                )
            }

            SeasonEnumClass.WINTER -> {
                month1 = MonthModel(
                    monthName = getString(R.string.label_dey),
                    registeredValue = info.oldMonth10 ?: "0",
                    editedValue = info.newMonth10 ?: "0",
                    maxDayAvailable = 30
                )
                month2 = MonthModel(
                    monthName = getString(R.string.label_bahman),
                    registeredValue = info.oldMonth11 ?: "0",
                    editedValue = info.newMonth11 ?: "0",
                    maxDayAvailable = 30
                )
                var monthName = getString(R.string.label_esfand)
                var maxDay = 29
                if (mainInfo?.isLeapYear() == true) {
                    monthName = getString(R.string.label_esfand_leap_year)
                    maxDay = 30
                }

                month3 = MonthModel(
                    monthName = monthName,
                    registeredValue = info.oldMonth12 ?: "0",
                    editedValue = info.newMonth12 ?: "0",
                    isLeapYear = mainInfo?.isLeapYear() == true,
                    maxDayAvailable = maxDay
                )
            }

        }
        return arrayListOf(month1, month2, month3)
    }
//endregion


}