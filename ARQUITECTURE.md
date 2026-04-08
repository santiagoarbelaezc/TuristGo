<div align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&height=120&section=header&animation=fadeIn" />
</div>

<h1 align="center">🏗️ TuristGo — Guía de Arquitectura y Buenas Prácticas</h1>

<h3 align="center">Especificaciones técnicas para el desarrollo de la aplicación Android</h3>

<p align="center">
  Basado en la documentación oficial del curso <strong>Construcción de Aplicaciones Móviles</strong><br>
  Universidad del Quindío · Ingeniería de Sistemas y Computación<br>
  Docente: Carlos Andrés Florez V.
</p>

---

## 📚 **Tabla de Contenidos**

1. [Visión General de la Arquitectura](#-1-visión-general-de-la-arquitectura)
2. [Estructura de Paquetes del Proyecto](#-2-estructura-de-paquetes-del-proyecto)
3. [Capa de Dominio — Entidades y Repositorios](#-3-capa-de-dominio--entidades-y-repositorios)
4. [Capa de Datos — Implementación de Repositorios](#-4-capa-de-datos--implementación-de-repositorios)
5. [Capa de Presentación — ViewModels](#-5-capa-de-presentación--viewmodels)
6. [Inyección de Dependencias con Hilt](#-6-inyección-de-dependencias-con-hilt)
7. [Navegación con Navigation Compose](#-7-navegación-con-navigation-compose)
8. [Componentes UI Reutilizables](#-8-componentes-ui-reutilizables)
9. [Manejo del Estado](#-9-manejo-del-estado)
10. [Formularios y Validación](#-10-formularios-y-validación)
11. [Listas y Elementos Múltiples](#-11-listas-y-elementos-múltiples)
12. [Imágenes con Coil](#-12-imágenes-con-coil)
13. [Material You y Temas](#-13-material-you-y-temas)
14. [Configuración de Dependencias](#-14-configuración-de-dependencias)
15. [Convenciones de Código](#-15-convenciones-de-código)

---

## 🧭 **1. Visión General de la Arquitectura**

TuristGo sigue la arquitectura **MVVM (Model-View-ViewModel)** con una organización en capas, tal como se recomienda en las guías oficiales de Android y en la documentación del curso.

### **¿Por qué MVVM?**

- Separa claramente la lógica de negocio de la interfaz de usuario.
- Los ViewModels sobreviven a los cambios de configuración (rotación de pantalla).
- Facilita las pruebas unitarias al desacoplar la UI del modelo.
- Permite que múltiples pantallas compartan el mismo repositorio de datos.

### **Capas de la Arquitectura**

```
┌─────────────────────────────────────────────────┐
│              CAPA DE PRESENTACIÓN               │
│    Screens (Composables) + ViewModels           │
│    Observan StateFlow, envían eventos           │
└─────────────────────┬───────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────┐
│               CAPA DE DOMINIO                   │
│    Interfaces de Repositorios + Entidades       │
│    Modelos de datos (data classes, enums)       │
└─────────────────────┬───────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────┐
│               CAPA DE DATOS                     │
│    Implementaciones de Repositorios             │
│    Firebase / API / En memoria                  │
└─────────────────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────┐
│         CAPA DE INYECCIÓN DE DEPENDENCIAS       │
│    Hilt Modules — Configuración de bindings     │
│    (Capa transversal a todas las anteriores)    │
└─────────────────────────────────────────────────┘
```

> **Regla fundamental:** Las capas superiores dependen de las inferiores, nunca al revés. Una pantalla nunca debe conocer la implementación del repositorio, solo su interfaz.

---

## 📁 **2. Estructura de Paquetes del Proyecto**

La organización de paquetes sigue una estructura **feature-first** combinada con la separación por capas de arquitectura.

```text
com.example.turistgo/
│
├── MyApp.kt                        # Clase Application anotada con @HiltAndroidApp
│
├── data/                           # Capa de datos
│   └── repository/                 # Implementaciones concretas de repositorios
│       ├── PlaceRepositoryImpl.kt
│       ├── UserRepositoryImpl.kt
│       └── RouteRepositoryImpl.kt
│
├── di/                             # Inyección de dependencias
│   └── RepositoryModule.kt         # Módulo Hilt que vincula interfaces con implementaciones
│
├── domain/                         # Capa de dominio
│   ├── model/                      # Entidades del dominio
│   │   ├── Place.kt
│   │   ├── User.kt
│   │   ├── Route.kt
│   │   ├── Badge.kt
│   │   ├── Location.kt
│   │   ├── PlaceCategory.kt        # Enum de categorías
│   │   ├── PlaceStatus.kt          # Enum de estados
│   │   └── UserRole.kt             # Enum de roles
│   └── repository/                 # Interfaces de repositorios
│       ├── PlaceRepository.kt
│       ├── UserRepository.kt
│       └── RouteRepository.kt
│
├── core/                           # Utilidades y componentes compartidos
│   ├── components/                 # Composables reutilizables
│   │   ├── DropdownMenu.kt
│   │   ├── ConfirmAlertDialog.kt
│   │   └── ProfileImage.kt
│   ├── navigation/                 # Configuración de navegación
│   │   ├── AppNavigation.kt
│   │   └── AppRoutes.kt
│   └── util/                       # Funciones utilitarias
│       ├── LocationUtils.kt
│       ├── PermissionUtils.kt
│       └── DateUtils.kt
│
└── features/                       # Funcionalidades por módulo
    ├── auth/
    │   ├── login/
    │   │   ├── LoginScreen.kt
    │   │   └── LoginViewModel.kt
    │   └── register/
    │       ├── RegisterScreen.kt
    │       └── RegisterViewModel.kt
    ├── feed/
    │   ├── FeedScreen.kt
    │   └── FeedViewModel.kt
    ├── place/
    │   ├── list/
    │   │   ├── PlaceListScreen.kt
    │   │   └── PlaceListViewModel.kt
    │   ├── detail/
    │   │   ├── PlaceDetailScreen.kt
    │   │   └── PlaceDetailViewModel.kt
    │   └── create/
    │       ├── CreatePlaceScreen.kt
    │       └── CreatePlaceViewModel.kt
    ├── profile/
    │   ├── ProfileScreen.kt
    │   └── ProfileViewModel.kt
    ├── moderator/
    │   ├── ModeratorScreen.kt
    │   └── ModeratorViewModel.kt
    ├── route/
    │   ├── RouteScreen.kt
    │   └── RouteViewModel.kt
    └── notifications/
        ├── NotificationsScreen.kt
        └── NotificationsViewModel.kt
```

---

## 🧩 **3. Capa de Dominio — Entidades y Repositorios**

### **3.1 Entidades del Dominio**

Las entidades son `data class` de Kotlin. Se ubican en `domain/model/`. Toda entidad que almacene datos debe ser una `data class`, no una clase normal, ya que Kotlin genera automáticamente `equals()`, `hashCode()`, `toString()` y `copy()`.

#### `Place.kt`
```kotlin
data class Place(
    val id: String,
    val title: String,
    val description: String,
    val location: Location,
    val category: PlaceCategory,
    val status: PlaceStatus,
    val photoUrl: String,
    val ownerId: String,
    val rating: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)
```

#### `User.kt`
```kotlin
data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val city: String,
    val address: String = "",
    val phoneNumber: String = "",
    val profilePictureUrl: String = "",
    val role: UserRole = UserRole.USER,
    val points: Int = 0,
    val badges: List<Badge> = emptyList()
)
```

#### `Location.kt`
```kotlin
data class Location(
    val latitude: Double,
    val longitude: Double
)
```

#### `Route.kt`
```kotlin
data class Route(
    val id: String,
    val name: String,
    val description: String,
    val places: List<Place>,
    val ownerId: String,
    val createdAt: Long = System.currentTimeMillis()
)
```

#### `Badge.kt`
```kotlin
data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val iconUrl: String
)
```

#### Enumeraciones

```kotlin
// PlaceCategory.kt
enum class PlaceCategory {
    BEACH, MOUNTAIN, MUSEUM, GASTRONOMY, ADVENTURE,
    HISTORICAL, NATURE, URBAN, SPORTS, OTHER
}

// PlaceStatus.kt
enum class PlaceStatus {
    PENDING,
    APPROVED,
    REJECTED
}

// UserRole.kt
enum class UserRole {
    USER,
    MODERATOR,
    ADMIN
}
```

> **Buena práctica:** Los atributos opcionales siempre deben tener un valor por defecto en la `data class`. Nunca usar `null` a menos que sea estrictamente necesario; preferir valores vacíos (`""`, `emptyList()`, `0`).

---

### **3.2 Interfaces de Repositorios**

Las interfaces se ubican en `domain/repository/`. Definen el contrato de operaciones disponibles sin revelar ningún detalle de implementación.

#### `PlaceRepository.kt`
```kotlin
interface PlaceRepository {
    val places: StateFlow<List<Place>>
    fun save(place: Place)
    fun findById(id: String): Place?
    fun findByCategory(category: PlaceCategory): List<Place>
    fun update(place: Place)
    fun delete(id: String)
}
```

#### `UserRepository.kt`
```kotlin
interface UserRepository {
    val users: StateFlow<List<User>>
    fun save(user: User)
    fun findById(id: String): User?
    fun login(email: String, password: String): User?
    fun updatePoints(userId: String, points: Int)
    fun addBadge(userId: String, badge: Badge)
}
```

#### `RouteRepository.kt`
```kotlin
interface RouteRepository {
    val routes: StateFlow<List<Route>>
    fun save(route: Route)
    fun findById(id: String): Route?
    fun findByOwner(ownerId: String): List<Route>
    fun delete(id: String)
}
```

---

## 🗄️ **4. Capa de Datos — Implementación de Repositorios**

Las implementaciones concretas se ubican en `data/repository/`. Aquí es donde se define cómo se accede realmente a los datos (en memoria, Firebase, etc.).

### **4.1 Patrón Singleton con Hilt**

Todas las implementaciones deben anotarse con `@Singleton` para garantizar una única instancia en toda la aplicación.

#### `PlaceRepositoryImpl.kt`
```kotlin
@Singleton
class PlaceRepositoryImpl @Inject constructor() : PlaceRepository {

    private val _places = MutableStateFlow<List<Place>>(emptyList())
    override val places: StateFlow<List<Place>> = _places.asStateFlow()

    init {
        _places.value = seedData()
    }

    override fun save(place: Place) {
        _places.value = _places.value + place
    }

    override fun findById(id: String): Place? {
        return _places.value.firstOrNull { it.id == id }
    }

    override fun findByCategory(category: PlaceCategory): List<Place> {
        return _places.value.filter { it.category == category }
    }

    override fun update(place: Place) {
        _places.value = _places.value.map {
            if (it.id == place.id) place else it
        }
    }

    override fun delete(id: String) {
        _places.value = _places.value.filter { it.id != id }
    }

    private fun seedData(): List<Place> {
        return listOf(
            Place(
                id = "1",
                title = "Salento",
                description = "Pueblo cafetero en el corazón del Quindío",
                location = Location(4.6387, -75.5711),
                category = PlaceCategory.NATURE,
                status = PlaceStatus.APPROVED,
                photoUrl = "https://picsum.photos/400?random=1",
                ownerId = "user_1"
            )
            // Agregar más datos de ejemplo...
        )
    }
}
```

> **Buena práctica:** El método `update()` no muta la lista directamente. Usa `map` para crear una nueva lista inmutable. Esto es fundamental para que el `StateFlow` detecte los cambios y notifique a los observadores.

---

## 🎛️ **5. Capa de Presentación — ViewModels**

Los ViewModels se ubican junto a sus pantallas en `features/<modulo>/`. Se anotan con `@HiltViewModel` y reciben dependencias vía `@Inject constructor`.

### **5.1 Reglas para ViewModels**

- Un ViewModel **solo** expone `StateFlow` o `Flow` a la UI, nunca listas mutables directas.
- Toda lógica de negocio va en el ViewModel, no en el Composable.
- El Composable solo observa el estado y llama funciones del ViewModel.
- Nunca accedas a la capa de datos directamente desde la pantalla.

### **5.2 Ejemplo: PlaceListViewModel**

```kotlin
@HiltViewModel
class PlaceListViewModel @Inject constructor(
    private val repository: PlaceRepository
) : ViewModel() {

    // Estado de la lista de lugares
    val places: StateFlow<List<Place>> = repository.places

    // Estado de filtro actual
    private val _selectedCategory = MutableStateFlow<PlaceCategory?>(null)
    val selectedCategory: StateFlow<PlaceCategory?> = _selectedCategory.asStateFlow()

    // Lista filtrada derivada del estado
    val filteredPlaces: StateFlow<List<Place>> = combine(
        repository.places,
        _selectedCategory
    ) { places, category ->
        if (category == null) places
        else places.filter { it.category == category }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun filterByCategory(category: PlaceCategory?) {
        _selectedCategory.value = category
    }
}
```

### **5.3 Ejemplo: CreatePlaceViewModel (con IA)**

```kotlin
@HiltViewModel
class CreatePlaceViewModel @Inject constructor(
    private val repository: PlaceRepository
) : ViewModel() {

    var title by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set

    var suggestedCategory by mutableStateOf<PlaceCategory?>(null)
        private set

    var isLoadingAI by mutableStateOf(false)
        private set

    fun onTitleChange(value: String) { title = value }
    fun onDescriptionChange(value: String) { description = value }

    fun suggestCategory() {
        viewModelScope.launch {
            isLoadingAI = true
            // Llamada a Gemini API para clasificar la categoría
            // La respuesta actualiza suggestedCategory
            isLoadingAI = false
        }
    }

    fun savePlace(location: Location, photoUrl: String) {
        val place = Place(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            location = location,
            category = suggestedCategory ?: PlaceCategory.OTHER,
            status = PlaceStatus.PENDING,
            photoUrl = photoUrl,
            ownerId = "current_user_id"
        )
        repository.save(place)
    }
}
```

---

## 💉 **6. Inyección de Dependencias con Hilt**

### **6.1 Configuración de la Aplicación**

```kotlin
// MyApp.kt
@HiltAndroidApp
class MyApp : Application()
```

En `AndroidManifest.xml`:
```xml
<application
    android:name=".MyApp"
    ... >
```

La actividad principal debe anotarse con `@AndroidEntryPoint`:
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() { ... }
```

### **6.2 Módulo de Repositorios**

El módulo Hilt vincula cada interfaz con su implementación concreta. Sin este archivo, Hilt no sabrá qué clase usar cuando se inyecte una interfaz.

```kotlin
// di/RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPlaceRepository(
        impl: PlaceRepositoryImpl
    ): PlaceRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindRouteRepository(
        impl: RouteRepositoryImpl
    ): RouteRepository
}
```

### **6.3 Uso en Composables**

En lugar de `viewModel()`, se usa `hiltViewModel()` para que Hilt inyecte las dependencias automáticamente:

```kotlin
@Composable
fun PlaceListScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: PlaceListViewModel = hiltViewModel() // ← Hilt se encarga
) {
    val places by viewModel.places.collectAsState()
    // ...
}
```

> **Buena práctica:** Siempre declara el ViewModel como parámetro con valor por defecto (`= hiltViewModel()`). Esto facilita las pruebas, ya que en los tests se puede pasar un ViewModel falso sin depender de Hilt.

---

## 🧭 **7. Navegación con Navigation Compose**

### **7.1 Definición de Rutas**

Todas las rutas se definen en un solo archivo usando `sealed class` con `@Serializable`. Esto garantiza type-safety en la navegación.

```kotlin
// core/navigation/AppRoutes.kt
sealed class AppRoutes {

    // Auth
    @Serializable data object Login : AppRoutes()
    @Serializable data object Register : AppRoutes()

    // Feed
    @Serializable data object Feed : AppRoutes()

    // Places
    @Serializable data object PlaceList : AppRoutes()
    @Serializable data class PlaceDetail(val placeId: String) : AppRoutes()
    @Serializable data object CreatePlace : AppRoutes()

    // Profile
    @Serializable data object Profile : AppRoutes()

    // Moderator
    @Serializable data object Moderator : AppRoutes()

    // Routes
    @Serializable data object RouteList : AppRoutes()
    @Serializable data class RouteDetail(val routeId: String) : AppRoutes()
}
```

### **7.2 Configuración del NavHost**

```kotlin
// core/navigation/AppNavigation.kt
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Surface(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = AppRoutes.Login
        ) {

            composable<AppRoutes.Login> {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate(AppRoutes.Register)
                    },
                    onLoginSuccess = {
                        navController.navigate(AppRoutes.Feed) {
                            popUpTo(AppRoutes.Login) { inclusive = true }
                        }
                    }
                )
            }

            composable<AppRoutes.Register> {
                RegisterScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable<AppRoutes.Feed> {
                FeedScreen(
                    onNavigateToDetail = { placeId ->
                        navController.navigate(AppRoutes.PlaceDetail(placeId))
                    }
                )
            }

            composable<AppRoutes.PlaceDetail> {
                val args = it.toRoute<AppRoutes.PlaceDetail>()
                PlaceDetailScreen(placeId = args.placeId)
            }

            composable<AppRoutes.CreatePlace> {
                CreatePlaceScreen(
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            // Agregar el resto de rutas...
        }
    }
}
```

### **7.3 Reglas de Navegación**

- **Nunca** pasar objetos complejos como parámetros de navegación. Solo tipos primitivos o `String`.
- Usar `popUpTo` con `inclusive = true` al hacer login/logout para limpiar el back stack.
- Toda la lógica de navegación vive en `AppNavigation.kt`, no en las pantallas.
- Las pantallas solo invocan las funciones lambda que reciben como parámetros (`onNavigateTo...`).

### **7.4 Navegación hacia Atrás Personalizada**

Cuando se necesite interceptar el botón de retroceso del dispositivo (por ejemplo, en formularios con datos sin guardar):

```kotlin
@Composable
fun CreatePlaceScreen(onSaveSuccess: () -> Unit) {
    var showExitDialog by remember { mutableStateOf(false) }

    // Intercepta el botón físico de retroceso
    BackHandler(enabled = !showExitDialog) {
        showExitDialog = true
    }

    if (showExitDialog) {
        ConfirmAlertDialog(
            title = "¿Salir sin guardar?",
            text = "Se perderán los datos ingresados.",
            onConfirm = { onSaveSuccess() }, // Navega hacia atrás
            onDismiss = { showExitDialog = false }
        )
    }
    // Resto de la pantalla...
}
```

---

## 🧱 **8. Componentes UI Reutilizables**

Los composables reutilizables se ubican en `core/components/`. La idea es evitar duplicar código en múltiples pantallas.

### **8.1 DropdownMenu Genérico**

```kotlin
// core/components/DropdownMenu.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDropdownMenu(
    value: String,
    label: String,
    list: List<String>,
    onValueChange: (String) -> Unit,
    supportingText: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            enabled = enabled,
            readOnly = true,
            value = value,
            onValueChange = { },
            label = { Text(label) },
            supportingText = supportingText?.let { { Text(it) } },
            leadingIcon = icon?.let { { Icon(it, contentDescription = null) } },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded && enabled) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false }
        ) {
            list.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onValueChange(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
```

### **8.2 AlertDialog Reutilizable**

```kotlin
// core/components/ConfirmAlertDialog.kt
@Composable
fun ConfirmAlertDialog(
    title: String,
    text: String,
    confirmText: String = "Confirmar",
    dismissText: String = "Cancelar",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        title = { Text(title) },
        text = { Text(text) },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onDismiss(); onConfirm() }) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        }
    )
}
```

### **8.3 Imagen de Perfil con Coil**

```kotlin
// core/components/ProfileImage.kt
@Composable
fun ProfileImage(
    url: String,
    size: Dp = 80.dp,
    cornerRadius: Dp = 16.dp
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = "Imagen de perfil",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
    )
}
```

---

## 🔄 **9. Manejo del Estado**

### **9.1 StateFlow vs MutableState**

En TuristGo se usan dos mecanismos de estado según el contexto:

**`StateFlow` en ViewModels** — para datos que provienen del repositorio o que múltiples partes de la UI observan:
```kotlin
// En el ViewModel
private val _places = MutableStateFlow<List<Place>>(emptyList())
val places: StateFlow<List<Place>> = _places.asStateFlow()
```

**`mutableStateOf` en ViewModels** — para campos de formulario o estado local simple:
```kotlin
// En el ViewModel
var searchQuery by mutableStateOf("")
    private set

fun onSearchQueryChange(value: String) { searchQuery = value }
```

**`remember { mutableStateOf(...) }` en Composables** — para estado estrictamente local a la UI (visibilidad de un diálogo, estado de expansión):
```kotlin
// En el Composable
var showDialog by remember { mutableStateOf(false) }
```

### **9.2 Observar StateFlow en Composables**

```kotlin
@Composable
fun PlaceListScreen(viewModel: PlaceListViewModel = hiltViewModel()) {

    // collectAsState convierte StateFlow en State<T> que Compose puede observar
    val places by viewModel.places.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    // La UI se recompone automáticamente cuando el estado cambia
    LazyColumn {
        items(places) { place ->
            PlaceItem(place = place)
        }
    }
}
```

### **9.3 Efectos Secundarios con LaunchedEffect**

Usar `LaunchedEffect` para ejecutar código que depende de un cambio de estado (como navegar tras un login exitoso):

```kotlin
val loginResult by viewModel.loginResult.collectAsState()

LaunchedEffect(loginResult) {
    if (loginResult is RequestResult.Success) {
        viewModel.resetForm()
        onLoginSuccess()
    }
}
```

> **Buena práctica:** `LaunchedEffect` garantiza que el bloque de código solo se ejecute una vez cuando la clave cambia, evitando efectos secundarios no deseados en recomposiciones.

---

## 📝 **10. Formularios y Validación**

### **10.1 Patrón de Campo Validado**

Para manejar formularios de manera consistente, se recomienda encapsular la lógica de validación en una clase auxiliar:

```kotlin
class ValidatedField<T>(
    initialValue: T,
    private val validator: (T) -> String?
) {
    var value by mutableStateOf(initialValue)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun onChange(newValue: T) {
        value = newValue
        error = validator(newValue)
    }

    fun validate(): Boolean {
        error = validator(value)
        return error == null
    }
}
```

### **10.2 Uso en un ViewModel**

```kotlin
class RegisterViewModel : ViewModel() {

    val name = ValidatedField("") { value ->
        when {
            value.isBlank() -> "El nombre es obligatorio"
            value.length < 3 -> "Mínimo 3 caracteres"
            else -> null
        }
    }

    val email = ValidatedField("") { value ->
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches())
            "Correo electrónico inválido"
        else null
    }

    val password = ValidatedField("") { value ->
        if (value.length < 6) "Mínimo 6 caracteres" else null
    }

    val isFormValid: Boolean
        get() = name.error == null && email.error == null && password.error == null
                && name.value.isNotBlank() && email.value.isNotBlank() && password.value.isNotBlank()

    fun resetForm() {
        name.onChange("")
        email.onChange("")
        password.onChange("")
    }
}
```

### **10.3 Uso en la Pantalla**

```kotlin
OutlinedTextField(
    value = viewModel.email.value,
    onValueChange = { viewModel.email.onChange(it) },
    label = { Text("Correo electrónico") },
    isError = viewModel.email.error != null,
    supportingText = viewModel.email.error?.let { { Text(it) } },
    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
)
```

---

## 📋 **11. Listas y Elementos Múltiples**

### **11.1 LazyColumn para Listas Verticales**

```kotlin
LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    items(
        items = places,
        key = { it.id } // Clave única para optimizar recomposiciones
    ) { place ->
        PlaceCard(
            place = place,
            onClick = { onNavigateToDetail(place.id) }
        )
    }
}
```

> **Buena práctica:** Siempre usar el parámetro `key` en `items()`. Esto permite a Compose identificar qué elementos cambiaron y evitar recomposiciones innecesarias.

### **11.2 ListItem para Elementos Estándar**

```kotlin
@Composable
fun PlaceCard(place: Place, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
        headlineContent = { Text(place.title) },
        supportingContent = { Text(place.description, maxLines = 2) },
        leadingContent = {
            ProfileImage(url = place.photoUrl, size = 64.dp)
        },
        trailingContent = {
            AssistChip(
                onClick = {},
                label = { Text(place.category.name) }
            )
        }
    )
}
```

### **11.3 LazyVerticalGrid para Cuadrículas**

```kotlin
LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    contentPadding = PaddingValues(16.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    items(badges) { badge ->
        BadgeCard(badge = badge)
    }
}
```

---

## 🖼️ **12. Imágenes con Coil**

### **12.1 Dependencias en `libs.versions.toml`**

```toml
[versions]
coil = "3.3.0"

[libraries]
coil-compose = { module = "io.coil-kt.coil3:coil-compose", version.ref = "coil" }
coil-network-okhttp = { module = "io.coil-kt.coil3:coil-network-okhttp", version.ref = "coil" }
```

En `build.gradle.kts`:
```kotlin
implementation(libs.coil.compose)
implementation(libs.coil.network.okhttp)
```

En `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### **12.2 AsyncImage con Placeholder y Error**

```kotlin
AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
        .data(url)
        .crossfade(true)
        .placeholder(R.drawable.placeholder_place)
        .error(R.drawable.error_image)
        .build(),
    contentDescription = "Foto del lugar",
    contentScale = ContentScale.Crop,
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .clip(RoundedCornerShape(12.dp))
)
```

---

## 🎨 **13. Material You y Temas**

### **13.1 Estructura del Tema**

```text
ui/theme/
├── Color.kt       # Paleta de colores
├── Theme.kt       # Definición del tema claro/oscuro
└── Type.kt        # Escala tipográfica
```

### **13.2 Aplicación del Tema**

```kotlin
// En MainActivity
setContent {
    TuristGoTheme {
        AppNavigation()
    }
}
```

### **13.3 Uso de Colores del Tema en Composables**

Nunca usar colores fijos (`Color.Red`). Siempre referenciar el tema actual:

```kotlin
// ✅ Correcto
Text(
    text = place.title,
    color = MaterialTheme.colorScheme.onSurface
)

Card(
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
)

// ❌ Incorrecto
Text(text = place.title, color = Color(0xFF333333))
```

---

## 📦 **14. Configuración de Dependencias**

### **14.1 `libs.versions.toml` Completo**

```toml
[versions]
kotlin = "2.2.21"
navigationCompose = "2.9.6"
kotlinxSerialization = "1.9.0"
hiltAndroid = "2.57.2"
hiltNavigationCompose = "1.3.0"
ksp = "2.1.21-2.0.1"
coil = "3.3.0"
materialIconsExtended = "1.7.8"

[libraries]
# Navegación
androidx-navigation-compose = { module = "androidx.navigation:navigation-compose", version.ref = "navigationCompose" }
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinxSerialization" }

# Hilt
hilt-android = { module = "com.google.dagger:hilt-android", version.ref = "hiltAndroid" }
hilt-compiler = { module = "com.google.dagger:hilt-compiler", version.ref = "hiltAndroid" }
androidx-hilt-navigation-compose = { module = "androidx.hilt:hilt-navigation-compose", version.ref = "hiltNavigationCompose" }

# Coil
coil-compose = { module = "io.coil-kt.coil3:coil-compose", version.ref = "coil" }
coil-network-okhttp = { module = "io.coil-kt.coil3:coil-network-okhttp", version.ref = "coil" }

# Iconos
material-icons-extended = { module = "androidx.compose.material:material-icons-extended", version.ref = "materialIconsExtended" }

[plugins]
kotlinx-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
hilt-android = { id = "com.google.dagger.hilt.android", version.ref = "hiltAndroid" }
devtools-ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

### **14.2 `build.gradle.kts` del Módulo App**

```kotlin
plugins {
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Navegación
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Iconos extendidos
    implementation(libs.material.icons.extended)
}
```

---

## ✅ **15. Convenciones de Código**

### **15.1 Nomenclatura**

| Elemento | Convención | Ejemplo |
|---|---|---|
| Composables | PascalCase | `PlaceListScreen`, `PlaceCard` |
| ViewModels | PascalCase + ViewModel | `PlaceListViewModel` |
| Repositorios (interfaz) | PascalCase + Repository | `PlaceRepository` |
| Repositorios (impl) | PascalCase + RepositoryImpl | `PlaceRepositoryImpl` |
| Funciones | camelCase | `onTitleChange`, `filterByCategory` |
| StateFlow privados | `_camelCase` | `_places`, `_selectedCategory` |
| StateFlow públicos | camelCase | `places`, `selectedCategory` |
| Archivos de rutas | PascalCase | `AppRoutes.kt` |
| Paquetes | lowercase | `features.place.list` |

### **15.2 Orden en Composables**

Seguir siempre este orden dentro de un Composable:

```kotlin
@Composable
fun PlaceListScreen(
    onNavigateToDetail: (String) -> Unit,           // 1. Parámetros de navegación
    viewModel: PlaceListViewModel = hiltViewModel() // 2. ViewModel al final con default
) {
    // 3. Observar estado
    val places by viewModel.places.collectAsState()

    // 4. Estado local de UI
    var showFilter by remember { mutableStateOf(false) }

    // 5. Efectos secundarios
    LaunchedEffect(Unit) { /* ... */ }

    // 6. UI
    Column {
        // ...
    }
}
```

### **15.3 Checklist antes de hacer commit**

- [ ] El ViewModel no tiene referencias a Context ni a la View.
- [ ] Todos los campos de formulario pasan por `ValidatedField`.
- [ ] No hay lógica de negocio dentro de Composables.
- [ ] Los colores y tipografía usan `MaterialTheme`, no valores fijos.
- [ ] Los `LazyColumn` / `LazyRow` tienen el parámetro `key` definido.
- [ ] Los repositorios se acceden solo desde ViewModels, nunca desde Composables directamente.
- [ ] Hilt está configurado en todos los ViewModels que usan repositorios (`@HiltViewModel`).
- [ ] Las rutas de navegación están todas definidas en `AppRoutes.kt`.
- [ ] La navegación se gestiona únicamente desde `AppNavigation.kt`.

---

<div align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&height=90&section=footer&animation=fadeIn" />
  <p>📖 Documento de arquitectura · TuristGo · Universidad del Quindío · 2026</p>
</div>
