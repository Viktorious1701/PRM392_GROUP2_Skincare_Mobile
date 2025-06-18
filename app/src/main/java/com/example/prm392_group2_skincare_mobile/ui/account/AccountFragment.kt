// app/src/main/java/com/example/prm392_group2_skincare_mobile/ui/account/AccountFragment.kt
package com.example.prm392_group2_skincare_mobile.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.prm392_group2_skincare_mobile.databinding.FragmentAccountBinding
import com.example.prm392_group2_skincare_mobile.ui.chat.ChatActivity
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

        // Set up click listener for the chat button
        binding.buttonChat.setOnClickListener {
            val intent = Intent(activity, ChatActivity::class.java)
            startActivity(intent)
        }

        // Set up click listener for the map button
        binding.buttonMap.setOnClickListener {
            val intent = Intent(activity, MapActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}