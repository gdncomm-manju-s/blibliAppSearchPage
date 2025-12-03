package com.example.bliblihomepage.ui.homePage

import android.os.Bundle
import android.util.Log
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
import com.example.bliblihomepage.util.SharedPrefManager

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val TAG = "LoginFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")

        // Clear error text while typing
        binding.etUsername.addTextChangedListener {
            binding.tvError.visibility = View.GONE
            binding.etUsername.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.edittext_bg)
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isBlank()) {
                showError("Nomor HP atau email harus diisi.")
                return@setOnClickListener
            }
            if (password.isBlank()) {
                showError("Kata sandi harus diisi.")
                return@setOnClickListener
            }

            val isEmail = Patterns.EMAIL_ADDRESS.matcher(username).matches()
            val isPhone = username.matches(Regex("^\\+?[0-9]{8,15}\$"))

            if (!isEmail && !isPhone) {
                showError("Hanya email atau nomor HP yang valid diperbolehkan.")
                return@setOnClickListener
            }

            // 1) If user not registered, go to signup
            if (!SharedPrefManager.isRegistered(requireContext(), username)) {
                Log.d(TAG, "User not registered -> navigate signup")
                val args = Bundle().apply {
                    putString("prefill_username", username)
                    putBoolean("is_email", isEmail)
                }
                // safe navigation: ensure fragment is added
                if (isAdded) {
                    findNavController().navigate(R.id.action_loginFragment_to_signupFragment, args)
                }
                return@setOnClickListener
            }

            // 2) User is registered -> try login
            val loginOk = SharedPrefManager.login(requireContext(), username, password)
            if (loginOk) {
                Toast.makeText(requireContext(), "Login berhasil!", Toast.LENGTH_SHORT).show()
                if (isAdded) findNavController().navigate(R.id.cartFragment)
            } else {
                // Registered but wrong password
                showError("Kata sandi salah. Silakan coba lagi.")
            }
        }
    }

    private fun showError(message: String) {
        binding.tvError.apply {
            text = message
            visibility = View.VISIBLE
        }
        binding.etUsername.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.edittext_error_bg)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
