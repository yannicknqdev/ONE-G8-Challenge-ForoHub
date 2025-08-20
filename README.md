# 💬 ForoHub - API REST

> **Challenge Backend** - API REST para gestión de foros desarrollada con Spring Boot

ForoHub es una API REST que replica la funcionalidad backend de un foro, permitiendo a los usuarios crear, consultar, actualizar y eliminar tópicos de discusión. Implementa autenticación JWT y control de acceso para garantizar la seguridad de las operaciones.

## 🎯 Descripción del Desafío

Un foro es un lugar donde los participantes pueden hacer preguntas sobre determinados temas. En Alura, los estudiantes utilizan el foro para resolver dudas sobre cursos y proyectos. Este proyecto replica el backend de este sistema, implementando una API REST completa.

### Funcionalidades Principales

- ✅ **CRUD completo de tópicos**
- ✅ **CRUD completo de usuarios**
- ✅ **CRUD completo de respuestas**
- ✅ **Sistema de autenticación JWT**
- ✅ **Control de acceso y autorización**
- ✅ **Validaciones de reglas de negocio**
- ✅ **Paginación y filtros de búsqueda**
- ✅ **Sistema de marcado de soluciones**
- ✅ **Estadísticas y métricas**
- ✅ **Búsqueda global**
- ✅ **Manejo de errores y excepciones**

## 🛠️ Tecnologías Utilizadas

![Java](https://img.shields.io/badge/Java-24-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white)

### Dependencias Principales

- **Spring Boot Starter Web** - Para crear la API REST
- **Spring Boot Starter Data JPA** - Para persistencia de datos
- **Spring Boot Starter Security** - Para autenticación y autorización
- **Spring Boot Starter Validation** - Para validaciones
- **MySQL Connector** - Driver de base de datos
- **Flyway** - Para migraciones de base de datos
- **Lombok** - Para reducir código boilerplate
- **Auth0 JWT** - Para manejo de tokens JWT

## 🏗️ Arquitectura del Proyecto

```
src/
├── main/
│   ├── java/com/alura/forohub/
│   │   ├── controller/           # Controladores REST
│   │   ├── domain/               # Entidades y repositorios
│   │   │   ├── curso/
│   │   │   ├── perfil/
│   │   │   ├── respuesta/
│   │   │   ├── topico/
│   │   │   └── usuario/
│   │   ├── infra/                # Infraestructura
│   │   │   ├── errores/          # Manejo de errores
│   │   │   └── security/         # Configuración de seguridad
│   │   └── ForohubApplication.java
│   └── resources/
│       ├── db/migration/         # Scripts de migración Flyway
│       └── application.properties
```

## 📊 Modelo de Base de Datos

### Entidades Principales

- **Usuario**: Información de usuarios del foro
- **Perfil**: Roles de usuarios (Estudiante, Instructor)
- **Curso**: Cursos disponibles en la plataforma
- **Topico**: Preguntas/temas de discusión
- **Respuesta**: Respuestas a los tópicos

### Relaciones

- Un usuario pertenece a un perfil
- Un tópico pertenece a un curso y es creado por un usuario
- Una respuesta pertenece a un tópico y es creada por un usuario

## 🚀 Instalación y Configuración

### Prerrequisitos

- ☕ Java 24+
- 🐬 MySQL 8.0+
- 📦 Maven 3.6+

### Configuración de Base de Datos

1. Crear base de datos MySQL:
```sql
CREATE DATABASE forohub;
```

2. Configurar credenciales en `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/forohub?createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

### Ejecución del Proyecto

1. **Clonar el repositorio**
```bash
git clone https://github.com/yannicknqdev/ONE-G8-Challenge-ForoHub.git
cd ONE-G8-Challenge-ForoHub
```

2. **Instalar dependencias**
```bash
mvn clean install
```

3. **Ejecutar la aplicación**
```bash
mvn spring-boot:run
```

4. **La API estará disponible en**: `http://localhost:8080`

## 🔐 Autenticación

La API utiliza **JWT (JSON Web Tokens)** para autenticación. 

### Flujo de Autenticación

1. **Registro** → Crear cuenta de usuario
2. **Login** → Obtener token JWT
3. **Usar token** → Incluir en header `Authorization: Bearer [TOKEN]`

### Usuarios de Prueba

| Email | Contraseña | Perfil |
|-------|------------|--------|
| `juan@email.com` | `123456` | Estudiante |
| `maria@email.com` | `123456` | Estudiante |
| `carlos@email.com` | `123456` | Instructor |

## 📡 Endpoints de la API

### 🔓 Endpoints Públicos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/registro` | Registrar nuevo usuario |
| `POST` | `/login` | Autenticar usuario |

### 🔒 Endpoints Protegidos (Requieren JWT)

#### 📝 Gestión de Tópicos
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/topicos` | Crear nuevo tópico |
| `GET` | `/topicos` | Listar tópicos (con paginación) |
| `GET` | `/topicos/{id}` | Obtener tópico específico |
| `PUT` | `/topicos/{id}` | Actualizar tópico |
| `DELETE` | `/topicos/{id}` | Eliminar tópico |

#### 👥 Gestión de Usuarios
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/usuarios` | Listar usuarios (con paginación) |
| `GET` | `/usuarios/{id}` | Obtener usuario específico |
| `PUT` | `/usuarios/{id}` | Actualizar usuario |
| `DELETE` | `/usuarios/{id}` | Eliminar usuario |

#### 💬 Gestión de Respuestas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/respuestas` | Crear nueva respuesta |
| `GET` | `/respuestas` | Listar respuestas (con filtros) |
| `GET` | `/respuestas/{id}` | Obtener respuesta específica |
| `PUT` | `/respuestas/{id}` | Actualizar respuesta |
| `DELETE` | `/respuestas/{id}` | Eliminar respuesta |
| `PATCH` | `/respuestas/{id}/solucion` | Marcar como solución |

#### 📊 Funcionalidades Adicionales
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/estadisticas` | Estadísticas generales |
| `GET` | `/estadisticas/resumen` | Resumen completo |
| `GET` | `/buscar?q={termino}` | Búsqueda global |

## 🧪 Ejemplos de Uso

### 1. Registro de Usuario

```http
POST http://localhost:8080/registro
Content-Type: application/json

{
    "nombre": "Nuevo Usuario",
    "correoElectronico": "nuevo@email.com",
    "contrasena": "123456"
}
```

**Respuesta:**
```json
{
    "id": 4,
    "nombre": "Nuevo Usuario",
    "correoElectronico": "nuevo@email.com",
    "perfil": "ESTUDIANTE",
    "fechaRegistro": "2024-08-20T14:30:00"
}
```

### 2. Login

```http
POST http://localhost:8080/login
Content-Type: application/json

{
    "correoElectronico": "juan@email.com",
    "contrasena": "123456"
}
```

**Respuesta:**
```json
{
    "jwTtoken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."
}
```

### 3. Crear Tópico

```http
POST http://localhost:8080/topicos
Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
    "titulo": "¿Cómo usar Spring Security?",
    "mensaje": "Tengo dudas sobre la configuración de Spring Security en mi proyecto",
    "autorId": 1,
    "cursoId": 1
}
```

**Respuesta:**
```json
{
    "id": 1,
    "titulo": "¿Cómo usar Spring Security?",
    "mensaje": "Tengo dudas sobre la configuración de Spring Security en mi proyecto",
    "fechaCreacion": "2024-08-20T14:35:00",
    "status": "NO_RESPONDIDO",
    "autor": "Juan Pérez",
    "curso": "Spring Boot Básico"
}
```

### 4. Listar Tópicos con Filtros

```http
GET http://localhost:8080/topicos?curso=Spring Boot Básico&page=0&size=10
Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...
```

### 5. Actualizar Tópico

```http
PUT http://localhost:8080/topicos/1
Authorization: Bearer eyJ0eXAiOaWciOiJIUzI1NiJ9...
Content-Type: application/json

{
    "titulo": "¿Cómo usar Spring Security? - RESUELTO",
    "mensaje": "Ya entendí la configuración, gracias por la ayuda",
    "status": "SOLUCIONADO",
    "cursoId": 1
}
```

### 6. Crear Respuesta

```http
POST http://localhost:8080/respuestas
Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
    "mensaje": "Debes configurar el SecurityConfig correctamente",
    "topicoId": 1,
    "autorId": 2,
    "solucion": false
}
```

### 7. Listar Respuestas de un Tópico

```http
GET http://localhost:8080/respuestas?topicoId=1
Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...
```

### 8. Marcar Respuesta como Solución

```http
PATCH http://localhost:8080/respuestas/1/solucion
Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...
```

### 9. Obtener Estadísticas

```http
GET http://localhost:8080/estadisticas
Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...
```

### 10. Búsqueda Global

```http
GET http://localhost:8080/buscar?q=Spring
Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...
```

## 🔍 Validaciones y Reglas de Negocio

- ✅ Todos los campos obligatorios deben estar presentes
- ✅ No se permiten tópicos duplicados (mismo título y mensaje)
- ✅ Email debe tener formato válido
- ✅ Contraseña debe tener mínimo 6 caracteres
- ✅ Solo usuarios autenticados pueden hacer operaciones CRUD
- ✅ Los tópicos se crean con estado inicial "NO_RESPONDIDO"

## 📄 Estados de Tópicos

| Estado | Descripción |
|--------|-------------|
| `NO_RESPONDIDO` | Tópico recién creado |
| `NO_SOLUCIONADO` | Tiene respuestas pero no está resuelto |
| `SOLUCIONADO` | Problema resuelto |
| `CERRADO` | Tópico cerrado |

## 🐛 Manejo de Errores

La API maneja los siguientes códigos de error HTTP:

| Código | Descripción |
|--------|-------------|
| `200` | OK - Operación exitosa |
| `400` | Bad Request - Error de validación |
| `401` | Unauthorized - No autenticado |
| `403` | Forbidden - Sin permisos |
| `404` | Not Found - Recurso no encontrado |
| `500` | Internal Server Error - Error del servidor |

## 🧪 Testing con Postman

1. **Importar colección** con los endpoints
2. **Crear usuario** con `/registro`
3. **Hacer login** y copiar el token
4. **Configurar Authorization** → Bearer Token
5. **Probar endpoints** protegidos

## 🔧 Configuraciones Avanzadas

### Variables de Entorno

```properties
# JWT Configuration
JWT_SECRET=tu-secreto-super-seguro
api.security.token.expiration=3600000

# Database Configuration
DB_URL=jdbc:mysql://localhost:3306/forohub
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_contraseña
```
### 🏆 Challenge Backend - Alura LATAM

> Proyecto desarrollado como parte del programa ONE (Oracle Next Education) en colaboración con Alura LATAM.

**⭐ Si este proyecto te fue útil, no olvides darle una estrella en GitHub**
