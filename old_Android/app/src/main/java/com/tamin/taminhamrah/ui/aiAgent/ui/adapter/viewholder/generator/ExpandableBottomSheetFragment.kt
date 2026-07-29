package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class ExpandableBottomSheetFragment : BottomSheetDialogFragment() {

    fun interface ContentBinder {
        fun bind(context: Context, container: ViewGroup)
    }

    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var onDismissCallback: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FrameLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val key = arguments?.getString(ARG_BINDER_KEY) ?: run {
            dismissAllowingStateLoss()
            return
        }
        val binder = binderRegistry[key]
        if (binder != null) {
            binder.bind(requireContext(), view as ViewGroup)
        } else {
            dismissAllowingStateLoss()
        }
    }

    override fun onStart() {
        super.onStart()
        val bottomSheet = (dialog as? BottomSheetDialog)
            ?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?: return

        bottomSheet.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheet.requestLayout()

        val behavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior = behavior
        behavior.isFitToContents = false
        behavior.halfExpandedRatio = 0.5f
        behavior.skipCollapsed = true
        behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    override fun onDismiss(dialog: DialogInterface) {
        onDismissCallback?.invoke()
        onDismissCallback = null
        super.onDismiss(dialog)
    }

    override fun onDestroyView() {
        arguments?.getString(ARG_BINDER_KEY)?.let { binderRegistry.remove(it) }
        bottomSheetBehavior = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_BINDER_KEY = "binder_key"
        private val binderRegistry = ConcurrentHashMap<String, ContentBinder>()

        fun show(
            fragmentManager: FragmentManager,
            tag: String? = null,
            onDismiss: (() -> Unit)? = null,
            binder: ContentBinder
        ) {
            val key = UUID.randomUUID().toString()
            binderRegistry[key] = binder
            ExpandableBottomSheetFragment().apply {
                arguments = Bundle().apply { putString(ARG_BINDER_KEY, key) }
                onDismissCallback = onDismiss
            }.show(fragmentManager, tag)
        }
    }
}
