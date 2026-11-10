# Calibrations CafeLab - API Reference

## Responsabilidad

Microservicio extraido de procedures-cafelab, conforme al bounded context Calibrations (seccion 5.5 del informe).
Contiene solo calibrations y su shared kernel local; no depende de clases ni artefactos de Procedures o Defects.
Gestiona el agregado GrindCalibration y la tabla grind_calibrations en una base PostgreSQL propia.
El paquete raiz es com.acme.calibrationscafelab y la aplicacion es CalibrationsCafeLabApplication.

## Configuracion y ejecucion

- JDK 17 y Maven Wrapper incluidos.
- Copiar .env.example a .env y configurar DB_URL, DB_USERNAME y DB_PASSWORD para la base exclusiva de Calibrations.
- DB_URL debe ser una URL JDBC PostgreSQL completa. Para Supabase, usar el host, usuario y parametros del proyecto destinado a Calibrations.
- SERVER_PORT: 8086 por defecto; en App Service se puede definir SERVER_PORT=8080.
- EUREKA_URL: servidor Eureka sin el sufijo /eureka; por defecto http://localhost:8761.
- Nombre de registro: Calibrations-CafeLab. El gateway enruta /api/v1/calibrations y sus subrutas a lb://Calibrations-CafeLab.
- Se conserva la configuracion de registro HTTPS de Azure del servicio original. Para Eureka local con HTTP, definir EUREKA_INSTANCE_SECURE_PORT_ENABLED=false, EUREKA_INSTANCE_NON_SECURE_PORT_ENABLED=true y EUREKA_INSTANCE_NON_SECURE_PORT=8086.
- Compilar y ejecutar pruebas: `.\mvnw.cmd clean verify`.
- Ejecutar: `.\mvnw.cmd spring-boot:run`.
- Swagger UI: /swagger-ui/index.html. OpenAPI: /v3/api-docs.
- CI: .github/workflows/build.yml compila, ejecuta pruebas y produce el JAR; el despliegue requiere un App Service propio.

## Contrato conservado

| Operacion | Metodo | Ruta |
| --- | --- | --- |
| Crear | POST | /api/v1/calibrations |
| Listar por usuario | GET | /api/v1/calibrations/user/{userId} |
| Consultar por id | GET | /api/v1/calibrations/user/{userId}/{calibrationId} |
| Actualizar | PUT | /api/v1/calibrations/user/{userId}/{calibrationId} |

Las rutas y los cuerpos son los del modulo original. Las respuestas son application/json; los errores controlados usan MessageResource con message.
Se conserva la seguridad existente: estas rutas son publicas en el microservicio; el API Gateway valida JWT.
El servicio delimita las consultas por userId, pero no coteja por si mismo ese valor con X-User-Id o JWT.

Esta migracion separa los servicios existentes. Las ampliaciones descritas en el informe (DELETE, busqueda por nombre, eventos de dominio y validacion de identidad en el propio servicio) siguen pendientes; no se anuncian como endpoints implementados.
La integracion ACL de Procedure hacia Defects descrita en el informe tampoco existia en el codigo original y no se introduce en esta extraccion.

Las pruebas de dominio, assemblers, controller y flujo funcional de calibraciones se trasladaron a este proyecto.
Ver [MIGRATION.md](MIGRATION.md) para mover los datos y coordinar el despliegue.

---

<h2 style="color:#0A58CA;">Calibrations - Crear calibracion</h2>

<p>
<span style="background:#198754;color:#ffffff;padding:4px 10px;border-radius:999px;font-weight:700;">POST</span>
<code>/api/v1/calibrations</code>
</p>

### Header

| Campo | Descripcion |
| --- | --- |
| `Content-Type` | Requerido. Usar `application/json`. |
| `Accept` | Opcional. Recomendado `application/json`. |

### Parametro (body)

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `userId` | `number` | Requerido. Positivo. |
| `name` | `string` | Requerido. |
| `method` | `string` | Requerido. Metodo de preparacion. |
| `equipment` | `string` | Requerido. Equipo usado. |
| `grindNumber` | `string` | Requerido. Numero o ajuste de molienda. |
| `aperture` | `number` | Requerido. `>= 0`. |
| `cupVolume` | `number` | Requerido. `>= 0`. |
| `finalVolume` | `number` | Requerido. `>= 0`. |
| `calibrationDate` | `string` | Requerido. `YYYY-MM-DD`. |
| `comments` | `string` | `Opcional`. |
| `notes` | `string` | `Opcional`. |
| `sampleImage` | `string` | `Opcional`. Puede ser URL o data URI base64. |

### Request Example

```json
{
  "userId": 9201,
  "name": "Calibracion funcional",
  "method": "v60",
  "equipment": "Comandante",
  "grindNumber": "15",
  "aperture": 1.1,
  "cupVolume": 200.0,
  "finalVolume": 180.0,
  "calibrationDate": "2026-05-05",
  "comments": "ok",
  "notes": "ok",
  "sampleImage": null
}
```

### Success Response

**HTTP 201**

```json
{
  "id": 1,
  "userId": 9201,
  "name": "Calibracion funcional",
  "method": "v60",
  "equipment": "Comandante",
  "grindNumber": "15",
  "aperture": 1.1,
  "cupVolume": 200.0,
  "finalVolume": 180.0,
  "calibrationDate": "2026-05-05",
  "comments": "ok",
  "notes": "ok",
  "sampleImage": null
}
```

### Default Response

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `id` | `number` | Identificador de la calibracion. |
| `userId` | `number` | Propietario. |
| `name` | `string` | Nombre del registro. |
| `method` | `string` | Metodo. |
| `equipment` | `string` | Equipo. |
| `grindNumber` | `string` | Ajuste de molienda. |
| `aperture` | `number` | Apertura. |
| `cupVolume` | `number` | Volumen de taza. |
| `finalVolume` | `number` | Volumen final. |
| `calibrationDate` | `string` | Fecha de calibracion. |
| `comments` | `string` | Comentarios. |
| `notes` | `string` | Notas. |
| `sampleImage` | `string` | Imagen de muestra. |

### Extra Response

No aplica.

### Error 4xx

Error 400:

```json
{
  "message": "userId is required and must be positive"
}
```

```json
{
  "message": "No se pudo crear la calibración"
}
```

---

<h2 style="color:#0A58CA;">Calibrations - Listar calibraciones por usuario</h2>

<p>
<span style="background:#0A58CA;color:#ffffff;padding:4px 10px;border-radius:999px;font-weight:700;">GET</span>
<code>/api/v1/calibrations/user/{userId}</code>
</p>

### Header

| Campo | Descripcion |
| --- | --- |
| `Accept` | Opcional. Recomendado `application/json`. |

### Parametro (path)

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `userId` | `number` | Requerido. |

### Request Example

```http
GET /api/v1/calibrations/user/9201
Accept: application/json
```

### Success Response

**HTTP 200**

Arreglo de `GrindCalibrationResource`.

### Default Response

Igual que **Calibrations - Crear calibracion**.

### Extra Response

No aplica.

### Error 4xx

Listado vacio devuelve `[]`.

---

<h2 style="color:#0A58CA;">Calibrations - Obtener calibracion por id</h2>

<p>
<span style="background:#0A58CA;color:#ffffff;padding:4px 10px;border-radius:999px;font-weight:700;">GET</span>
<code>/api/v1/calibrations/user/{userId}/{calibrationId}</code>
</p>

### Header

| Campo | Descripcion |
| --- | --- |
| `Accept` | Opcional. Recomendado `application/json`. |

### Parametro (path)

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `userId` | `number` | Requerido. |
| `calibrationId` | `number` | Requerido. |

### Request Example

```http
GET /api/v1/calibrations/user/9201/1
Accept: application/json
```

### Success Response

**HTTP 200**

Objeto `GrindCalibrationResource`.

### Default Response

Igual que **Calibrations - Crear calibracion**.

### Extra Response

No aplica.

### Error 4xx

Error 404:

```json
{
  "message": "Calibración no encontrada"
}
```

---

<h2 style="color:#0A58CA;">Calibrations - Actualizar calibracion</h2>

<p>
<span style="background:#6F42C1;color:#ffffff;padding:4px 10px;border-radius:999px;font-weight:700;">PUT</span>
<code>/api/v1/calibrations/user/{userId}/{calibrationId}</code>
</p>

### Header

| Campo | Descripcion |
| --- | --- |
| `Content-Type` | Requerido. Usar `application/json`. |
| `Accept` | Opcional. Recomendado `application/json`. |

### Parametro (path)

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `userId` | `number` | Requerido. |
| `calibrationId` | `number` | Requerido. |

### Parametro (body)

Mismos campos que crear, excepto `userId` (va solo en la ruta).

| Campo | Tipo | Descripcion |
| --- | --- | --- |
| `name` | `string` | Requerido. |
| `method` | `string` | Requerido. |
| `equipment` | `string` | Requerido. |
| `grindNumber` | `string` | Requerido. |
| `aperture` | `number` | Requerido. `>= 0`. |
| `cupVolume` | `number` | Requerido. `>= 0`. |
| `finalVolume` | `number` | Requerido. `>= 0`. |
| `calibrationDate` | `string` | Requerido. |
| `comments` | `string` | `Opcional`. |
| `notes` | `string` | `Opcional`. |
| `sampleImage` | `string` | `Opcional`. |

### Request Example

```json
{
  "name": "Calibracion con imagen",
  "method": "v60",
  "equipment": "Comandante",
  "grindNumber": "15",
  "aperture": 1.1,
  "cupVolume": 200.0,
  "finalVolume": 180.0,
  "calibrationDate": "2026-05-05",
  "sampleImage": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUg"
}
```

### Success Response

**HTTP 200**

Objeto `GrindCalibrationResource` actualizado.

### Default Response

Igual que **Calibrations - Crear calibracion**.

### Extra Response

No aplica.

### Error 4xx

Error 404:

```json
{
  "message": "Calibración no encontrada"
}
```

---
