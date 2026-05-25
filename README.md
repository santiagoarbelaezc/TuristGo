<div align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&height=120&section=header&animation=fadeIn" />
</div>

<h1 align="center">🌍 TuristGo - App de Turismo Inteligente</h1>

<h3 align="center">🚀 Plataforma móvil para descubrir lugares, crear rutas y gamificar tus viajes</h3>

<p align="center">
  Aplicación Android nativa desarrollada con Kotlin y Jetpack Compose.<br>
  Incluye mapas, sugerencias con IA, y un sistema de reputación para la comunidad de viajeros.
</p>

---

## 📋 **Descripción del Proyecto**

**TuristGo** es una aplicación móvil moderna orientada al turismo inteligente. La plataforma permite a los usuarios descubrir lugares, crear rutas turísticas, recibir sugerencias mediante IA y gamificar su experiencia de viaje a través de un sistema de puntos e insignias.

---

## ✨ **Características Principales**

- **Feed de Descubrimiento:** Visualiza lugares populares en una lista o en un mapa interactivo.
- **Asistente IA:** Clasificación automática de categorías y sugerencias personalizadas con Gemini API.
- **Planificación de Viajes IA:** Integración con asistentes de IA para planificar itinerarios de viaje de forma inteligente.
- **Sistema de Reputación:** Acumula puntos, sube de nivel y gana insignias (Badges) por tu actividad.
- **Rutas Turísticas:** Crea, comparte y sigue rutas optimizadas que conectan diferentes puntos de interés.
- **Moderación Comunitaria:** Un panel de moderadores se encarga de verificar la calidad de los posts.

---

## 🤖 **Requisito de Inteligencia Artificial**

TuristGo implementa **Clasificación automática de categorías y moderación activa** como requisitos de IA:

> Al momento de crear una publicación o actualizar una foto de perfil, el sistema analiza la descripción o la imagen mediante el SDK de Google Generative AI y clasifica/modera la seguridad del contenido en tiempo real.

**Modelo utilizado:** Google Gemini API (Gemini 1.5 Flash)

---

## 🔧 **Stack Tecnológico**

### **Mobile (Android Nativo)**
<div align="center">
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img width="8" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" />
  <img width="8" />
  <img src="https://img.shields.io/badge/Material%20You-4285F4?style=for-the-badge&logo=material-design&logoColor=white" />
</div>

### **Backend & Servicios**
<div align="center">
  <img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" />
  <img width="8" />
  <img src="https://img.shields.io/badge/Google%20Gemini-4285F4?style=for-the-badge&logo=google&logoColor=white" />
  <img width="8" />
  <img src="https://img.shields.io/badge/Cloudinary-000000?style=for-the-badge&logo=cloudinary&logoColor=white" />
</div>

### **Mapas**
<div align="center">
  <img src="https://img.shields.io/badge/Google%20Maps-4285F4?style=for-the-badge&logo=googlemaps&logoColor=white" />
</div>

---

## 🗺️ **Fases del Proyecto**

### **📐 Fase 1 — Diseño (Mockups)**

Diseño de todas las pantallas necesarias para resolver los requerimientos del proyecto, siguiendo las guías de diseño de **Material You** de Google. Los prototipos se realizan en formato digital usando **Figma**.

---

### **⚙️ Fase 2 — Funcionalidades Básicas**

Implementación de toda la parte funcional de la aplicación: pantallas, navegación y lógica principal. Los datos se manejaban inicialmente en memoria (sin persistencia en base de datos).

---

### **🚀 Fase 3 — Funcionalidades Completas (Fase Actual)**

Entrega final con todas las funcionalidades integradas, incluyendo persistencia, autenticación y servicios en la nube.

**Entregables:**
- 🔐 **Autenticación** con Firebase Auth (login, registro, recuperación de contraseña).
- 🗄️ **Persistencia** en tiempo real con Firebase Firestore.
- 🗺️ **Mapas** interactivos con Google Maps SDK.
- 🤖 **IA completa:** clasificación de categorías y moderación de imágenes con Gemini + planificación de viajes con Groq LLM.
- 🖼️ **Subida de imágenes** remota con Cloudinary.

---

## 🔥 **Integración con Firebase**

El proyecto cuenta con una integración completa a los servicios de Firebase para toda la capa de datos y seguridad:

*   🔐 **Autenticación (Firebase Auth):**
    *   Registro e inicio de sesión convencional con Correo y Contraseña.
    *   Inicio de sesión social integrado con **Google Sign-In** mediante el nuevo Credential Manager de Android.
    *   Flujo real de recuperación de contraseña enviando correos de restablecimiento de contraseña automatizados.
    *   Gestión de cierres de sesión sincronizando la sesión remota (`firebaseAuth.signOut()`) con el almacenamiento de estado local (`DataStore`).
*   🗄️ **Base de Datos (Cloud Firestore):**
    *   Persistencia completa reactiva mediante `Flow` de Kotlin en lugar de almacenamiento temporal en memoria.
    *   **Usuarios (`users/`):** Almacena perfiles detallados sincronizados con su UID de autenticación, listas de seguidores, seguidos, posts guardados y me gusta.
    *   **Publicaciones (`posts/`):** Base de datos de destinos turísticos y eventos moderados, vinculada a los autores de las publicaciones.
    *   **Comentarios (`comments/`):** Gestión de hilos de comentarios para cada publicación.
    *   **Notificaciones (`notifications/`):** Sistema de notificaciones en tiempo real para solicitudes de amistad, aprobaciones de posts, etc.
    *   **Chat en Tiempo Real (`users/{uid}/chat_messages/`):** Registro persistente en tiempo real de los itinerarios y mensajes de chat de planificación turística asistida por IA.

---

## 📁 **Estructura del Proyecto (Capa UI)**

```text
ui/
ui/
├── theme/          # Material You (Colores, Tipografía, Formas)
├── navigation/     # NavHost y Definición de Rutas
├── components/     # Composables reutilizables (Cards, Chips, etc.)
├── auth/           # Login, Registro, Recuperación de contraseña, Completar Perfil
├── feed/           # Feed principal (Lista/Mapa)
├── home/           # Pantalla de inicio
├── post/           # Detalle, Creación y Edición de Posts
├── profile/        # Perfil, Estadísticas e Insignias
├── notifications/  # Centro de notificaciones
├── moderator/      # Panel de revisión para moderadores
├── route/          # Gestión de rutas turísticas
└── util/           # Utilidades (Ubicación, Permisos, Fechas)
```

---

## 🛠️ **Configuración Local**

1. Clona el repositorio.
2. Añade tu archivo `google-services.json` (descargado de la consola de Firebase) en la carpeta `app/`.
3. Crea un archivo `.env` en la raíz del proyecto y agrega tus variables de entorno configuradas:
   ```env
   GROQ_API_KEY=tu_api_key_de_groq
   GEMINI_API_KEY=tu_api_key_de_gemini
   GOOGLE_WEB_CLIENT_ID=tu_web_client_id_de_google_auth
   CLOUDINARY_CLOUD_NAME=tu_cloud_name_de_cloudinary
   CLOUDINARY_API_KEY=tu_api_key_de_cloudinary
   CLOUDINARY_API_SECRET=tu_api_secret_de_cloudinary
   GOOGLE_MAPS_API_KEY=tu_api_key_de_google_maps
   ```
4. Sincroniza el proyecto con Gradle y ejecuta en tu emulador o dispositivo Android.

---

## 👨‍💻 **Desarrolladores**

<div align="center">

### Santiago Arbelaez Contreras
Junior Full Stack Developer · Estudiante de Ingeniería de Sistemas – Universidad del Quindío

<a href="https://github.com/santiagoarbelaezc"><img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white" /></a>
<img width="10" />
<a href="https://www.linkedin.com/in/santiago-arbelaez-contreras-9830b5290/"><img src="https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white" /></a>
<img width="10" />
<a href="https://portfolio-santiagoa.web.app/portfolio"><img src="https://img.shields.io/badge/Portfolio-6C63FF?style=for-the-badge&logo=sparkles&logoColor=white" /></a>

---

### Eliana Hernandez
Estudiante de Ingeniería de Sistemas – Universidad del Quindío

---

### Juan David Gutierrez
Estudiante de Ingeniería de Sistemas – Universidad del Quindío

</div>

---

<div align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&height=90&section=footer&animation=fadeIn" />
  <p>© 2026 TuristGo - Universidad del Quindío · Todos los derechos reservados</p>
</div>
