// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/MainActivity.kt
package com.example.prm392_group2_skincare_mobile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.prm392_group2_skincare_mobile.data.local.preferences.UserPreferences
import com.example.prm392_group2_skincare_mobile.data.remote.RetrofitClient
import com.example.prm392_group2_skincare_mobile.data.remote.api.CartApiService
import com.example.prm392_group2_skincare_mobile.data.repository.CartRepository
import com.example.prm392_group2_skincare_mobile.databinding.ActivityMainBinding
import com.example.prm392_group2_skincare_mobile.ui.cart.CartActivity
import com.example.prm392_group2_skincare_mobile.ui.cart.CartViewModel
import com.example.prm392_group2_skincare_mobile.ui.common.NotificationHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // ViewModel for handling cart logic.
    private val cartViewModel: CartViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val cartService = RetrofitClient.create(CartApiService::class.java)
                val repository = CartRepository(cartService)
                @Suppress("UNCHECKED_CAST")
                return CartViewModel(repository) as T
            }
        }
    }

    // Activity result launcher for handling the notification permission request.
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Show the notification.
                NotificationHelper.showCartNotification(this)
            } else {
                // The user denied the permission. Show a toast message.
                Toast.makeText(this, "Notification permission denied.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_shop, R.id.navigation_account)
        )
        navView.setupWithNavController(navController)

        // Create the notification channel as soon as the app starts.
        NotificationHelper.createNotificationChannel(this)

        // Check if the user is logged in and then check their cart for items.
        checkForCartAndNotify()
    }

    // Checks if the user's cart has items and triggers notifications if it does.
    private fun checkForCartAndNotify() {
        if (!UserPreferences.isLoggedIn()) {
            return // Don't check cart if user is not logged in.
        }

        // Observe the cart data from the ViewModel.
        cartViewModel.cart.observe(this) { result ->
            result.onSuccess { cart ->
                // If the cart has items, show both system and in-app notifications.
                if (cart.items.isNotEmpty()) {
                    showCartSnackbar()
                    askForNotificationPermission()
                }
            }
            // We can optionally handle the failure case, e.g., by logging.
        }
        // Trigger the cart fetch.
        cartViewModel.fetchCurrentUserCart()
    }

    // Displays an in-app Snackbar notification to alert the user about items in their cart.
    private fun showCartSnackbar() {
        Snackbar.make(binding.root, "You have items in your cart!", Snackbar.LENGTH_LONG)
            .setAction("VIEW CART") {
                // When the "VIEW CART" button is clicked, open the CartActivity.
                val intent = Intent(this, CartActivity::class.java)
                startActivity(intent)
            }
            .show()
    }

    // Asks for the POST_NOTIFICATIONS permission on Android 13+ if not already granted.
    private fun askForNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission is already granted, show the notification directly.
                    NotificationHelper.showCartNotification(this)
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Show an educational UI explaining why the permission is needed (optional).
                    // For this example, we directly request the permission.
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Directly ask for the permission.
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // On older Android versions, permission is not required. Show notification.
            NotificationHelper.showCartNotification(this)
        }
    }
}