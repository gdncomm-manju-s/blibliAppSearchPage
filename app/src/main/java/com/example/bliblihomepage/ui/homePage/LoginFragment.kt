package com.example.bliblihomepage.ui.homePage

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
import com.example.bliblihomepage.util.SharedPrefManager

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Clear error text while typing
        binding.etUsername.addTextChangedListener {
            binding.tvError.visibility = View.GONE
            binding.etUsername.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.edittext_bg)
        }

        /** -------------------------
         *  LOGIN BUTTON CLICK
         * ------------------------- */
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()

            if (username.isBlank()) {
                showError("Nomor HP atau email harus diisi.")
                return@setOnClickListener
            }

            // Validate formats
            val isEmail = Patterns.EMAIL_ADDRESS.matcher(username).matches()
            val isPhone = username.matches(Regex("^\\+?[0-9]{8,15}\$"))

            if (!isEmail && !isPhone) {
                showError("Hanya email atau nomor HP yang valid diperbolehkan.")
                return@setOnClickListener
            }

            // LOGIN → returns TRUE if already registered
            val registered = SharedPrefManager.login(requireContext(), username)

            if (!registered) {
                // New user → Go to Signup
                val args = Bundle().apply {
                    putString("prefill_username", username)
                    putBoolean("is_email", isEmail)
                }
                findNavController().navigate(
                    R.id.action_loginFragment_to_signupFragment,
                    args
                )
            } else {
                // Existing user → Go to Cart
                Toast.makeText(requireContext(), "Login berhasil!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.cartFragment)
            }
        }

        /** -------------------------
         *  REGISTER CLICK
         * ------------------------- */
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        /** -------------------------
         *  CLOSE APP
         * ------------------------- */
        binding.btnClose.setOnClickListener {
            requireActivity().finish()
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
