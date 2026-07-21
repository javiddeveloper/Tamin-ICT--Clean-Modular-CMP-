package com.tamin.taminhamrah.utils.extentions

import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment

/**
 * Sets a fragment result with a single key-value pair on the parent FragmentManager
 * and then dismisses the DialogFragment.
 *
 * @param requestKey The request key to use for the fragment result.
 * @param key The key for the value in the bundle.
 * @param value The value to put in the bundle. Can be of any type supported by bundleOf.
 */
fun DialogFragment.setFragmentResultAndDismiss(requestKey: String, key: String?=null, value: Any?=null) {

    parentFragmentManager.setFragmentResult(
        requestKey,
        if (key != null) bundleOf(key to value) else bundleOf()
    )
    dismiss()
}
