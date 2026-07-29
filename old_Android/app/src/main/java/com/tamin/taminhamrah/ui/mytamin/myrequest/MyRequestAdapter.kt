package com.tamin.taminhamrah.ui.mytamin.myrequest

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.user.MyRequestItem
import com.tamin.taminhamrah.databinding.ListItemMyRequestBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.ui.mytamin.myrequest.showRequestInfo.RequestTypeEnumClass

class MyRequestAdapter(var onItemClickListener: AdapterInterface.OnItemClickListener<MyRequestItem>? = null) :
    BasePagingAdapter<MyRequestItem, ListItemMyRequestBinding>(DiffUtilCallBack) {

    override fun getLayoutResId(): Int {
        return R.layout.list_item_my_request
    }

    override fun bindItem(
        binding: ListItemMyRequestBinding,
        item: MyRequestItem?,
        position: Int
    ) {
        with(binding) {
            this.item = item ?: return

            btnShowRequest.text = btnShowRequest.context.getString(R.string.show_request)

            btnGuide.setOnClickListener {
                onItemClickListener?.onItemClick(
                    item,
                    tag = binding.btnGuide.text.toString()
                )
            }
            btnShowError.setOnClickListener {
                onItemClickListener?.onItemClick(
                    item,
                    tag = binding.btnShowError.text.toString()
                )
            }
            btnShowRequest.setOnClickListener {
                onItemClickListener?.onItemClick(
                    item,
                    tag = binding.btnShowRequest.text.toString()
                )
            }
            when (item.requestType?.id) {
                18 -> {
                    //'نمایش خطاها'
                    btnShowError.isVisible = (item.status?.requestCode == "0006")

                }

                9 -> {
                    //نمایش خطاها
                    btnShowError.isVisible = (item.status?.requestCode == "0019")
                }

                19 -> {
                    //نمایش خطاها
                    btnShowError.isVisible = (item.status?.requestCode == "0019")
                }

                RequestTypeEnumClass.MEDICAL_COMMISSION.serviceId -> {
                    btnShowRequest.isVisible = false

                    btnShowError.isVisible = false
                }
                RequestTypeEnumClass.ORTHOTICS_PROSTHESIS.serviceId -> {
                    btnShowRequest.isVisible = (item.status?.requestCode == "0021"||item.status?.requestCode == "0019")

//                    btnShowError.isVisible = (item.status?.requestCode == "0019")
                }

                RequestTypeEnumClass.ARTICLE16.serviceId -> {
                    btnShowRequest.isVisible = item.status?.requestCode == "2602"
                }

                RequestTypeEnumClass.PREGNANCY.serviceId -> {
                    btnShowRequest.isVisible = item.status?.requestCode == "0014"
                }

                RequestTypeEnumClass.ILL_DAY.serviceId -> {
                    btnShowRequest.isVisible = item.status?.requestCode == "0021"
                }

                RequestTypeEnumClass.DEFERRED_INSTALLMENT_CERTIFICATE.serviceId -> {
                    btnShowRequest.isVisible = item.status?.requestCode == "0018"
                }

                RequestTypeEnumClass.FOLLOW_UP_RESULT_OBJECTION_HISTORY_NONE_EXIST.serviceId -> {
                    btnShowError.visibility = View.GONE
                    btnShowRequest.apply {
                        isVisible = true
                        text = context.getString(R.string.follow_up_objection)
                    }
                }

                else -> {
                    btnShowError.visibility = View.GONE
                    btnShowRequest.visibility = View.GONE
                }
            }
        }
    }

    override fun initViewHolder(binding: ListItemMyRequestBinding, itemView: View) {

        /* binding.btnShowDetail.setOnClickListener {
             if (binding.groupDetails.visibility == View.GONE) {
                 binding.groupDetails.visibility = View.VISIBLE
                 binding.btnShowDetail.text = binding.btnShowDetail.context.getString(R.string.label_hide_detail)
             } else {
                 binding.groupDetails.visibility = View.GONE
                 binding.btnShowDetail.text = binding.btnShowDetail.context.getString(R.string.label_show_detail)
             }

             itemView.requestFocus()
         }*/
    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<MyRequestItem>() {
        override fun areItemsTheSame(
            oldItem: MyRequestItem,
            newItem: MyRequestItem
        ): Boolean {
            return oldItem.refrenceid == newItem.refrenceid
        }

        override fun areContentsTheSame(
            oldItem: MyRequestItem,
            newItem: MyRequestItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}

