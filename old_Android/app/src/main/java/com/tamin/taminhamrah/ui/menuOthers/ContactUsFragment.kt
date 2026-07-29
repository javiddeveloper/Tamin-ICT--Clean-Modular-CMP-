package com.tamin.taminhamrah.ui.menuOthers

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.databinding.FragmentContactUsBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.utils.UiUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactUsFragment :
    BaseFragment<FragmentContactUsBinding, MenuOthersViewModel>(),
    AdapterInterface.OnActionResultContactList<MenuModel> {

    override val mViewModel: MenuOthersViewModel by viewModels()
    private lateinit var newFeaturesAdapter: ContactAdapter

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_contact_us
    }

    override fun setupObserver() {


    }

    override fun initView() {

        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )

        newFeaturesAdapter = ContactAdapter()

        viewDataBinding?.recycler?.apply {
            this.adapter = newFeaturesAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(UiUtils.createDivider(this.context))
            }
        }

        newFeaturesAdapter.setItems(mViewModel.getContactUsInfo(), this)
    }

    override fun getData() {
    }

    override fun onClick() {

        viewDataBinding?.apply {
            groupAnswering.setOnClickListener {
                // call1420()
                startActivity(
                    Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:" + getString(R.string.tel_1420))
                    )
                )
            }
        }
    }

    override fun onSocialNetworkClick(uri: Uri) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = uri
            requireActivity().startActivity(intent)
    }

    override fun onItemClick(item: MenuModel) {
        when (item.id) {
            "1" -> {
                startActivity(
                    Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:" + getString(R.string.tel_64501))
                    )
                )
            }
            "5" -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(Constants.LINK_OFFICAL_PORTAL)
                requireActivity().startActivity(intent)
            }
            "6" -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(Constants.LINK_NEWS_STATION)
                requireActivity().startActivity(intent)
            }
        }
    }

}