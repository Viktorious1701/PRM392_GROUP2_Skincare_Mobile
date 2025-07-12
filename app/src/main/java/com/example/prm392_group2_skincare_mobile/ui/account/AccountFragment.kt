package com.example.prm392_group2_skincare_mobile.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.prm392_group2_skincare_mobile.data.local.preferences.UserPreferences
import com.example.prm392_group2_skincare_mobile.databinding.FragmentAccountBinding
import com.example.prm392_group2_skincare_mobile.ui.auth.LoginActivity
import com.example.prm392_group2_skincare_mobile.ui.auth.SignUpActivity
import com.example.prm392_group2_skincare_mobile.ui.chat.ChatActivity
import com.example.prm392_group2_skincare_mobile.ui.chatAI.ChatAIActivity
import com.example.prm392_group2_skincare_mobile.ui.map.MapActivity

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupClickListeners()

        return root
    }

    override fun onResume() {
        super.onResume()
        // Update the UI every time the fragment is displayed
        updateUiState()
    }

    private fun setupClickListeners() {
        binding.buttonLogin.setOnClickListener {
            startActivity(Intent(activity, LoginActivity::class.java))
        }

        binding.buttonSignup.setOnClickListener {
            startActivity(Intent(activity, SignUpActivity::class.java))
        }

        binding.buttonLogout.setOnClickListener {
            // Clear stored user data and update UI
            UserPreferences.clear()
            updateUiState()
        }

        binding.buttonChat.setOnClickListener {
            startActivity(Intent(activity, ChatActivity::class.java))
        }

        binding.buttonMap.setOnClickListener {
            startActivity(Intent(activity, MapActivity::class.java))
        }

        binding.buttonChatAI.setOnClickListener {
            startActivity(Intent(activity, ChatAIActivity::class.java))
        }
    }

    private fun updateUiState() {
        if (UserPreferences.isLoggedIn()) {
            // User is logged in: show user info and logout button
            val userData = UserPreferences.getUserData()
            binding.textAccountTitle.text = "My Account"
            binding.loggedInGroup.visibility = View.VISIBLE
            binding.loggedOutGroup.visibility = View.GONE
            binding.textUserInfo.text = "Username: ${userData?.userName}\nEmail: ${userData?.email}"
        } else {
            // User is not logged in: show login and sign-up buttons
            binding.textAccountTitle.text = "Account & Support"
            binding.loggedInGroup.visibility = View.GONE
            binding.loggedOutGroup.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}