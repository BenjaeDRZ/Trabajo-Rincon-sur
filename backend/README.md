# Backend Rincón Sur — microservicios

| Servicio      | Puerto | BD (MySQL)     | Qué usa la app |
|---------------|--------|----------------|----------------|
| eureka-server | 8761   | —              | registro de servicios |
| ms-auth       | 8081   | `db_auth`      | `POST /api/auth/login` (login) |
| ms-operacion  | 8082   | `db_operacion` | sesiones y registros de personas |
| ms-analitica  | 8083   | `db_analitica` | `GET /api/analitica/conteos/por-acceso?fecha=` (indicadores) |
| ms-reportes   | 8084   | `db_reportes`  | (aún no se usa desde la app) |

## Cómo probar con la app

1. En Laragon/HeidiSQL crea las BD `db_auth`, `db_operacion`, `db_analitica`, `db_reportes`.
2. Levanta `eureka-server` y luego los `ms-*` (launch.json de VS Code o `./mvnw spring-boot:run`).
   - `ms-auth` crea los colaboradores C-01…C-07 (contraseña `12345`) si la tabla está vacía.
   - `ms-analitica` crea los conteos de ejemplo del 2026-09-15 si la tabla está vacía.
3. Corre la app en el **emulador**: usa `10.0.2.2` para llegar al PC.
   En un **celular físico**, cambia `HOST` en
   `Trabajo-Rincon-sur/app/src/main/java/cl/duocuc/miregistro/data/remote/ApiConfig.kt`
   por la IP del PC (misma red Wi-Fi).

## Cómo se conecta la app

- **Login**: valida contra `ms-auth`. Si el servidor no responde, usa la lista local (modo offline).
- **Sesiones y registros**: se guardan en SQLite y se envían a `ms-operacion` al momento
  (programar, registrar, anular, finalizar, validar/rechazar). Lo que no se pudo enviar se
  reenvía en la próxima sincronización (al abrir la app, Observador o Supervisor).
- **Indicadores**: los ingresos por acceso se traen de `ms-analitica`; si no responde, se usan los locales.
