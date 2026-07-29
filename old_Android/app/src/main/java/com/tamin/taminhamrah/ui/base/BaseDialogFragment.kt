package com.tamin.taminhamrah.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianDatePickerDialog

abstract class BaseDialogFragment<VB : ViewBinding>(private val inflate: Inflate<VB>) :
    DialogFragment() {

    private var _binding: VB? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    val viewBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun handlePageDestination(id: Int, finishActivity: Boolean = false) {
        findNavController().navigate(id)
        if (finishActivity) {
            requireActivity().finish()
        }
    }

    fun handlePageDestination(id: Int, bundle: Bundle?, finishActivity: Boolean = false) {
        findNavController().navigate(id, bundle)
        if (finishActivity) {
            requireActivity().finish()
        }
    }

    fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    fun getDatePicker(): MyPersianDatePickerDialog? {
        return Utility.getDatePicker(requireContext())
    }
}