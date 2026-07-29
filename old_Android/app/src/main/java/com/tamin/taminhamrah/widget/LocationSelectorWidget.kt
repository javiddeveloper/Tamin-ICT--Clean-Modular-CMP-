package com.tamin.taminhamrah.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.databinding.WidgetLocationSelectorBinding
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.dialog.locationselector.LocationSelectorDialogFragment
import dagger.hilt.android.internal.managers.FragmentComponentManager

class LocationSelectorWidget(mContext: Context, attrs: AttributeSet?) :
    BaseWidget(mContext, attrs) {

    private lateinit var viewBinding: WidgetLocationSelectorBinding

    interface OnLocationSelectListener {
        fun onLocationSelect(type: ListType, title: String?, code: String?)
    }

    private var mListener: OnLocationSelectListener? = null

    fun setListener(listener: OnLocationSelectListener) {
        mListener = listener
    }

    override fun initLayout(context: Context?, attrs: AttributeSet?) {

        context?.let {
            viewBinding = WidgetLocationSelectorBinding.inflate(LayoutInflater.from(it), this, true)
            attrs?.let { it1 -> setAttribute(it, it1) }
        }
    }

    private fun setAttribute(context: Context, attrs: AttributeSet) {

        viewBinding.apply {

            val appCompatActivity =
                FragmentComponentManager.findActivity(context) as? AppCompatActivity
            selectProvince.getIt().setOnClickListener {

                selectProvince.getLayout().isErrorEnabled = false

                val dialog =
                    LocationSelectorDialogFragment.newInstance(ListType.TYPE_PROVINCE).apply {
                        setListener(object : MenuInterface.OnResult {
                            override fun onResult(itemResult: MenuModel) {

                                provinceId = itemResult.id
                                cityId = ""
                                branchId = ""

                                mListener?.onLocationSelect(
                                    ListType.TYPE_PROVINCE,
                                    itemResult.title,
                                    getValidProvinceId(itemResult.id)
                                )
                                mListener?.onLocationSelect(ListType.TYPE_CITY, "", "")
                                mListener?.onLocationSelect(ListType.TYPE_BRANCH, "", "")

                                selectProvince.setValue(itemResult.title ?: "")
                                selectCity.getIt().setText("")
                                selectBranch.getIt().setText("")

                            }
                        })
                    }



                appCompatActivity?.supportFragmentManager?.let {
                    dialog.show(it, "province_dialog")
                }
            }

            selectCity.getIt().setOnClickListener {
                selectCity.getLayout().isErrorEnabled = false
                if (provinceId.isNullOrEmpty())
                    return@setOnClickListener
                val dialog =
                    LocationSelectorDialogFragment.newInstance(ListType.TYPE_CITY, provinceId)
                        .apply {
                            setListener(object : MenuInterface.OnResult {
                                override fun onResult(itemResult: MenuModel) {

                                    cityId = itemResult.id
                                    branchId = ""

                                    mListener?.onLocationSelect(
                                        ListType.TYPE_CITY,
                                        getValidCityId(itemResult.title),
                                        itemResult.id
                                    )
                                    mListener?.onLocationSelect(ListType.TYPE_BRANCH, "", "")
                                    selectCity.setValue(itemResult.title ?: "")
                                    selectBranch.getIt().setText("")
                                }
                            })
                        }

                appCompatActivity?.supportFragmentManager?.let {
                    dialog.show(it, "province_dialog")
                }
            }

            selectBranch.getIt().setOnClickListener {
                selectBranch.getLayout().isErrorEnabled = false
                if (provinceId.isNullOrEmpty() || cityId.isNullOrEmpty())
                    return@setOnClickListener

                val dialog =
                    LocationSelectorDialogFragment.newInstance(ListType.TYPE_BRANCH, cityId).apply {
                        setListener(object : MenuInterface.OnResult {
                            override fun onResult(itemResult: MenuModel) {
                                if (itemResult.title != null) {
                                    branchId = itemResult.id

                                    mListener?.onLocationSelect(
                                        ListType.TYPE_BRANCH,
                                        getValidBranchId(itemResult.title),
                                        itemResult.id
                                    )
                                    selectBranch.setValue(itemResult.title ?: "")
                                }
                            }
                        })
                    }

                appCompatActivity?.supportFragmentManager?.let {
                    dialog.show(it, "province_dialog")
                }
            }
        }
    }

    private var provinceId: String? = null
    private var cityId: String? = null
    private var branchId: String? = null

    private fun getValidProvinceId(provinceId: String?): String? {
        if (provinceId.isNullOrEmpty()) {
            viewBinding.selectProvince.setError(context.getString(R.string.error_select_province_name))
        }
        return provinceId
    }

    private fun getValidCityId(cityId: String?): String? {
        if (cityId.isNullOrEmpty()) {
            viewBinding.selectCity.setError(context.getString(R.string.error_select_city_name))
        }
        return cityId
    }

    private fun getValidBranchId(branchId: String?): String? {
        if (branchId.isNullOrEmpty()) {
            viewBinding.selectBranch.setError(context.getString(R.string.error_select_branch_name))
        }
        return branchId
    }

    fun getProvinceName() = viewBinding.selectProvince.getIt().text.toString()
    fun getCityName() = viewBinding.selectCity.getIt().text.toString()
    fun getBranchName() = viewBinding.selectBranch.getIt().text.toString()

    fun getProvinceId() = getValidProvinceId(provinceId)
    fun getCityId() = getValidCityId(cityId)
    fun getBranchId() = getValidBranchId(branchId)

    fun getProvinceLayout() = viewBinding.selectProvince.getLayout()
    fun getCityLayout() = viewBinding.selectCity.getLayout()
    fun getBranchLayout() = viewBinding.selectBranch.getLayout()


    fun setProvince(title: String, id: String?) {
        provinceId = id
        viewBinding.selectProvince.getIt().setText(title)
    }

    fun setCity(title: String, id: String?) {
        cityId = id
        viewBinding.selectCity.getIt().setText(title)
    }

    fun setBranch(title: String, id: String?) {
        branchId = id
        viewBinding.selectBranch.getIt().setText(title)
    }

    enum class ListType {
        TYPE_PROVINCE, TYPE_CITY, TYPE_BRANCH
    }

}
