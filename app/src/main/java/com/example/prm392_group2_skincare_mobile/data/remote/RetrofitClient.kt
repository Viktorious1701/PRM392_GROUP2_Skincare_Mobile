package com.example.prm392_group2_skincare_mobile.data.remote

import com.example.prm392_group2_skincare_mobile.data.remote.api.AuthApiService
import com.example.prm392_group2_skincare_mobile.data.remote.api.ChatAIApiService
import com.example.prm392_group2_skincare_mobile.data.remote.api.CosmeticApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RetrofitClient {
    // For local development with Android Emulator, use 10.0.2.2 to connect to your PC's localhost.
    // The backend is running in a Docker container that exposes port 8080 to the host's port 8082.
    // Therefore, the emulator must connect to port 8082 on the host.
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

            // Create a logging interceptor
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // Build the OkHttpClient with the SSL socket factory AND the logger
            OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true }
                .addInterceptor(loggingInterceptor) // Add the logger here
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson)) // Use custom Gson
            .build()
    }

    // Generic function to create any service
    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }

    // Existing services for backward compatibility
    val chatAIApiService: ChatAIApiService by lazy {
        retrofit.create(ChatAIApiService::class.java)
    }

    val apiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val cosmeticApiService : CosmeticApiService by lazy {
        retrofit.create(CosmeticApiService::class.java)
    }
}