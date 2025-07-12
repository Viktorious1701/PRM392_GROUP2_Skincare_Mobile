package com.example.prm392_group2_skincare_mobile.ui.auth

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.prm392_group2_skincare_mobile.data.local.preferences.UserPreferences
import com.example.prm392_group2_skincare_mobile.data.model.request.LoginRequest
import com.example.prm392_group2_skincare_mobile.data.remote.RetrofitClient
import com.example.prm392_group2_skincare_mobile.data.remote.api.AuthApiService
import com.example.prm392_group2_skincare_mobile.data.repository.AuthRepository
import com.example.prm392_group2_skincare_mobile.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val authService = RetrofitClient.create(AuthApiService::class.java)
                val authRepository = AuthRepository(authService)
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(authRepository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Login"

        setupObservers()

        binding.loginButton.setOnClickListener {
            validateAndLogin()
        }
    }

    private fun setupObservers() {
        authViewModel.isLoading.observe(this) { isLoading ->
            binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.loginButton.isEnabled = !isLoading
        }

        authViewModel.loginResult.observe(this) { result ->
            result.onSuccess { loginResponse ->
                Toast.makeText(this, "Login successful! Welcome ${loginResponse.userName}", Toast.LENGTH_LONG).show()
                // Save tokens and user data
                UserPreferences.saveTokens(loginResponse)
                // Close the activity and return to the previous screen
                finish()
            }.onFailure { exception ->
                Toast.makeText(this, "Login failed: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateAndLogin() {
        val username = binding.usernameInput.text.toString().trim()
        val password = binding.passwordInput.text.toString().trim()

        binding.usernameLayout.error = null
        binding.passwordLayout.error = null

        var isValid = true
        if (username.isEmpty()) {
            binding.usernameLayout.error = "Username is required"
            isValid = false
        }
        if (password.isEmpty()) {
            binding.passwordLayout.error = "Password is required"
            isValid = false
        }

        if (isValid) {
            val loginRequest = LoginRequest(userName = username, password = password)
            authViewModel.login(loginRequest)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}