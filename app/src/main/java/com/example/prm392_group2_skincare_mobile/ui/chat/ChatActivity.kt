// app/src/main/java/com/example/prm392_group2_skincare_mobile/ui/chat/ChatActivity.kt
package com.example.prm392_group2_skincare_mobile.ui.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.prm392_group2_skincare_mobile.R

class ChatActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: Toolbar

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Initialize views
        webView = findViewById(R.id.chat_webview)
        progressBar = findViewById(R.id.progress_bar)
        toolbar = findViewById(R.id.toolbar)

        // Set up the toolbar and back navigation
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            handleBackNavigation()
        }

        // Setup WebView with all necessary configurations
        setupWebView()

        // Load the Tawk.to chat URL
        val tawkToUrl = "https://tawk.to/chat/68517dd5e95763190f2370e3/1itv574cc"
        webView.loadUrl(tawkToUrl)

        // Handle the device's back button presses
        setupBackButtonHandler()
    }

    private fun setupWebView() {
        // ** THIS IS THE CRITICAL FIX **
        // Enable third-party cookies, which is required for Tawk.to to manage sessions.
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

        webView.settings.apply {
            // Enable JavaScript, which is essential for modern web pages.
            javaScriptEnabled = true
            // Enable DOM Storage API, required by Tawk.to for session management.
            domStorageEnabled = true
            // Allow the WebView to handle file access, which can be useful for attachments.
            allowFileAccess = true
            // Enable zooming for accessibility.
            setSupportZoom(true)
            builtInZoomControls = false // Let the user use pinch-to-zoom without showing controls.
            displayZoomControls = false

            // Set a standard mobile User-Agent to prevent the site from serving an incompatible version.
            userAgentString = "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.0.0 Mobile Safari/537.36"
        }

        // Handles page loading events and errors.
        webView.webViewClient = object : WebViewClient() {
            // This ensures that links clicked inside the WebView open in the user's default browser,
            // which is better for external links.
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString()
                if (url != null && !url.startsWith("https://tawk.to")) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                    return true // The WebView does not load the URL.
                }
                return false // The WebView will load the URL.
            }

            // Handle SSL errors. For development, we can proceed.
            // In a production app, you should log this error and consider not proceeding.
            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.proceed()
            }
        }

        // Handles UI-related events like progress changes and permissions.
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar.progress = newProgress
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.visibility = View.VISIBLE
                }
            }

            // Grant permissions for features like file uploads.
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }
        }
    }

    private fun setupBackButtonHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackNavigation()
            }
        })
    }

    private fun handleBackNavigation() {
        // If the WebView can go back in its history, do that.
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            // Otherwise, finish the activity.
            finish()
        }
    }

    // Ensure WebView state is saved and restored properly on configuration changes (like screen rotation).
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webView.restoreState(savedInstanceState)
    }
}