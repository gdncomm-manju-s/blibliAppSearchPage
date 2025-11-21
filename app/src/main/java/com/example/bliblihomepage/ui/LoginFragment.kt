package com.example.bliblihomepage.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bliblihomepage.R
import com.example.bliblihomepage.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        // Clear error when typing
        binding.etUsername.addTextChangedListener {
            binding.tvError.visibility = View.GONE
            binding.etUsername.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.edittext_bg
            )
        }

        // LOGIN BUTTON CLICK
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()

            // Empty validation
            if (username.isBlank()) {
                showError("Nomor HP atau email harus diisi.")
                return@setOnClickListener
            }

            // Email/Phone validation
            val isEmail = Patterns.EMAIL_ADDRESS.matcher(username).matches()
            val isPhone = username.matches(Regex("^\\+?[0-9]{8,15}\$"))

            if (!isEmail && !isPhone) {
                showError("Hanya email atau nomor HP yang valid diperbolehkan.")
                return@setOnClickListener
            }

            val registered = prefs.contains("user_$username")

            if (!registered) {
                // NEW USER → GO TO SIGNUP WITH PREFILL
                val args = Bundle().apply {
                    putString("prefill_username", username)
                    putBoolean("is_email", isEmail)
                }

                findNavController().navigate(
                    R.id.action_loginFragment_to_signupFragment,
                    args
                )

            } else {
                // REGISTERED → GO TO PRODUCT LIST
                Toast.makeText(requireContext(), "Login berhasil!", Toast.LENGTH_SHORT).show()

                findNavController().navigate(R.id.action_loginFragment_to_productListFragment)
            }
        }

        // "Daftar" text click - go to signup
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        // Close app
        binding.btnClose.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun showError(message: String) {
        binding.tvError.apply {
            text = message
            visibility = View.VISIBLE
        }
        binding.etUsername.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.edittext_error_bg
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
