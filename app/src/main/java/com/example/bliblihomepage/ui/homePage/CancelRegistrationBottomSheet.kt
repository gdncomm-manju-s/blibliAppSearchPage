package com.example.bliblihomepage.ui.homePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bliblihomepage.databinding.BottomCancelSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CancelRegistrationBottomSheet(
    private val onConfirm: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomCancelSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomCancelSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Ya batalkan - Go to login
        binding.btnYes.setOnClickListener {
            dismiss()
            onConfirm.invoke()
        }

        //Lanjutkan pendaftaran - Dismiss only
        binding.btnContinue.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}