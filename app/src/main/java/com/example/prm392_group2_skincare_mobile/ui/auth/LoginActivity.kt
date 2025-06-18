package com.example.prm392_group2_skincare_mobile.ui.auth

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.prm392_group2_skincare_mobile.R
import com.example.prm392_group2_skincare_mobile.data.model.request.LoginRequest
import com.example.prm392_group2_skincare_mobile.data.remote.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class LoginActivity : AppCompatActivity() {

    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var forgotPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Login"

        emailLayout = findViewById(R.id.email_layout)
        passwordLayout = findViewById(R.id.password_layout)
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        loginButton = findViewById(R.id.login_button)
        forgotPassword = findViewById(R.id.forgot_password)

        loginButton.setOnClickListener {
            validateAndLogin()
        }
    }

    private fun validateAndLogin() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        // Reset errors
        emailLayout.error = null
        passwordLayout.error = null

        // Validation
        var isValid = true

        if (email.isEmpty()) {
            emailLayout.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.error = "Invalid email format"
            isValid = false
        }

        if (password.isEmpty()) {
            passwordLayout.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordLayout.error = "Password must be at least 6 characters"
            isValid = false
        }

        if (isValid) {
            if (isValid) {
                loginButton.isEnabled = false // Disable button to prevent multiple clicks
                performLogin(email, password)
            }
        }
    }

    private fun performLogin(email: String, password: String) {
        CoroutineScope(Dispatchers.Main).launch{
            try {
                val request = LoginRequest(email, password)
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.login(request).awaitResponse()
                }

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.token != null) {
                        Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                        // TODO: Save token and navigate to next screen
                        // Example: startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        // finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                loginButton.isEnabled = true // Re-enable button
            }
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