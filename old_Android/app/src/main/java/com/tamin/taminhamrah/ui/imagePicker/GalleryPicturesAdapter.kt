package com.tamin.taminhamrah.ui.imagePicker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.GalleryPicture
import com.tamin.taminhamrah.utils.ImageUtils

class GalleryPicturesAdapter(private val list: List<GalleryPicture>) : RecyclerView.Adapter<GVH>() {


    private val IMAGE_LIST = 0
    private val IMAGE_PICKER = 1
    private val CAMERA_PREVIWE = 2

    init {
        initSelectedIndexList()
    }

    constructor(list: List<GalleryPicture>, selectionLimit: Int) : this(list) {
        setSelectionLimit(selectionLimit)
    }

    private lateinit var onClick: (Int) -> Unit
    private lateinit var afterSelectionCompleted: () -> Unit
    private var isSelectionEnabled = false
    private lateinit var selectedIndexList: ArrayList<Int> // only limited items are selectable.
    private var selectionLimit = 0

    private fun initSelectedIndexList() {
        selectedIndexList = ArrayList(selectionLimit)
    }

    fun setSelectionLimit(selectionLimit: Int) {
        this.selectionLimit = selectionLimit
        removedSelection()
        initSelectedIndexList()
    }

    fun setOnClickListener(onClick: (Int) -> Unit) {
        this.onClick = onClick
    }

    fun setAfterSelectionListener(afterSelectionCompleted: () -> Unit) {
        this.afterSelectionCompleted = afterSelectionCompleted
    }

    private fun checkSelection(position: Int) {
        if (isSelectionEnabled) {
            if (getItem(position).isSelected)
                selectedIndexList.add(position)
            else {
                selectedIndexList.remove(position)
                isSelectionEnabled = selectedIndexList.isNotEmpty()
            }
        }
    }

    //    Useful Methods to provide delete feature.

//    fun deletePicture(picture: GalleryPicture) {
//        deletePicture(list.indexOf(picture))
//    }
//
//    fun deletePicture(position: Int) {
//        if (File(getItem(position).path).delete()) {
//            list.removeAt(position)
//            notifyItemRemoved(position)
//        } else {
//            Log.e("GalleryPicturesAdapter", "Deletion Failed")
//        }
//    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): GVH {
        /*   when (p1) {
               CAMERA_PREVIWE -> {
                   val vh =


                return  GVH(LayoutInflater.from(p0.context).inflate(R.layout.camera_preview_list, p0, false))
            }
            IMAGE_PICKER -> {
                val vh =
                    GVH(LayoutInflater.from(p0.context).inflate(R.layout.image_picker_list, p0, false))
                vh.containerView.setOnClickListener {
                    val position = vh.adapterPosition
                    onClick(position)

                    *//*  val picture = getItem(position)
                      if (isSelectionEnabled) {
                          handleSelection(position, it.context)
                          notifyItemChanged(position)
                          checkSelection(position)
                          afterSelectionCompleted()

                      } else*//*
                }
                return vh
            }
            else -> {*/
        val vh =
            GVH(
                LayoutInflater.from(p0.context)
                    .inflate(R.layout.multi_gallery_listitem, p0, false)
            )
        vh.containerView.setOnClickListener {
            val position = vh.adapterPosition
            onClick(position)
            /*  val picture = getItem(position)
              if (isSelectionEnabled) {
                  handleSelection(position, it.context)
                  notifyItemChanged(position)
                  checkSelection(position)
                  afterSelectionCompleted()
              } else*/
        }
//        vh.containerView.setOnLongClickListener {
//            val position = vh.adapterPosition
//            isSelectionEnabled = true
//            // handleSelection(position, it.context)
//            notifyItemChanged(position)
//            checkSelection(position)
//            afterSelectionCompleted()
//            isSelectionEnabled
//        }
        return vh
    }


    private fun getItem(position: Int) = list[position]


    override fun onBindViewHolder(p0: GVH, p1: Int) {
        if (p0.itemViewType == IMAGE_LIST) {
            val picture = list[p1]
            picture.path?.let {
                /*   Glide.with(p0.containerView).load(it).placeholder(R.drawable.place_holder)
                       .into(p0.itemView.findViewById(R.id.ivImg))*/
                ImageUtils.loadImage(view = p0.itemView.findViewById(R.id.ivImg), it)

            }
        } else {
            list[p1].imgres?.let {
                p0.itemView.findViewById<ImageView>(R.id.image).setImageResource(it)
            }
            list[p1].title?.let {
                p0.itemView.findViewById<TextView>(R.id.title).text = it
            }
        }
    }

    override fun getItemCount() = list.size

    fun getSelectedItems() = selectedIndexList.map {
        list[it]
    }

    fun removedSelection(): Boolean {
        return if (isSelectionEnabled) {
            selectedIndexList.forEach {
                list[it].isSelected = false
            }
            isSelectionEnabled = false
            selectedIndexList.clear()
            notifyDataSetChanged()
            true
        } else false
    }
}