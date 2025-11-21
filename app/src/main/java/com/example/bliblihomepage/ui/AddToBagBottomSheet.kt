package com.example.bliblihomepage.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bliblihomepage.databinding.BottomAddToBagBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddToBagBottomSheet(
    private val productTitle: String,
    private val onConfirmed: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomAddToBagBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomAddToBagBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.text = "Add \"$productTitle\" to bag?"
        binding.tvSubtitle.text = "Do you want to add \"$productTitle\" to your bag?"
        binding.btnConfirm.setOnClickListener {
            dismiss()
            onConfirmed.invoke()
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}