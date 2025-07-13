// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/ui/billing/BillingActivity.kt
package com.example.prm392_group2_skincare_mobile.ui.billing

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.prm392_group2_skincare_mobile.MainActivity
import com.example.prm392_group2_skincare_mobile.R
import com.example.prm392_group2_skincare_mobile.data.model.request.CreateOnlineOrderRequest
import com.example.prm392_group2_skincare_mobile.data.model.response.District
import com.example.prm392_group2_skincare_mobile.data.model.response.Province
import com.example.prm392_group2_skincare_mobile.data.model.response.Ward
import com.example.prm392_group2_skincare_mobile.data.remote.RetrofitClient
import com.example.prm392_group2_skincare_mobile.data.remote.api.GHNApiService
import com.example.prm392_group2_skincare_mobile.data.remote.api.OrderApiService
import com.example.prm392_group2_skincare_mobile.data.repository.GHNRepository
import com.example.prm392_group2_skincare_mobile.data.repository.OrderRepository
import com.example.prm392_group2_skincare_mobile.databinding.ActivityBillingBinding
import com.example.prm392_group2_skincare_mobile.ui.payment.PaymentActivity

class BillingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBillingBinding

    private val orderViewModel: OrderViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val orderService = RetrofitClient.create(OrderApiService::class.java)
                val repository = OrderRepository(orderService)
                @Suppress("UNCHECKED_CAST")
                return OrderViewModel(repository) as T
            }
        }
    }

    private val ghnViewModel: GHNViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val ghnService = RetrofitClient.create(GHNApiService::class.java)
                val repository = GHNRepository(ghnService)
                @Suppress("UNCHECKED_CAST")
                return GHNViewModel(repository) as T
            }
        }
    }

    // Modern way to handle activity results, replacing onActivityResult.
    private val paymentActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // This callback executes when PaymentActivity finishes.
        if (result.resultCode == RESULT_OK) {
            val paymentSuccess = result.data?.getBooleanExtra("PAYMENT_SUCCESS", false) ?: false
            if (paymentSuccess) {
                Toast.makeText(this, "Payment completed! Your order is being processed.", Toast.LENGTH_LONG).show()
                // Clear the back stack and return to the main activity.
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Payment verification failed.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Handle RESULT_CANCELED from PaymentActivity (e.g., user pressed back).
            Toast.makeText(this, "Payment was cancelled or failed.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBillingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Billing"

        setupObservers()
        setupSpinners()

        binding.buttonConfirmPurchase.setOnClickListener {
            handleConfirmPurchase()
        }

        // Initial fetch for provinces
        ghnViewModel.fetchProvinces()
    }

    private fun setupSpinners() {
        binding.provinceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedProvince = parent?.getItemAtPosition(position) as? Province
                selectedProvince?.let {
                    ghnViewModel.fetchDistricts(it.provinceID)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.districtSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedDistrict = parent?.getItemAtPosition(position) as? District
                selectedDistrict?.let {
                    ghnViewModel.fetchWards(it.districtID)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupObservers() {
        ghnViewModel.provinces.observe(this) { provinces ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, provinces)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.provinceSpinner.adapter = adapter
        }

        ghnViewModel.districts.observe(this) { districts ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, districts)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.districtSpinner.adapter = adapter
        }

        ghnViewModel.wards.observe(this) { wards ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, wards)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.wardSpinner.adapter = adapter
        }

        orderViewModel.isLoading.observe(this) { isLoading ->
            binding.buttonConfirmPurchase.isEnabled = !isLoading
        }

        orderViewModel.orderResult.observe(this) { result ->
            result.onSuccess { orderResponse ->
                val paymentUrl = orderResponse.paymentUrl
                if (!paymentUrl.isNullOrEmpty()) {
                    // Launch PaymentActivity using the modern launcher to get a result back.
                    val intent = Intent(this, PaymentActivity::class.java).apply {
                        putExtra("PAYMENT_URL", paymentUrl)
                    }
                    paymentActivityResultLauncher.launch(intent)
                } else {
                    // Handle non-online payment methods like COD.
                    Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                    finish()
                }
            }.onFailure { exception ->
                Toast.makeText(this, "Error creating order: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleConfirmPurchase() {
        val fullName = binding.fullNameInput.text.toString().trim()
        val streetAddress = binding.addressInput.text.toString().trim()

        val selectedProvince = binding.provinceSpinner.selectedItem as? Province
        val selectedDistrict = binding.districtSpinner.selectedItem as? District
        val selectedWard = binding.wardSpinner.selectedItem as? Ward

        if (fullName.isEmpty() || streetAddress.isEmpty() || selectedProvince == null || selectedDistrict == null || selectedWard == null) {
            Toast.makeText(this, "Please fill all shipping fields and select an address.", Toast.LENGTH_SHORT).show()
            return
        }

        val fullAddress = "$fullName, $streetAddress, ${selectedWard.wardName}, ${selectedDistrict.districtName}, ${selectedProvince.provinceName}"

        val paymentMethod = when (binding.paymentMethodRadioGroup.checkedRadioButtonId) {
            R.id.radio_cod -> "COD"
            else -> "ONLINE"
        }

        val request = CreateOnlineOrderRequest(
            shippingAddress = fullAddress,
            billingAddress = fullAddress,
            paymentMethod = paymentMethod,
            currency = "VND",
            wardCode = selectedWard.wardCode,
            districtId = selectedDistrict.districtID
        )

        orderViewModel.createOrder(request)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}