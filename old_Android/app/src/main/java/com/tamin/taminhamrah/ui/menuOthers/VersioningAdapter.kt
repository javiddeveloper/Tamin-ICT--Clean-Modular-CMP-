package com.tamin.taminhamrah.ui.menuOthers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.local.othersInfo.entity.VersionInfoModel
import com.tamin.taminhamrah.databinding.VersionItemBinding
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible

class VersioningAdapter :
    RecyclerView.Adapter<VersioningAdapter.ItemViewHolder>() {
    var openIndex = 0
    private val mItems: ArrayList<VersionInfoModel> by lazy { ArrayList() }
    fun setItems(
        items: List<VersionInfoModel>
    ) {
        mItems.clear()
        mItems.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(
            VersionItemBinding.inflate(inflater, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val version = mItems[position]
        val context = holder.binding.root.context
        holder.binding.apply {
            tvVersionName.text = context.getString(R.string.version_name, version.versionName)
            tvReleaseDate.text = context.getString(R.string.release_date, version.releaseDate)

            if (position == openIndex) {
                imgArrow.animate().rotation(180f).setDuration(500).start()

                if (version.newFeatures.isEmpty()) {
                    groupNewFeature.gone()
                }else{
                    groupNewFeature.visible()
                }
                if (version.debug.isEmpty()) {
                    groupBugs.gone()
                }else{
                    groupBugs.visible()
                }
            } else {
                groupNewFeature.gone()
                groupBugs.gone()
                recyclerDebugs.gone()
                tvTitleDebugs.gone()
                imgArrow.animate().rotation(0f).setDuration(500).start()
            }
            clickHolder.setOnClickListener {
                var lastOpenIndex = openIndex

                openIndex = if (openIndex == position)
                    -1
                else
                    position

                if (lastOpenIndex != -1)
                    notifyItemChanged(lastOpenIndex)
                notifyItemChanged(openIndex)

            }

            val featureLayoutManager = LinearLayoutManager(holder.binding.root.context)
            val debugLayoutManager = LinearLayoutManager(holder.binding.root.context)

            val featureAdapter = VersioningFeatureAdapter()
            val debugAdapter = VersioningFeatureAdapter()


            recyclerFeatures.adapter = featureAdapter
            recyclerFeatures.layoutManager = featureLayoutManager
            featureAdapter.setItems(version.newFeatures)

            recyclerDebugs.adapter = debugAdapter
            recyclerDebugs.layoutManager = debugLayoutManager
            debugAdapter.setItems(version.debug)


        }

    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    class ItemViewHolder(var binding: VersionItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    inner class DiffCallback(
        private val mOldList: List<VersionInfoModel>,
        private val mNewList: List<VersionInfoModel>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return mOldList.size
        }

        override fun getNewListSize(): Int {
            return mNewList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition].versionCode == mNewList[newItemPosition].versionCode
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = mOldList[oldItemPosition]
            val newItem = mNewList[newItemPosition]

            return (newItem == oldItem)
        }

    }
}
