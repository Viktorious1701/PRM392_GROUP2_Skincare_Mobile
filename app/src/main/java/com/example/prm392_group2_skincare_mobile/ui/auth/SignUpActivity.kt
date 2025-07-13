package com.example.prm392_group2_skincare_mobile.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.prm392_group2_skincare_mobile.data.model.request.RegisterRequest
import com.example.prm392_group2_skincare_mobile.data.remote.RetrofitClient
import com.example.prm392_group2_skincare_mobile.data.remote.api.AuthApiService
import com.example.prm392_group2_skincare_mobile.data.repository.AuthRepository
import com.example.prm392_group2_skincare_mobile.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
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
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Sign Up"

        setupObservers()

        binding.registerButton.setOnClickListener {
            validateAndRegister()
        }
    }

    private fun setupObservers() {
        authViewModel.isLoading.observe(this) { isLoading ->
            binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.registerButton.isEnabled = !isLoading
        }

        authViewModel.registerResult.observe(this) { result ->
            result.onSuccess { message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                // Navigate to login screen on success
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }.onFailure { exception ->
                Toast.makeText(this, "Registration failed: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateAndRegister() {
        val firstName = binding.firstNameInput.text.toString().trim()
        val lastName = binding.lastNameInput.text.toString().trim()
        val userName = binding.usernameInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val phone = binding.phoneInput.text.toString().trim()
        val password = binding.passwordInput.text.toString()
        val confirmPassword = binding.confirmPasswordInput.text.toString()
        val selectedGenderId = binding.genderRadioGroup.checkedRadioButtonId

        var isValid = true

        if (firstName.isEmpty()) {
            binding.firstNameLayout.error = "First name is required"
            isValid = false
        }
        if (lastName.isEmpty()) {
            binding.lastNameLayout.error = "Last name is required"
            isValid = false
        }
        if (userName.isEmpty()) {
            binding.usernameLayout.error = "Username is required"
            isValid = false
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.error = "Enter a valid email"
            isValid = false
        }
        if (phone.isEmpty()) {
            binding.phoneLayout.error = "Phone number is required"
            isValid = false
        }
        if (selectedGenderId == -1) {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        if (password.length < 5) {
            binding.passwordLayout.error = "Password must be at least 5 characters"
            isValid = false
        }
        if (password != confirmPassword) {
            binding.confirmPasswordLayout.error = "Passwords do not match"
            isValid = false
        }

        if (isValid) {
            val gender = (selectedGenderId == binding.radioMale.id) // Assuming true for male, false for female
            val request = RegisterRequest(
                userName = userName,
                email = email,
                gender = gender,
                phoneNumber = phone,
                firstName = firstName,
                lastName = lastName,
                password = password,
                passwordConfirmation = confirmPassword
            )
            authViewModel.register(request)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}