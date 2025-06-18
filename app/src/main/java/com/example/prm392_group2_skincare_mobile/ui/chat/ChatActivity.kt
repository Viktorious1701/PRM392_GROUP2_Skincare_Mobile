// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/ui/chat/ChatActivity.kt
package com.example.prm392_group2_skincare_mobile.ui.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
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

    private val TAG = "ChatActivity"

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

        //
        // IMPORTANT: The original URL was invalid and has been removed.
        // Please get the correct and active "Direct Chat Link" from your Tawk.to dashboard
        // and replace the URL below.
        //
        val tawkToUrl = "https://tawk.to/chat/68517dd5e95763190f2370e3/1itv574cc"
        Log.d(TAG, "Loading Tawk.to URL: $tawkToUrl")
        webView.loadUrl(tawkToUrl)

        // Handle the device's back button presses
        setupBackButtonHandler()
    }

    private fun setupWebView() {
        // Enable third-party cookies, which is required for Tawk.to to manage sessions.
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

        webView.settings.apply {
            // Enable JavaScript, which is essential for modern web pages.
            javaScriptEnabled = true
            // Enable DOM Storage API, required by Tawk.to for session management.
            domStorageEnabled = true
            // Enable database storage API.
            databaseEnabled = true
            // Allow the WebView to handle file access.
            allowFileAccess = true
            // Enable zooming for accessibility.
            setSupportZoom(true)
            builtInZoomControls = false // Let the user use pinch-to-zoom without showing controls.
            displayZoomControls = false
            // Allow content from both HTTP and HTTPS sources on newer Android versions.
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            // Set a standard mobile User-Agent to prevent the site from serving an incompatible version.
            userAgentString = "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.0.0 Mobile Safari/537.36"
        }

        // Handles page loading events and errors.
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d(TAG, "Page loading started: $url")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(TAG, "Page loading finished: $url")
            }

            // This ensures that links clicked inside the WebView open in the user's default browser.
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString()
                if (url != null && !url.startsWith("https://tawk.to")) {
                    Log.d(TAG, "Opening external link in browser: $url")
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    } catch (e: Exception) {
                        Log.e(TAG, "Could not open external link", e)
                        Toast.makeText(this@ChatActivity, "Could not open link", Toast.LENGTH_SHORT).show()
                    }
                    return true // The WebView does not load the URL.
                }
                return false // The WebView will load the URL.
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                if (request.isForMainFrame) {
                    val errorMessage = "Error: ${error.errorCode}, ${error.description}"
                    Log.e(TAG, "onReceivedError: $errorMessage for URL ${request.url}")
                    Toast.makeText(this@ChatActivity, "Failed to load chat: ${error.description}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
                super.onReceivedHttpError(view, request, errorResponse)
                if (request?.isForMainFrame == true) {
                    val statusCode = errorResponse?.statusCode ?: 0
                    Log.e(TAG, "HTTP Error: $statusCode for URL ${request.url}")
                    if (statusCode == 404) {
                        Toast.makeText(this@ChatActivity, "Chat page not found. Please check the URL.", Toast.LENGTH_LONG).show()
                    }
                }
            }

            @SuppressLint("WebViewClientOnReceivedSslError")
            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                Log.w(TAG, "SSL Error: ${error?.primaryError}. Proceeding anyway.")
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

            override fun onPermissionRequest(request: PermissionRequest?) {
                Log.d(TAG, "Permission request for: ${request?.resources?.joinToString()}")
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