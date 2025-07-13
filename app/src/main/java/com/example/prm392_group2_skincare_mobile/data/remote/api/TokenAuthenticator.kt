// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/data/remote/TokenAuthenticator.kt
package com.example.prm392_group2_skincare_mobile.data.remote

import com.example.prm392_group2_skincare_mobile.data.local.preferences.UserPreferences
import com.example.prm392_group2_skincare_mobile.data.model.request.RefreshTokenRequest
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

// This Authenticator intercepts 401 Unauthorized responses to automatically refresh the access token.
class TokenAuthenticator : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Get the refresh token from preferences. If it's null, we can't refresh.
        val refreshToken = UserPreferences.getRefreshToken() ?: return null

        // Use a synchronized block to prevent multiple threads from refreshing the token simultaneously.
        synchronized(this) {
            // Check if the token was already refreshed by another thread.
            val newAccessToken = UserPreferences.getAccessToken()
            if (response.request.header("Authorization") != "Bearer $newAccessToken") {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
            }

            // Execute the synchronous refresh token call.
            val call = RetrofitClient.apiService.refreshToken(RefreshTokenRequest(refreshToken))
            val refreshResponse = call.execute()

            if (refreshResponse.isSuccessful) {
                val loginResponse = refreshResponse.body()?.data
                if (loginResponse != null) {
                    // Save the new tokens.
                    UserPreferences.saveTokens(loginResponse)
                    // Build a new request with the new access token and return it.
                    return response.request.newBuilder()
                        .header("Authorization", "Bearer ${loginResponse.accessToken}")
                        .build()
                }
            }
        }
        // If refresh fails (e.g., refresh token expired), clear preferences and log the user out.
        UserPreferences.clear()
        return null // Returning null signals that authentication failed.
    }
}