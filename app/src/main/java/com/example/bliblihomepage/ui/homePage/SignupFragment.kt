package com.example.bliblihomepage.ui.homePage

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bliblihomepage.R
import com.example.bliblihomepage.databinding.FragmentSignupBinding
import com.example.bliblihomepage.util.SharedPrefManager

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val prefillUsername = arguments?.getString("prefill_username")
        val isEmail = arguments?.getBoolean("is_email", false) ?: false

        if (!prefillUsername.isNullOrEmpty()) {
            if (isEmail) {
                binding.etEmail.setText(prefillUsername)
                binding.etEmail.isEnabled = false
                binding.etEmail.alpha = 0.6f
            } else {
                val cleanPhone = prefillUsername.replace(Regex("^\\+62"), "")
                binding.etPhone.setText(cleanPhone)
                binding.etPhone.isEnabled = false
                binding.etPhone.alpha = 0.6f
            }
        }

        // password validation UI already exists in your layout; leave as is
        binding.btnSignup.setOnClickListener {
            val name = binding.etFullName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showError("Semua kolom wajib diisi")
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showError("Format email tidak valid")
                return@setOnClickListener
            }

            val validPassword = password.any { it.isUpperCase() } &&
                    password.any { it.isLowerCase() } &&
                    password.any { it.isDigit() } &&
                    password.length in 8..50

            if (!validPassword) {
                showError("Kata sandi belum memenuhi syarat")
                return@setOnClickListener
            }

            // Save user + login session
            SharedPrefManager.signup(requireContext(), email, password)
            val loginSuccess = SharedPrefManager.login(requireContext(), email, password)
            if (loginSuccess) {
                Toast.makeText(requireContext(), "Akun berhasil dibuat!", Toast.LENGTH_SHORT).show()
                if (isAdded) {
                    // Navigate to cart (app home)
                    findNavController().navigate(R.id.cartFragment)
                }
            } else {
                // very unlikely (signup saved but login failed)
                showError("Terjadi masalah saat membuat akun. Silakan coba lagi.")
            }
        }

        binding.btnClose.setOnClickListener {
            if (isAdded) findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun showError(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
