package com.example.prm392_group2_skincare_mobile.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.prm392_group2_skincare_mobile.MyApplication
import com.example.prm392_group2_skincare_mobile.data.model.response.LoginResponse
import com.google.gson.Gson

object UserPreferences {

    private const val PREFS_NAME = "user_prefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_USER_DATA = "user_data"

    private val gson = Gson()

    private fun getPreferences(): SharedPreferences {
        return MyApplication.appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveTokens(loginResponse: LoginResponse) {
        val editor = getPreferences().edit()
        editor.putString(KEY_ACCESS_TOKEN, loginResponse.accessToken)
        editor.putString(KEY_REFRESH_TOKEN, loginResponse.refreshToken)

        // Save user data as a JSON string for easy retrieval
        val userDataJson = gson.toJson(loginResponse)
        editor.putString(KEY_USER_DATA, userDataJson)

        editor.apply()
    }

    fun getAccessToken(): String? {
        return getPreferences().getString(KEY_ACCESS_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return getPreferences().getString(KEY_REFRESH_TOKEN, null)
    }

    fun getUserData(): LoginResponse? {
        val userDataJson = getPreferences().getString(KEY_USER_DATA, null)
        return userDataJson?.let {
            try {
                gson.fromJson(it, LoginResponse::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun isLoggedIn(): Boolean {
        return getAccessToken() != null
    }

    fun clear() {
        val editor = getPreferences().edit()
        editor.clear()
        editor.apply()
    }
}