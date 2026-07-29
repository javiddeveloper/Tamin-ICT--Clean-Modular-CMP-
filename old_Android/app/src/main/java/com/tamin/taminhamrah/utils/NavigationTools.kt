package com.tamin.taminhamrah.utils

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson

inline fun <reified T> Fragment.observeBackStackFromPopFor(
    key: String = T::class.simpleName ?: "RESULT", crossinline callback: (T) -> Unit
) {

    val navController = this.findNavController()
    val navBackStackEntry = navController.currentBackStackEntry

    val _observer = LifecycleEventObserver { _, event ->
        val isContain: Boolean = navBackStackEntry?.savedStateHandle?.contains(key) ?: false
        if (event == Lifecycle.Event.ON_RESUME && isContain) {
            navBackStackEntry.savedStateHandle.get<String>(key)?.let { data ->
                navBackStackEntry.savedStateHandle.remove<String>(key)
                callback((Gson().fromJson(data, T::class.java)))
            }
        }
    }
    navBackStackEntry?.lifecycle?.addObserver(_observer)
    viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            try {
                navBackStackEntry?.lifecycle?.removeObserver(_observer)
            } catch (e: Exception) {
                Log.d("---Exception--", "observeBackStackFromPopFor:${e.message}")
            }
        }
    })
}

inline fun <reified T> BottomSheetDialogFragment.dismissWithResult(
    data: T, key: String = T::class.simpleName ?: "RESULT"
) {
    setBackStackResult(data, key)
    dismiss()
}

inline fun <reified T> Fragment.setBackStackResult(
    data: T, key: String = T::class.simpleName ?: "RESULT"
) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(
        key, Gson().toJson(data).toString()
    )
}
