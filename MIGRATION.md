# Separacion de Procedure, Calibrations y Defects

## Distribucion del codigo

La separacion sigue las secciones 5.4, 5.5 y 5.6 del informe TB2 Arquitecturas:

| Servicio | Modulos de negocio | Entidades persistentes |
| --- | --- | --- |
| Procedures-CafeLab | cuppingSession, preparation | CuppingSession, Recipe, Portfolio, Ingredient |
| Calibrations-CafeLab | calibrations | GrindCalibration |
| Defects-CafeLab | defect | Defect |

Calibrations incluye copias locales de las utilidades shared necesarias, su propia aplicacion Spring Boot, Maven Wrapper, configuracion PostgreSQL/Eureka y pruebas. No necesita compilar Procedures como dependencia.
Los archivos Java de defectos de Procedures se compararon con el servicio existente antes de retirarlos: son equivalentes al normalizar el paquete raiz.

## Datos existentes

Esta extraccion cambia el codigo; no conecta a Supabase, no mueve registros ni elimina tablas. Hibernate con ddl-auto=update tampoco elimina las tablas antiguas al retirar una entidad.
Para completar la separacion de datos en un entorno existente:

1. Respaldar las bases de origen y destino y pausar temporalmente las escrituras de los servicios implicados.
2. Preparar una base PostgreSQL exclusiva para Calibrations y otra para Procedure. Defects conserva la base de su servicio existente. Cada proceso debe recibir sus propias credenciales y DB_URL; no compartir el archivo .env.
3. Crear el esquema de destino desde las entidades del servicio correspondiente. Copiar grind_calibrations, conservando id, user_id y los campos de auditoria, a la base de Calibrations. Si Procedure tambien cambia de base, copiar cupping_sessions, portfolios, recipes e ingredients conservando sus identificadores y relaciones.
4. Si el destino de Defects ya contiene datos, comparar los registros por id y contenido antes de importar cualquier copia antigua; no duplicar ni sobrescribir automaticamente los registros.
5. Restablecer las secuencias de las tablas importadas para que el siguiente id sea mayor que el maximo existente. Comparar conteos, identificadores, auditoria y lecturas por usuario entre origen y destino.
6. Configurar DB_URL, DB_USERNAME y DB_PASSWORD de Calibrations. Para Procedure se agrego DB_URL como override; si no se define, conserva su URL anterior y SUPABASE_PROJECT_REF.
7. Conservar las tablas antiguas durante la verificacion y el periodo de retorno. Su eliminacion fisica queda a una operacion posterior, una vez comprobada la migracion.

## Despliegue coordinado

1. Publicar Calibrations en su propio repositorio y App Service. El workflow incluido verifica y empaqueta; no reutiliza las credenciales ni el destino Azure de Procedures.
2. Configurar EUREKA_URL y verificar los registros Calibrations-CafeLab y Defects-CafeLab. En Azure, SERVER_PORT puede configurarse en 8080; localmente el nuevo servicio usa 8086.
3. Coordinar el despliegue del gateway actualizado con los servicios y las bases ya preparados: /api/v1/calibrations apunta exclusivamente a Calibrations y /api/v1/defects a Defects. Los prefijos publicos y cuerpos no cambian.
4. Desplegar Procedures sin los modulos extraidos y comprobar recetas, portafolios, ingredientes y sesiones de cata.
5. Comprobar desde el gateway crear, listar, consultar y actualizar calibraciones; comprobar tambien las lecturas de defectos. Confirmar que los registros previos siguen disponibles.

No se ha realizado ningun despliegue remoto ni ninguna migracion de datos como parte de la extraccion local.

## Alcance respecto al informe

Se conserva la funcionalidad que existia en el codigo fuente. Las operaciones DELETE y busqueda por nombre, eventos de dominio, validacion de identidad dentro de Calibrations y ACL remota de Procedure hacia Defects descritas en el informe requieren implementacion adicional. El contrato real trasladado esta documentado en README.md.
