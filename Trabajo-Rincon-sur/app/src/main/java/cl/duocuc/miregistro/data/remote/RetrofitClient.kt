package cl.duocuc.miregistro.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val http: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(ApiConfig.TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)
            .readTimeout(ApiConfig.TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)
            .writeTimeout(ApiConfig.TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)
            .build()
    }

    private fun <T> crear(baseUrl: String, servicio: Class<T>): T =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(http)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(servicio)

    val auth: AuthApi by lazy { crear(ApiConfig.URL_AUTH, AuthApi::class.java) }
    val operacion: OperacionApi by lazy { crear(ApiConfig.URL_OPERACION, OperacionApi::class.java) }
    val analitica: AnaliticaApi by lazy { crear(ApiConfig.URL_ANALITICA, AnaliticaApi::class.java) }
}
