package com.example.prm392_group2_skincare_mobile.ui.chatAI



import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prm392_group2_skincare_mobile.data.repository.ChatAIRepository
import com.example.prm392_group2_skincare_mobile.data.model.ChatAIMessage
import kotlinx.coroutines.launch

class ChatAIViewModel(private val repository: ChatAIRepository) : ViewModel() {

    private val _messages = MutableLiveData<List<ChatAIMessage>>(emptyList())
    val messages: LiveData<List<ChatAIMessage>> = _messages

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        // Add a welcome message from the bot
        addMessage(ChatAIMessage("Hello! I'm your skincare assistant. How can I help you today?", false))
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        // Add user message immediately
        addMessage(ChatAIMessage(text, true))

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.sendMessage(text)
                _isLoading.postValue(false)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.isSuccess && apiResponse.data != null) {
                        addMessage(ChatAIMessage(apiResponse.data.reply, false))
                    } else {
                        val error = apiResponse.errors?.firstOrNull()?.description ?: "An unknown error occurred."
                        _errorMessage.postValue(error)
                    }
                } else {
                    _errorMessage.postValue("Failed to get response from server. Code: ${response.code()}")
                }
            } catch (e: Exception) {
                _isLoading.postValue(false)
                _errorMessage.postValue("An error occurred: ${e.message}")
            }
        }
    }

    private fun addMessage(message: ChatAIMessage) {
        val currentList = _messages.value?.toMutableList() ?: mutableListOf()
        currentList.add(message)
        _messages.value = currentList
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}