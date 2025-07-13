// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/ui/payment/PaymentActivity.kt
package com.example.prm392_group2_skincare_mobile.ui.payment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prm392_group2_skincare_mobile.databinding.ActivityPaymentBinding

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Payment"

        webView = binding.webView
        setupWebView()

        val paymentUrl = intent.getStringExtra("PAYMENT_URL")
        if (paymentUrl != null) {
            // Log the URL that is being loaded initially.
            Log.d("PaymentActivity", "Loading Payment URL: $paymentUrl")
            webView.loadUrl(paymentUrl)
        } else {
            Toast.makeText(this, "Payment URL not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            // Allow mixed content for VNPay
            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                Log.d("PaymentActivity", "Loading URL: $url")

                // Intercept the custom return URL from VNPay.
                if (url.startsWith("https://defleur.app/payment/return")) {
                    handlePaymentReturn(url)
                    return true // Prevent the WebView from trying to load this custom URL.
                }

                return false // Allow the WebView to handle all other URLs.
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                Log.e("PaymentActivity", "WebView error: ${error?.description} for URL: ${request?.url}")
                if (request?.isForMainFrame == true) {
                    Toast.makeText(this@PaymentActivity, "Payment page error: ${error?.description}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                // Inject JavaScript to fix a 'timer is not defined' error on the VNPay sandbox page.
                val jsCode = """
                    javascript:(function() {
                        // Define 'timer' on the window object to prevent a ReferenceError from VNPay's script.
                        if (typeof timer === 'undefined') {
                            window.timer = null;
                        }
                        
                        // The VNPay page also has a jQuery function that can fail.
                        // This prevents it from crashing the WebView.
                        if (typeof jQuery !== 'undefined') {
                            jQuery(document).ready(function($) {
                                if (typeof updateTime === 'function') {
                                    try {
                                        updateTime();
                                    } catch(e) {
                                        console.log('Timer function error handled:', e);
                                    }
                                }
                            });
                        }
                    })();
                """

                view?.evaluateJavascript(jsCode, null)
            }
        }
    }

    private fun handlePaymentReturn(url: String) {
        try {
            val uri = Uri.parse(url)
            val responseCode = uri.getQueryParameter("vnp_ResponseCode")
            val transactionStatus = uri.getQueryParameter("vnp_TransactionStatus")
            val orderId = uri.getQueryParameter("vnp_TxnRef")
            val amount = uri.getQueryParameter("vnp_Amount")

            Log.d("PaymentActivity", "Payment result - Code: $responseCode, Status: $transactionStatus, OrderId: $orderId")

            when (responseCode) {
                "00" -> {
                    // Payment successful
                    Toast.makeText(this, "Payment successful!", Toast.LENGTH_LONG).show()

                    // Send result back to previous activity
                    val resultIntent = Intent().apply {
                        putExtra("PAYMENT_SUCCESS", true)
                        putExtra("ORDER_ID", orderId)
                        putExtra("TRANSACTION_STATUS", transactionStatus)
                        putExtra("AMOUNT", amount)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
                "24" -> {
                    // Transaction cancelled
                    Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_CANCELED)
                    finish()
                }
                else -> {
                    // Payment failed
                    Toast.makeText(this, "Payment failed. Code: $responseCode", Toast.LENGTH_LONG).show()
                    setResult(RESULT_CANCELED)
                    finish()
                }
            }
        } catch (e: Exception) {
            Log.e("PaymentActivity", "Error handling payment return", e)
            Toast.makeText(this, "Error processing payment result", Toast.LENGTH_SHORT).show()
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}