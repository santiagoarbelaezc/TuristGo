package com.turistgo.app.data.repository

import com.turistgo.app.domain.model.Post
import com.turistgo.app.domain.model.User
import com.turistgo.app.domain.model.Comment
import com.turistgo.app.domain.model.Notification
import com.turistgo.app.domain.repository.AppDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryRepository @Inject constructor() : AppDataRepository {
    private val users = MutableStateFlow<List<User>>(listOf(
        User(
            id = "santi_001",
            name = "Santiago",
            lastName = "Arbelaez",
            age = "25",
            country = "Colombia",
            city = "Armenia",
            department = "Quindío",
            phone = "3054078225",
            email = "santiago@turistgo.com",
            username = "santiarco",
            password = "santi123",
            role = "ADMIN",
            isVerified = true
        ),
        User(
            id = "juanda_001",
            name = "Juan",
            lastName = "David",
            age = "22",
            country = "Colombia",
            city = "Medellín",
            phone = "3000000001",
            email = "juanda@turistgo.com",
            username = "juanda",
            password = "juanda123",
            profilePhotoUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776632171/WhatsApp_Image_2026-04-19_at_3.55.25_PM_1_l9dbve.jpg",
            isVerified = true
        ),
        User(
            id = "eli_001",
            name = "Eliana",
            lastName = "Lopez",
            age = "24",
            country = "Colombia",
            city = "Bogotá",
            phone = "3000000002",
            email = "eli@turistgo.com",
            username = "eli",
            password = "eli123",
            profilePhotoUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776632171/WhatsApp_Image_2026-04-19_at_3.55.25_PM_xn4jqm.jpg"
        )
    ))
    private val comments = MutableStateFlow<List<Comment>>(emptyList())
    private val notifications = MutableStateFlow<List<Notification>>(emptyList())
    private val posts = MutableStateFlow<List<Post>>(listOf(
        Post(
            id = "1", 
            name = "Santuario de Las Lajas", 
            location = "Ipiales, Nariño", 
            rating = "4.9", 
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/iglesia_s29dbh.jpg", 
            description = "Un santuario impresionante construido en el cañón del río Guáitara. Es una joya de la arquitectura gótica.", 
            schedule = "6:00 AM - 9:00 PM", 
            priceRange = "Entrada libre",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares", "Cultura", "Turismo")
        ),
        Post(
            id = "2", 
            name = "San Andrés Islas", 
            location = "San Andrés, Colombia", 
            rating = "4.8", 
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/playa_qg2ifb.jpg", 
            description = "Playa, brisa y mar en el mar de los siete colores. Perfecto para buceo y relajación.", 
            schedule = "Siempre abierto", 
            priceRange = "Variable (Vuelos + Hospedaje)",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares", "Naturaleza", "Playa")
        ),
        Post(
            id = "3", 
            name = "Piedra del Peñol", 
            location = "Guatapé, Antioquia", 
            rating = "4.7", 
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/pe%C3%B1ol_jlujxo.jpg", 
            description = "Un monolito de 220 metros de altura con una de las mejores vistas del mundo desde su cima.", 
            schedule = "8:00 AM - 6:00 PM", 
            priceRange = "$25.000 COP (Subida)",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares", "Naturaleza")
        ),
        Post(
            id = "4", 
            name = "Nevado del Ruiz", 
            location = "Manizales, Caldas", 
            rating = "4.6", 
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/nevadoruiz_rc301x.jpg", 
            description = "Un volcán cubierto por glaciares. Ofrece paisajes únicos de páramo y nieve.", 
            schedule = "7:00 AM - 2:00 PM", 
            priceRange = "Moderado ($50.000+ COP)",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares", "Naturaleza")
        ),
        // --- NEW CONTENT ---
        Post(
            id = "5",
            name = "FERXXOCALIPSIS - Feid",
            location = "Medellín, Antioquia",
            rating = "5.0",
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776565122/concierto-feid_r1a6bb.jpg",
            description = "El concierto más esperado del año. Ven a disfrutar de todo el perreo de Feid en su ciudad natal.",
            schedule = "8:00 PM",
            priceRange = "Desde $150.000 COP",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Eventos", "Conciertos")
        ),
        Post(
            id = "6",
            name = "Papi Juancho Tour - Maluma",
            location = "Medellín, Antioquia",
            rating = "4.9",
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776565122/concierto-maluma_vkwbfq.jpg",
            description = "Maluma Baby vuelve a casa. Un espectáculo de clase mundial con todos sus éxitos.",
            schedule = "9:00 PM",
            priceRange = "Desde $120.000 COP",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Eventos", "Conciertos")
        ),
        Post(
            id = "7",
            name = "Jardín, El Pueblo más Lindo",
            location = "Jardín, Antioquia",
            rating = "4.8",
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776565121/jardin-antioquia_tup3m2.jpg",
            description = "Colores, café y mucha tradición. Disfruta de la arquitectura colonial más conservada de Antioquia.",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares", "Cultura", "Montaña")
        ),
        Post(
            id = "8",
            name = "Dulcinea GastroBar",
            location = "Medellín, Antioquia",
            rating = "4.7",
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776565120/discoteca-dulcinea-medellin_tnzany.png",
            description = "La mejor vida nocturna y gastronomía en el corazón de Provenza. Experiencias únicas cada noche.",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares", "Gastronomía")
        ),
        Post(
            id = "9",
            name = "Santa Fe de Antioquia",
            location = "Santa Fe de Antioquia, ANT",
            rating = "4.7",
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776565119/santa-fe-de-antioquia_fin516.jpg",
            description = "La ciudad madre. Recorre sus calles de piedra y cruza el majestuoso Puente de Occidente.",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares", "Cultura")
        ),
        Post(
            id = "10",
            name = "Graffitour Comuna 13",
            location = "Medellín, Antioquia",
            rating = "4.9",
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776565119/comuna13_znro30.jpg",
            description = "Resiliencia y arte urbano. Una historia de transformación contada a través de graffitis y baile.",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares", "Cultura")
        ),
        Post(
            id = "11",
            name = "Réplica del Peñol",
            location = "Guatapé, Antioquia",
            rating = "4.6",
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776565118/guatape_fqirly.jpg",
            description = "Un viaje al pasado sobre las aguas del embalse. Historia y paisajes inolvidables.",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares", "Naturaleza")
        ),
        Post(
            id = "12",
            name = "Centro Histórico Cartagena",
            location = "Cartagena, Bolívar",
            rating = "5.0",
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776565118/cartagena-de-indias_xpuz0f.jpg",
            description = "La ciudad amurallada. Un laberinto de balcones florecidos e historia colonial junto al mar.",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares", "Cultura", "Playa")
        ),
        Post(
            id = "13",
            name = "Parque Natural Gorgona",
            location = "Isla Gorgona, Cauca",
            rating = "4.8",
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776565118/isla-gorgona_ihfdeq.jpg",
            description = "Ciencia y naturaleza. La isla prisión que se convirtió en refugio para ballenas y selva virgen.",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares", "Naturaleza")
        ),
        Post(
            id = "14",
            name = "Estadio Atanasio Girardot",
            location = "Medellín, Antioquia",
            rating = "4.7",
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776565118/atanasio-girardot_yf1m15.jpg",
            description = "El epicentro del fútbol antioqueño. Vive la pasión de un clásico con las mejores hinchadas.",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Eventos", "Deportes")
        ),
        Post(
            id = "15",
            name = "Filandia 'La Colina Iluminada'",
            location = "Filandia, Quindío",
            rating = "4.8",
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776565118/filandia-quindio_xoexbk.jpg",
            description = "Artesanías y balcones. Un remanso de paz en el Eje Cafetero con vistas que quitan el aliento.",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares", "Montaña", "Cultura")
        ),
        Post(
            id = "16",
            name = "Islas del Rosario",
            location = "Islas del Rosario, Bolívar",
            rating = "4.9",
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776565118/isla-del-rosario_kyr3vr.jpg",
            description = "Paraíso cristalino. Aguas turquesas y arrecifes de coral perfectos para el careteo.",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares", "Naturaleza", "Playa")
        ),
        Post(
            id = "17",
            name = "Reserva Natural Río Claro",
            location = "Río Claro, Antioquia",
            rating = "4.7",
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776565117/rioclaro-antioquia_vmmqo6.jpg",
            description = "Aventura en el mármol. Rafting, canopy y espeleología en uno de los ríos más hermosos.",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares", "Naturaleza", "Aventura")
        ),
        Post(
            id = "18",
            name = "Hotel Sofitel Legend",
            location = "Cartagena, Bolívar",
            rating = "5.0",
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776565117/hotel-sofitel-legend_alimox.jpg",
            description = "Lujo en el Santa Clara. Un antiguo convento transformado en el hotel más exclusivo de la ciudad.",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares", "Gastronomía")
        ),
        Post(
            id = "19",
            name = "San Fernando Plaza",
            location = "Medellín, Antioquia",
            rating = "4.6",
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776565117/san-fernando-plaza-medellin_dykccx.jpg",
            description = "El corazón de los negocios. Arquitectura moderna y gastronomía de alto nivel en la Milla de Oro.",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares")
        ),
        Post(
            id = "20",
            name = "Bastión Luxury Hotel",
            location = "Cartagena, Bolívar",
            rating = "4.9",
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776565116/bastion-luxury-hotel_jzbxjr.jpg",
            description = "Elegancia republicana. Disfruta de la mejor terraza con vista al centro histórico.",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares")
        ),
        Post(
            id = "21",
            name = "JW Marriott Bogotá",
            location = "Bogotá, Colombia",
            rating = "4.8",
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776565116/jw-marriot-bogota-hotel_zici8k.jpg",
            description = "Confort y sofisticación. La mejor opción para tu estadía en la capital colombiana.",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares")
        ),
        Post(
            id = "22",
            name = "Parque del Café",
            location = "Montenegro, Quindío",
            rating = "4.9",
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776565116/parquedelcafe_wvflxe.jpg",
            description = "Diversión con aroma a café. El parque temático más importante del Eje Cafetero.",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares", "Gastronomía")
        ),
        Post(
            id = "23",
            name = "Parque Arví",
            location = "Santa Elena, Antioquia",
            rating = "4.7",
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776565116/parque-arvi-medellin_ybt6wk.jpg",
            description = "El pulmón verde de Medellín. Bosque de niebla y senderos prehispánicos a un teleférico de distancia.",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares", "Naturaleza", "Montaña")
        ),
        Post(
            id = "24",
            name = "Hotel Irotama Resort",
            location = "Santa Marta, Magdalena",
            rating = "4.8",
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776565116/hotel-irotama-santa-marta_t0pqas.jpg",
            description = "Tradición frente al mar Caribe. Un lugar mágico para tus vacaciones familiares.",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares", "Playa")
        ),
        Post(
            id = "25",
            name = "PANACA",
            location = "Quimbaya, Quindío",
            rating = "4.8",
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776565115/panaca-quindio_bm4agw.jpg",
            description = "¡Sin campo no hay ciudad! El primer parque temático agropecuario del mundo.",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares", "Naturaleza")
        ),
        Post(
            id = "26",
            name = "Hacienda Bambusa",
            location = "Quindío, Colombia",
            rating = "4.9",
            imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776565115/hacienda-bambusa-quindio_ll6vo9.jpg",
            description = "Exclusividad entre cafetales. Una oasis de paz rodeado de naturaleza exuberante.",
            status = com.turistgo.app.domain.model.PostStatus.APPROVED,
            categories = listOf("Lugares", "Naturaleza")
        )
    ))

    override fun getUsers(): Flow<List<User>> = users

    override suspend fun saveUser(user: User) {
        val exists = users.value.any { it.id == user.id }
        if (exists) {
            updateUser(user)
        } else {
            users.value = users.value + user
        }
    }

    override suspend fun updateUser(user: User) {
        users.value = users.value.map { if (it.id == user.id) user else it }
    }

    override suspend fun getUserByEmail(email: String): User? = users.value.find { it.email == email }
    override suspend fun getUserByUsername(username: String): User? = users.value.find { it.username == username }
    override suspend fun getUserById(userId: String): User? = users.value.find { it.id == userId }
    override suspend fun deleteUser(userId: String) {
        users.value = users.value.filter { it.id != userId }
    }

    override fun getPosts(status: com.turistgo.app.domain.model.PostStatus?): Flow<List<Post>> {
        return if (status == null) posts
        else posts.map { list -> list.filter { it.status == status } }
    }

    override fun getPostsByAuthor(authorId: String): Flow<List<Post>> {
        return posts.map { list -> list.filter { it.authorId == authorId } }
    }

    override suspend fun savePost(post: Post) {
        posts.value = posts.value + post
    }

    override suspend fun updatePostStatus(postId: String, status: com.turistgo.app.domain.model.PostStatus) {
        posts.value = posts.value.map { if (it.id == postId) it.copy(status = status) else it }
    }

    override suspend fun getPostById(postId: String): Post? = posts.value.find { it.id == postId }

    override suspend fun toggleSavedPost(userId: String, postId: String) {
        val userExists = users.value.any { it.id == userId }
        if (!userExists) {
            // Auto-register mock user for persistent sessions
            val newUser = User(
                id = userId,
                name = "Usuario",
                lastName = "App",
                age = "25",
                country = "Colombia",
                city = "Bogotá",
                phone = "0000000000",
                email = "user@turistgo.com"
            )
            users.value = users.value + newUser
        }
        
        users.value = users.value.map { user ->
            if (user.id == userId) {
                val newSavedIds = if (user.savedPostIds.contains(postId)) {
                    user.savedPostIds - postId
                } else {
                    user.savedPostIds + postId
                }
                user.copy(savedPostIds = newSavedIds)
            } else user
        }
    }

    override fun getSavedPosts(userId: String): Flow<List<Post>> {
        return combine(users, posts) { currentUsers, currentPosts ->
            val user = currentUsers.find { it.id == userId }
            val savedIds = user?.savedPostIds ?: emptyList()
            currentPosts.filter { it.id in savedIds && it.status == com.turistgo.app.domain.model.PostStatus.APPROVED }
        }
    }

    override suspend fun toggleLikedPost(userId: String, postId: String) {
        val userExists = users.value.any { it.id == userId }
        if (!userExists) {
            val newUser = User(
                id = userId,
                name = "Usuario",
                lastName = "App",
                age = "25",
                country = "Colombia",
                city = "Bogotá",
                phone = "0000000000",
                email = "user@turistgo.com"
            )
            users.value = users.value + newUser
        }
        
        users.value = users.value.map { user ->
            if (user.id == userId) {
                val newLikedIds = if (user.likedPostIds.contains(postId)) {
                    user.likedPostIds - postId
                } else {
                    user.likedPostIds + postId
                }
                user.copy(likedPostIds = newLikedIds)
            } else user
        }
    }

    override fun getLikedPosts(userId: String): Flow<List<Post>> {
        return combine(users, posts) { currentUsers, currentPosts ->
            val user = currentUsers.find { it.id == userId }
            val likedIds = user?.likedPostIds ?: emptyList()
            currentPosts.filter { it.id in likedIds && it.status == com.turistgo.app.domain.model.PostStatus.APPROVED }
        }
    }

    // Comments implementation
    override fun getComments(postId: String): Flow<List<Comment>> {
        return comments.map { list -> list.filter { it.postId == postId } }
    }
    
    override suspend fun addComment(comment: Comment) {
        comments.value = comments.value + comment
        // Increment comment count on the post
        posts.value = posts.value.map { 
            if (it.id == comment.postId) it.copy(commentCount = it.commentCount + 1) else it 
        }
    }

    // Notifications implementation
    override fun getNotifications(userId: String): Flow<List<Notification>> {
        return notifications.map { allNotifications ->
            allNotifications.filter { it.userId == userId }.sortedByDescending { it.timestamp }
        }
    }

    override suspend fun addNotification(notification: Notification) {
        notifications.value = notifications.value + notification
    }

    override suspend fun markNotificationAsRead(notificationId: String) {
        notifications.value = notifications.value.map {
            if (it.id == notificationId) it.copy(isRead = true) else it
        }
    }
}
