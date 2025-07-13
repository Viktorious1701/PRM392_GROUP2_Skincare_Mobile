// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/data/remote/RetrofitClient.kt
package com.example.prm392_group2_skincare_mobile.data.remote

import com.example.prm392_group2_skincare_mobile.data.local.preferences.UserPreferences
import com.example.prm392_group2_skincare_mobile.data.remote.api.AuthApiService
import com.example.prm392_group2_skincare_mobile.data.remote.api.ChatAIApiService
import com.example.prm392_group2_skincare_mobile.data.remote.api.CosmeticApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RetrofitClient {
    // For local development with Android Emulator, use 10.0.2.2 to connect to your PC's localhost.
    private const val BASE_URL = "https://10.0.2.2:5051/"

    // Custom Date Deserializer
    class DateDeserializer : JsonDeserializer<Date> {
        companion object {
            private val dateFormats = arrayOf(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault()),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault()),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            )
        }

        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): Date? {
            val dateString = json?.asString
            if (dateString.isNullOrEmpty()) return null

            for (format in dateFormats) {
                try {
                    return format.parse(dateString)
                } catch (e: Exception) {
                    // Continue to next format
                }
            }

            throw JsonParseException("Unable to parse date: $dateString")
        }
    }

    // Custom Gson configuration
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Date::class.java, DateDeserializer())
        .setLenient()
        .create()

    // Interceptor to add the Authorization token to requests
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val accessToken = UserPreferences.getAccessToken()

        // Do not add Authorization header to login/register requests
        if (originalRequest.url.encodedPath.contains("/api/auth/")) {
            return@Interceptor chain.proceed(originalRequest)
        }

        // If no token is available, proceed with the original request
        if (accessToken == null) {
            return@Interceptor chain.proceed(originalRequest)
        }

        // Add the Authorization header to the request
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

        chain.proceed(newRequest)
    }


    // This custom OkHttpClient is built to trust all certificates.
    // This is necessary for connecting to a local server with a self-signed dev certificate.
    private val okHttpClient: OkHttpClient by lazy {
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                }
            )

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory

            // Create a logging interceptor for debugging network requests
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // Build the OkHttpClient with the custom SSL socket factory, the logger, and the auth interceptor
            OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true } // Trust all hostnames
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor) // Add our auth interceptor
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Use the custom OkHttpClient
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // Generic function to create any service
    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }

    // Existing services for backward compatibility (consider refactoring to use the generic create method)
    val chatAIApiService: ChatAIApiService by lazy {
        create(ChatAIApiService::class.java)
    }

    val apiService: AuthApiService by lazy {
        create(AuthApiService::class.java)
    }

    val cosmeticApiService: CosmeticApiService by lazy {
        create(CosmeticApiService::class.java)
    }
}