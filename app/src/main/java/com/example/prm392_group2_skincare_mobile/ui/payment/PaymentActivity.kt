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
            // Allow mixed content for VNPay sandbox environment
            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                Log.d("PaymentActivity", "URL Loading: $url")

                // Intercept the custom success URL from the backend.
                if (url.startsWith("defleur-app://payment/success")) {
                    handlePaymentSuccess(url)
                    return true // URL handled, so stop the WebView from loading it.
                }

                // Intercept the custom failure URL from the backend.
                if (url.startsWith("defleur-app://payment/failure")) {
                    handlePaymentFailure(url)
                    return true // URL handled.
                }

                // Allow the WebView to load all other URLs, including the initial payment page
                // and the redirect to our backend's callback.
                return false
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
                        if (typeof timer === 'undefined') {
                            window.timer = null;
                        }
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

    private fun handlePaymentSuccess(url: String) {
        val uri = Uri.parse(url)
        val orderId = uri.getQueryParameter("orderId")
        Toast.makeText(this, "Payment for order $orderId successful!", Toast.LENGTH_LONG).show()
        val resultIntent = Intent().apply {
            putExtra("PAYMENT_SUCCESS", true)
            putExtra("ORDER_ID", orderId)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun handlePaymentFailure(url: String) {
        val uri = Uri.parse(url)
        val message = uri.getQueryParameter("message") ?: "Payment failed"
        Toast.makeText(this, "Payment failed: $message", Toast.LENGTH_LONG).show()
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            setResult(RESULT_CANCELED)
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        setResult(RESULT_CANCELED)
        finish()
        return true
    }
}