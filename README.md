# 🎓 EducaGestor360

Sistema integral de gestión educativa desarrollado con **Spring Boot** y **React** que permite administrar instituciones educativas de manera eficiente y moderna.

## ✨ Características Principales

- 👥 **Gestión de Usuarios**: Administradores, profesores y estudiantes con roles específicos
- 🏫 **Gestión de Instituciones**: Soporte para múltiples instituciones educativas
- 🎓 **Gestión de Estudiantes**: Registro completo, seguimiento académico y administración
- 👨‍🏫 **Gestión de Profesores**: Perfiles profesionales, especializaciones y asignaciones
- 📚 **Gestión de Cursos**: Creación, programación y administración de cursos
- 📝 **Sistema de Matrículas**: Inscripción automática de estudiantes en cursos
- 📊 **Gestión de Calificaciones**: Registro, cálculo y seguimiento de notas
- 📅 **Control de Asistencia**: Registro diario con múltiples estados
- 📁 **Recursos Educativos**: Gestión de materiales, documentos y enlaces
- 🔐 **Autenticación JWT**: Sistema seguro con refresh tokens
- 📈 **Dashboard Interactivo**: Estadísticas y métricas en tiempo real

## 🚀 Tecnologías Utilizadas

### Backend

- ☕ **Java 17** - Lenguaje de programación
- 🍃 **Spring Boot 3.2** - Framework principal
- 🔒 **Spring Security** - Autenticación y autorización JWT
- 🗄️ **Spring Data JPA** - Persistencia de datos
- 🐘 **PostgreSQL 15** - Base de datos relacional
- 📦 **Maven** - Gestión de dependencias
- 🐳 **Docker** - Containerización

### Frontend

- ⚛️ **React 18** - Biblioteca de UI
- 📘 **TypeScript** - Tipado estático
- ⚡ **Vite** - Build tool y dev server
- 🎨 **Tailwind CSS** - Framework de estilos
- 🧩 **ShadCN/UI** - Componentes de UI
- 🔄 **React Query** - Gestión de estado del servidor
- 🐻 **Zustand** - Gestión de estado global
- 🛣️ **React Router** - Enrutamiento

## 📁 Estructura del Proyecto

```
educagestor360/
├── 🔧 backend/                 # Aplicación Spring Boot
│   ├── src/main/java/com/educagestor/
│   │   ├── 📊 entity/          # Entidades JPA (9 entidades)
│   │   ├── 🗃️ repository/      # Repositorios con consultas personalizadas
│   │   ├── ⚙️ service/         # Servicios de negocio
│   │   ├── 🌐 controller/      # Controladores REST
│   │   ├── 📦 dto/             # Data Transfer Objects
│   │   ├── ⚙️ config/          # Configuraciones (Security, OpenAPI)
│   │   ├── 🔐 security/        # JWT Token Provider y filtros
│   │   └── ❌ exception/       # Manejo global de excepciones
│   ├── src/main/resources/
│   │   ├── application.yml     # Configuración de la aplicación
│   │   └── data.sql           # Datos iniciales
│   └── src/test/              # Pruebas unitarias
├── 🎨 frontend/               # Aplicación React
│   ├── src/
│   │   ├── 🧩 components/     # Componentes React reutilizables
│   │   ├── 📄 pages/          # Páginas de la aplicación
│   │   ├── 🌐 services/       # Servicios API con Axios
│   │   ├── 🐻 store/          # Estado global con Zustand
│   │   ├── 🛠️ lib/            # Utilidades y helpers
│   │   └── 🎨 styles/         # Estilos globales
├── 🗄️ database/              # Scripts de base de datos
│   └── init.sql              # Esquema inicial
├── 🐳 docker-compose.yml     # PostgreSQL + PgAdmin
└── 📚 README.md              # Documentación
```

## 🛠️ Instalación y Configuración

### 📋 Prerrequisitos

- ☕ Java 17+
- 📦 Node.js 18+
- 🐳 Docker y Docker Compose
- 🔧 Maven 3.8+

### 1️⃣ Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/educagestor360.git
cd educagestor360
```

### 2️⃣ Configurar la base de datos

```bash
# Iniciar PostgreSQL y PgAdmin con Docker
docker-compose up -d

# Verificar que los contenedores estén ejecutándose
docker-compose ps
```

### 3️⃣ Configurar el backend

```bash
cd backend

# Instalar dependencias y compilar
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run

# O ejecutar con perfil específico
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 4️⃣ Configurar el frontend

```bash
cd frontend

# Instalar dependencias
npm install

# Ejecutar en modo desarrollo
npm run dev

# O construir para producción
npm run build
```

## 🌐 Acceso a la Aplicación

| Servicio           | URL                                   | Descripción                 |
| ------------------ | ------------------------------------- | --------------------------- |
| 🎨 **Frontend**    | http://localhost:5173                 | Interfaz de usuario React   |
| 🔧 **Backend API** | http://localhost:8080/api             | API REST                    |
| 📚 **Swagger UI**  | http://localhost:8080/swagger-ui.html | Documentación interactiva   |
| 🗄️ **PgAdmin**     | http://localhost:5050                 | Administrador de PostgreSQL |
| 🐘 **PostgreSQL**  | localhost:5432                        | Base de datos               |

### 🔑 Credenciales por defecto

- **PgAdmin**: `admin@admin.com` / `admin`
- **PostgreSQL**: `educagestor` / `educagestor123`
- **Base de datos**: `educagestor`

## Licencia

Este proyecto está bajo la Licencia MIT.
