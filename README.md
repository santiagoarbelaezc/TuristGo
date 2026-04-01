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

> ⚠️ **Estado del Proyecto:** En desarrollo activo

---

## ✨ **Características Principales**

- **Feed de Descubrimiento:** Visualiza lugares populares en una lista o en un mapa interactivo (Mapbox).
- **Asistente IA:** Clasificación automática de categorías y sugerencias personalizadas con Gemini API.
- **Planificación de Viajes IA:** Integración con Minimax o DeepSeek para planificar itinerarios de viaje de forma inteligente.
- **Sistema de Reputación:** Acumula puntos, sube de nivel y gana insignias (Badges) por tu actividad.
- **Rutas Turísticas:** Crea, comparte y sigue rutas optimizadas que conectan diferentes puntos de interés.
- **Moderación Comunitaria:** Un panel de moderadores se encarga de verificar la calidad de los posts.

---

## 🤖 **Requisito de Inteligencia Artificial**

TuristGo implementa **Clasificación automática de categorías** como requisito de IA:

> Al momento de crear una publicación de un lugar turístico, el sistema analiza el título y la descripción ingresados por el usuario y sugiere automáticamente la categoría más apropiada (playa, montaña, museo, gastronomía, aventura, etc.). El usuario puede aceptar la sugerencia o seleccionar manualmente otra categoría.

**Modelo utilizado:** Google Gemini API

Adicionalmente, se integra una sección de planificación de viajes con IA (Minimax / DeepSeek) que genera itinerarios personalizados basados en los intereses y ubicación del usuario.

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
</div>

### **Mapas**
<div align="center">
  <img src="https://img.shields.io/badge/Mapbox-000000?style=for-the-badge&logo=mapbox&logoColor=white" />
</div>

---

## 🗺️ **Fases del Proyecto**

### **📐 Fase 1 — Diseño (Mockups)**

Diseño de todas las pantallas necesarias para resolver los requerimientos del proyecto, siguiendo las guías de diseño de **Material You** de Google. Los prototipos se realizan en formato digital usando **Figma**.

**Pantallas diseñadas:**
- Splash & Onboarding
- Login / Registro / Recuperación de contraseña
- Feed principal (Lista y Mapa)
- Detalle de lugar turístico
- Creación y edición de publicación (con sugerencia IA de categoría)
- Perfil de usuario con estadísticas e insignias
- Panel de moderación
- Gestión de rutas turísticas
- Sección de planificación de viajes con IA
- Centro de notificaciones

---

### **⚙️ Fase 2 — Funcionalidades Básicas**

Implementación de toda la parte funcional de la aplicación: pantallas, navegación y lógica principal. Los datos se manejan en memoria (sin persistencia en base de datos).

**Entregables:**
- Navegación completa entre pantallas con NavHost
- Arquitectura MVVM con repositorios en memoria
- Inyección de dependencias con Hilt
- Feed de lugares con lista y mapa (Mapbox)
- Creación de publicaciones con clasificación automática de categoría por IA (Gemini)
- Sistema de reputación: puntos e insignias
- Rutas turísticas: creación y visualización
- Panel de moderación funcional
- Sección de planificación de viajes con IA (Minimax / DeepSeek)

---

### **🚀 Fase 3 — Funcionalidades Completas**

Entrega final con todas las funcionalidades integradas, incluyendo persistencia, autenticación y servicios en la nube.

**Entregables:**
- 🔐 **Autenticación** con Firebase Auth (login, registro, recuperación de contraseña)
- 🗄️ **Persistencia** con Firebase Firestore o Realtime Database
- 🖼️ **Subida de imágenes** con Firebase Storage
- 🗺️ **Mapas** con Mapbox (mapa interactivo de lugares)
- 🌐 **Internacionalización** (soporte para múltiples idiomas)
- 🤖 **IA completa:** clasificación de categorías con Gemini + planificación de viajes con Minimax/DeepSeek
- 🔑 **Recuperación de contraseña** por correo electrónico vía Firebase

---

## 📁 **Estructura del Proyecto (Capa UI)**

```text
ui/
├── theme/          # Material You (Colores, Tipografía, Formas)
├── navigation/     # NavHost y Definición de Rutas
├── components/     # Composables reutilizables (Cards, Chips, etc.)
├── auth/           # Login, Registro, Recuperación de contraseña
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
2. Añade tu `google-services.json` en la carpeta `app/`.
3. Configura tus claves de API (Mapbox, Gemini) en `secrets.properties`.
4. Sincroniza el proyecto con Gradle y ejecuta en un emulador o dispositivo Android.

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
