package cl.duocuc.miregistro.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/** ms-auth (puerto 8081) */
interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequestDto): Response<ColaboradorDto>
}

/** ms-operacion (puerto 8082) */
interface OperacionApi {
    @GET("api/operacion/sesiones")
    suspend fun listarSesiones(): List<SesionDto>

    @POST("api/operacion/sesiones")
    suspend fun guardarSesion(@Body sesion: SesionDto): SesionDto

    @PUT("api/operacion/sesiones/{numero}/estado")
    suspend fun actualizarEstado(@Path("numero") numero: String, @Body cambio: CambioEstadoDto): Response<SesionDto>

    @GET("api/operacion/sesiones/{numero}/registros")
    suspend fun registrosDeSesion(@Path("numero") numero: String): List<RegistroDto>

    @POST("api/operacion/registros")
    suspend fun guardarRegistro(@Body registro: RegistroDto): RegistroDto

    @PUT("api/operacion/sesiones/{sesion}/registros/{numero}/anular")
    suspend fun anularRegistro(@Path("sesion") sesion: String, @Path("numero") numero: String): Response<RegistroDto>
}

/** ms-analitica (puerto 8083) */
interface AnaliticaApi {
    @GET("api/analitica/conteos/por-acceso")
    suspend fun ingresosPorAcceso(@Query("fecha") fecha: String): Map<String, Int>
}
