package com.example.bliblihomepage.ui.homePage

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bliblihomepage.R
import com.example.bliblihomepage.databinding.FragmentSignupBinding
import dagger.hilt.android.AndroidEntryPoint
import com.example.bliblihomepage.util.SharedPrefManager

@AndroidEntryPoint
class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        //Prefill from login screen (email or phone)
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

        //Password Validation
        binding.etPassword.apply {
            isFocusableInTouchMode = true

            setOnFocusChangeListener { _, hasFocus ->
                binding.passwordValidationCard.visibility =
                    if (hasFocus) View.VISIBLE else View.GONE
            }

            addTextChangedListener { text ->
                val password = text.toString()
                if (password.isEmpty()) {
                    binding.passwordValidationCard.visibility = View.GONE
                    return@addTextChangedListener
                } else {
                    binding.passwordValidationCard.visibility = View.VISIBLE
                }

                val hasUpper = password.any { it.isUpperCase() }
                val hasLower = password.any { it.isLowerCase() }
                val hasDigit = password.any { it.isDigit() }
                val isLength = password.length in 8..50

                updatePasswordRule(binding.tvUppercase, hasUpper, "Huruf besar")
                updatePasswordRule(binding.tvLowercase, hasLower, "Huruf kecil")
                updatePasswordRule(binding.tvNumber, hasDigit, "Angka")
                updatePasswordRule(binding.tvLength, isLength, "8–50 karakter")
            }
        }

        //Signup Button
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

            // Save user in SharedPref
            SharedPrefManager.signup(requireContext(), email, password)

            // Log them in immediately
            SharedPrefManager.login(requireContext(), email, password)


            Toast.makeText(requireContext(), "Akun berhasil dibuat!", Toast.LENGTH_SHORT).show()

            //Navigate to ProductList (using Navigation Component)
            findNavController().navigate(R.id.action_signupFragment_to_productListFragment)
        }

        //Close Button (Cancel signup)
        binding.btnClose.setOnClickListener {
            val cancelSheet = CancelRegistrationBottomSheet {
                // Navigate to login
                findNavController().navigate(R.id.loginFragment)
            }
            cancelSheet.show(parentFragmentManager, "CancelSheet")
        }

    }

    private fun updatePasswordRule(tv: TextView, valid: Boolean, label: String) {
        if (valid) {
            tv.text = "✓ $label"
            tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        } else {
            tv.text = "✗ $label"
            tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }
    }

    private fun showError(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}