package com.example.prm392_group2_skincare_mobile.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.prm392_group2_skincare_mobile.databinding.FragmentAccountBinding
import com.example.prm392_group2_skincare_mobile.ui.auth.LoginActivity
import com.example.prm392_group2_skincare_mobile.ui.auth.SignUpActivity
import com.example.prm392_group2_skincare_mobile.ui.chat.ChatActivity
import com.example.prm392_group2_skincare_mobile.ui.map.MapActivity
import com.example.prm392_group2_skincare_mobile.ui.chatAI.ChatAIActivity

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

        binding.buttonLogin.setOnClickListener {
            startActivity(Intent(activity, LoginActivity::class.java))
        }

        binding.buttonSignup.setOnClickListener {
            startActivity(Intent(activity, SignUpActivity::class.java))
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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}