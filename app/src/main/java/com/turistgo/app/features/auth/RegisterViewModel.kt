package com.turistgo.app.features.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turistgo.app.domain.repository.AppDataRepository
import com.turistgo.app.domain.model.User
import com.turistgo.app.data.datastore.UserSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AppDataRepository,
    private val sessionManager: UserSessionManager
) : ViewModel() {
    private val _name = mutableStateOf("")
    val name: State<String> = _name

    private val _lastName = mutableStateOf("")
    val lastName: State<String> = _lastName

    private val _age = mutableStateOf("")
    val age: State<String> = _age

    private val _country = mutableStateOf("")
    val country: State<String> = _country
    
    private val _department = mutableStateOf("")
    val department: State<String> = _department
    
    private val _address = mutableStateOf("")
    val address: State<String> = _address

    private val _city = mutableStateOf("")
    val city: State<String> = _city
    
    private val _phoneExtension = mutableStateOf("+57")
    val phoneExtension: State<String> = _phoneExtension
    
    private val _phone = mutableStateOf("")
    val phone: State<String> = _phone
    
    // Lista restringida de países
    val countries = listOf("Colombia", "Argentina", "Brasil")
    
    // Datos de geografía para Colombia (Departamentos y sus ciudades principales)
    private val colombiaGeography = mapOf(
        "Amazonas" to listOf("Leticia", "Puerto Nariño"),
        "Antioquia" to listOf("Medellín", "Envigado", "Itagüí", "Bello", "Rionegro", "Sabaneta", "Apartadó", "Turbo"),
        "Arauca" to listOf("Arauca", "Tame", "Saravena"),
        "Atlántico" to listOf("Barranquilla", "Soledad", "Malambo", "Puerto Colombia"),
        "Bolívar" to listOf("Cartagena", "Magangué", "Turbaco"),
        "Boyacá" to listOf("Tunja", "Duitama", "Sogamoso", "Chiquinquirá"),
        "Caldas" to listOf("Manizales", "La Dorada", "Chinchiná"),
        "Caquetá" to listOf("Florencia", "San Vicente del Caguán"),
        "Casanare" to listOf("Yopal", "Aguazul", "Villanueva"),
        "Cauca" to listOf("Popayán", "Santander de Quilichao", "Puerto Tejada"),
        "Cesar" to listOf("Valledupar", "Aguachica", "Codazzi"),
        "Chocó" to listOf("Quibdó", "Istmina", "Condoto"),
        "Córdoba" to listOf("Montería", "Cereté", "Lorica"),
        "Cundinamarca" to listOf("Bogotá", "Soacha", "Fusagasugá", "Facatativá", "Chía", "Zipaquirá", "Girardot"),
        "Guainía" to listOf("Inírida"),
        "Guaviare" to listOf("San José del Guaviare"),
        "Huila" to listOf("Neiva", "Pitalito", "Garzón"),
        "La Guajira" to listOf("Riohacha", "Maicao", "Uribia"),
        "Magdalena" to listOf("Santa Marta", "Ciénaga", "Fundación"),
        "Meta" to listOf("Villavicencio", "Acacías", "Granada"),
        "Nariño" to listOf("Pasto", "Ipiales", "Tumaco"),
        "Norte de Santander" to listOf("Cúcuta", "Ocaña", "Villa del Rosario", "Pamplona"),
        "Putumayo" to listOf("Mocoa", "Puerto Asís", "Orito"),
        "Quindío" to listOf("Armenia", "Calarcá", "Montenegro", "Quimbaya", "Salento"),
        "Risaralda" to listOf("Pereira", "Dosquebradas", "Santa Rosa de Cabal"),
        "San Andrés" to listOf("San Andrés", "Providencia"),
        "Santander" to listOf("Bucaramanga", "Floridablanca", "Girón", "Barrancabermeja", "Piedecuesta", "San Gil"),
        "Sucre" to listOf("Sincelejo", "Corozal", "San Marcos"),
        "Tolima" to listOf("Ibagué", "Espinal", "Melgar", "Mariquita"),
        "Valle del Cauca" to listOf("Cali", "Buenaventura", "Palmira", "Tuluá", "Buga", "Cartago", "Jamundí"),
        "Vaupés" to listOf("Mitú"),
        "Vichada" to listOf("Puerto Carreño")
    )

    private val argentinaCities = listOf("Buenos Aires", "Córdoba", "Rosario", "Mendoza", "La Plata", "Mar del Plata", "San Miguel de Tucumán", "Salta", "Santa Fe", "Corrientes")
    private val brasilCities = listOf("São Paulo", "Rio de Janeiro", "Brasília", "Salvador", "Fortaleza", "Belo Horizonte", "Manaus", "Curitiba", "Recife", "Porto Alegre")

    private val _availableDepartments = mutableStateOf<List<String>>(emptyList())
    val availableDepartments: State<List<String>> = _availableDepartments

    private val _availableCities = mutableStateOf<List<String>>(emptyList())
    val availableCities: State<List<String>> = _availableCities

    val phoneExtensions = listOf("+57", "+52", "+54", "+34", "+56", "+1", "+33", "+49")

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _confirmPassword = mutableStateOf("")
    val confirmPassword: State<String> = _confirmPassword

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun onNameChange(v: String)            { _name.value = v }
    fun onLastNameChange(v: String)        { _lastName.value = v }
    fun onAgeChange(v: String)             { _age.value = v }
    
    fun onCountryChange(v: String) { 
        _country.value = v
        _department.value = "" 
        _city.value = ""
        
        when (v) {
            "Colombia" -> {
                _availableDepartments.value = colombiaGeography.keys.sorted()
                _availableCities.value = emptyList()
            }
            "Argentina" -> {
                _availableDepartments.value = emptyList()
                _availableCities.value = argentinaCities.sorted()
            }
            "Brasil" -> {
                _availableDepartments.value = emptyList()
                _availableCities.value = brasilCities.sorted()
            }
            else -> {
                _availableDepartments.value = emptyList()
                _availableCities.value = emptyList()
            }
        }
    }

    fun onDepartmentChange(v: String) {
        _department.value = v
        _city.value = ""
        if (_country.value == "Colombia") {
            _availableCities.value = colombiaGeography[v]?.sorted() ?: emptyList()
        }
    }
    
    fun onCityChange(v: String)            { _city.value = v }
    fun onAddressChange(v: String)         { _address.value = v }
    fun onPhoneExtensionChange(v: String)  { _phoneExtension.value = v }
    fun onPhoneChange(v: String)           { _phone.value = v }
    fun onEmailChange(v: String)           { _email.value = v }
    fun onPasswordChange(v: String)        { _password.value = v }
    fun onConfirmPasswordChange(v: String) { _confirmPassword.value = v }

    fun register(onSuccess: (String) -> Unit) {
        val fields = listOf(_name, _lastName, _age, _country, _city, _phone, _email, _password, _confirmPassword)
        if (fields.any { it.value.isEmpty() }) {
            _snackbarMessage.value = "Todos los campos obligatorios deben estar llenos"
            return
        }
        if (_country.value == "Colombia" && _department.value.isEmpty()) {
            _snackbarMessage.value = "Selecciona un departamento"
            return
        }
        if (_password.value != _confirmPassword.value) {
            _snackbarMessage.value = "Las contraseñas no coinciden"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            
            val userId = UUID.randomUUID().toString()
            val newUser = User(
                id = userId,
                name = _name.value,
                lastName = _lastName.value,
                age = _age.value,
                country = _country.value,
                department = _department.value.takeIf { it.isNotEmpty() },
                city = _city.value,
                address = _address.value.takeIf { it.isNotEmpty() },
                phone = "${_phoneExtension.value} ${_phone.value}",
                email = _email.value,
                password = _password.value
            )
            
            repository.saveUser(newUser)
            sessionManager.saveSession(userId, newUser.name, newUser.email)

            kotlinx.coroutines.delay(3000)
            _snackbarMessage.value = "¡Bienvenido ${_name.value}! Registro casi completo"
            onSuccess(userId)

            _isLoading.value = false
        }
    }

    fun registerWithSocial(provider: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _snackbarMessage.value = "Regístrate con $provider..."
            
            val userId = "social_${System.currentTimeMillis()}"
            val userName = "$provider User"
            val userEmail = "${provider.lowercase()}@example.com"
            
            // Crear usuario social (con campos opcionales nulos por defecto)
            val newUser = User(
                id = userId,
                name = userName,
                lastName = "Social",
                age = "0",
                country = "",
                city = "",
                phone = "",
                email = userEmail
            )
            repository.saveUser(newUser)
            
            // Guardar sesión ficticia
            sessionManager.saveSession(userId, userName, userEmail)
            
            // Simular latencia
            kotlinx.coroutines.delay(2000)
            
            _snackbarMessage.value = "¡Bienvenido via $provider!"
            onSuccess(userId)
            
            _isLoading.value = false
        }
    }

    fun clearSnackbarMessage() { _snackbarMessage.value = null }
}

