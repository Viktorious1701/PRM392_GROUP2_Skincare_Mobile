package com.example.prm392_group2_skincare_mobile.ui.chatAI

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prm392_group2_skincare_mobile.data.remote.RetrofitClient
import com.example.prm392_group2_skincare_mobile.data.repository.ChatAIRepository
import com.example.prm392_group2_skincare_mobile.databinding.ActivityChatAiBinding
import com.example.prm392_group2_skincare_mobile.data.model.ChatAIMessage

class ChatAIActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatAiBinding // Correct binding class name
    private lateinit var chatAdapter: ChatAIAdapter

    private val viewModel: ChatAIViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = ChatAIRepository(RetrofitClient.chatAIApiService)
                @Suppress("UNCHECKED_CAST")
                return ChatAIViewModel(repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatAiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // Setup RecyclerView
        setupRecyclerView()

        // Observe ViewModel LiveData
        observeViewModel()

        // Set up click listener for the send button
        binding.sendButton.setOnClickListener {
            val messageText = binding.messageInput.text.toString()
            if (messageText.isNotBlank()) {
                viewModel.sendMessage(messageText)
                binding.messageInput.text.clear()
            }
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAIAdapter(mutableListOf())
        binding.chatRecyclerView.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@ChatAIActivity).apply { // Correct context reference
                stackFromEnd = true
            }
        }
    }

    private fun observeViewModel() {
        viewModel.messages.observe(this) { messages ->
            messages?.let {
                updateChatUI(it)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage() // Clear after showing
            }
        }
    }

    private fun updateChatUI(messages: List<ChatAIMessage>) {
        chatAdapter.messages.clear()
        chatAdapter.messages.addAll(messages)
        chatAdapter.notifyDataSetChanged()

        // Scroll to the latest message
        if (messages.isNotEmpty()) {
            binding.chatRecyclerView.scrollToPosition(messages.size - 1)
        }
    }
}