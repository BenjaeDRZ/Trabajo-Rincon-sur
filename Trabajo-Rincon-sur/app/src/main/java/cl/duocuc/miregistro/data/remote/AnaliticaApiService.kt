
package cl.duocuc.miregistro.data.remote

import cl.duocuc.miregistro.model.ConteoApi
import cl.duocuc.miregistro.model.TransaccionApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import okhttp3.ResponseBody

interface AnaliticaApiService {

    @POST("api/analitica/conteos")
    suspend fun cargarConteos(
        @Body conteos: List<ConteoApi>
    ): ResponseBody

    @POST("api/analitica/transacciones")
    suspend fun cargarTransacciones(
        @Body transacciones: List<TransaccionApi>
    ): ResponseBody
}

object AnaliticaRetrofitInstance {

    private const val BASE_URL = "http://10.0.2.2:8083/"

    val api: AnaliticaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AnaliticaApiService::class.java)
    }
}
