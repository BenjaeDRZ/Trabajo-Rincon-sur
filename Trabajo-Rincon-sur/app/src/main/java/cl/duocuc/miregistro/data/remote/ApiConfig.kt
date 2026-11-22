package cl.duocuc.miregistro.data.remote

/**
 * Direcciones de los microservicios (carpeta /backend).
 *
 * - Emulador de Android Studio: 10.0.2.2 apunta al "localhost" del PC donde corren los servicios.
 * - Celular físico: reemplaza HOST por la IP del PC en la misma red Wi-Fi (ej. "192.168.1.50").
 */
object ApiConfig {
    const val HOST = "10.0.2.2"

    const val URL_AUTH = "http://$HOST:8081/"       // ms-auth
    const val URL_OPERACION = "http://$HOST:8082/"  // ms-operacion
    const val URL_ANALITICA = "http://$HOST:8083/"  // ms-analitica
    const val URL_REPORTES = "http://$HOST:8084/"   // ms-reportes

    /** Si el servidor no responde en este tiempo, la app sigue funcionando con la BD local. */
    const val TIMEOUT_SEGUNDOS = 4L
}
