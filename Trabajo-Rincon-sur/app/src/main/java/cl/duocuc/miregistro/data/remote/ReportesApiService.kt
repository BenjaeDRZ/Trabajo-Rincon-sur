package cl.duocuc.miregistro.data.remote

import cl.duocuc.miregistro.model.Meta
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportesApiService {
    @POST("api/reportes/metas")
    suspend fun crearMeta(@Body meta: Meta): Meta
}

object ReportesRetrofitInstance {

    private const val BASE_URL = "http://10.0.2.2:8084/"

    val api: ReportesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ReportesApiService::class.java)
    }
}