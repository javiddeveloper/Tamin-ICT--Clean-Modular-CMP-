package com.tamin.taminhamrah.ui.treatment.electronicPrescription.adapter

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.electronicPrescription.ElectronicPrescriptionDetail
import com.tamin.taminhamrah.databinding.ListItemElectronicPrescriptionDetailBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.base.BasePagingAdapter
import com.tamin.taminhamrah.ui.treatment.electronicPrescription.model.PrescriptionItemsEnumClass
import com.tamin.taminhamrah.utils.Utility

class ElectronicPrescriptionDetailAdapter(var onItemClickListener: AdapterInterface.OnItemClickListener<ElectronicPrescriptionDetail>? = null) :
    BasePagingAdapter<ElectronicPrescriptionDetail, ListItemElectronicPrescriptionDetailBinding>(
        DiffUtilCallBack
    ) {

    override fun getLayoutResId()= R.layout.list_item_electronic_prescription_detail


    override fun bindItem(
        binding: ListItemElectronicPrescriptionDetailBinding,
        item: ElectronicPrescriptionDetail?,
        position: Int
    ) {
        var mContext: Context?
        item?.let {
            binding.apply {
                mContext = binding.root.context
                groupDetailMedicalInfo.isVisible = item.prescriptionType == "1"  //prescriptionType == "1" => is medical prescription
                val received = item.deliveredNo ?: 0
                val prescription = item.serviceQuantity ?: 0
                tvValueReceivedNumber.text = received.toString()
                tvValuePrescriptionNumber.text = prescription.toString()
                tvValueServiceName.text = item.serviceName ?: "-"
                tvValueUseOrder.text = item.drugInstruction ?: "-"
                tvValueActionDate.text = Utility.getDateSeparator(item.registerDate)
                tvValuePharmacy.text = item.serverName?:"-"
                mContext?.let {context->
                    initialCastsView(binding,context,item)
                    if (received < prescription) {
                        tvValueReceivedNumber.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.text_color_dialog_red
                            )
                        )
                        val drawable = ContextCompat.getDrawable(tvValueReceivedNumber.context, R.drawable.bg_btn_dialog_red)
                        tvValueReceivedNumber.background = drawable
                    }
                }
            }
        }
    }

    private fun initialCastsView(binding: ListItemElectronicPrescriptionDetailBinding,context: Context,item:ElectronicPrescriptionDetail) {
        binding.patientShareView.apply {
            tvTitle.text = context.getString(PrescriptionItemsEnumClass.PRIMARY.title)
            tvPrice.setTextColor(ContextCompat.getColor(context, PrescriptionItemsEnumClass.PRIMARY.idTextColor))
            tvPrice.text = Utility.getRialWithSeparator(item.insurancePayment)
        }

        binding.organizationShareView.apply {
            tvTitle.text = context.getString(PrescriptionItemsEnumClass.ORANGE.title)
            tvPrice.setTextColor(ContextCompat.getColor(
               context, PrescriptionItemsEnumClass.ORANGE.idTextColor))
            tvPrice.text = Utility.getRialWithSeparator(item.ssoPayment)
        }

       binding.totalView.apply {
            tvTitle.text = context.getString(PrescriptionItemsEnumClass.GREEN.title)
            tvPrice.setTextColor(ContextCompat.getColor(
                context, PrescriptionItemsEnumClass.GREEN.idTextColor))
           tvPrice.text = Utility.getRialWithSeparator(item.sumPriceItem)
       }
    }

    override fun initViewHolder(
        binding: ListItemElectronicPrescriptionDetailBinding,
        itemView: View
    ) {
        binding.apply {
            btnShowMore.setOnClickListener {
                if (groupDetail.visibility == View.GONE) {
                    groupDetail.visibility = View.VISIBLE
                    btnShowMore.text = btnShowMore.context.getString(R.string.hide_detail)
                } else {
                    groupDetail.visibility = View.GONE
                    btnShowMore.text = btnShowMore.context.getString(R.string.show_detail)
                }
                itemView.requestFocus()
            }
        }

    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<ElectronicPrescriptionDetail>() {
        override fun areItemsTheSame(
            oldItem: ElectronicPrescriptionDetail,
            newItem: ElectronicPrescriptionDetail
        ) = oldItem == newItem


        override fun areContentsTheSame(
            oldItem: ElectronicPrescriptionDetail,
            newItem: ElectronicPrescriptionDetail
        ) = oldItem == newItem

    }
}