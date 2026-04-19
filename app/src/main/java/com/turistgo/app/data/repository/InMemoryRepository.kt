package com.turistgo.app.data.repository

import com.turistgo.app.domain.model.Post
import com.turistgo.app.domain.model.User
import com.turistgo.app.domain.repository.AppDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryRepository @Inject constructor() : AppDataRepository {
    private val users = MutableStateFlow<List<User>>(emptyList())
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
            categories = listOf("Turismo", "Cultura")
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
            categories = listOf("Turismo", "Naturaleza")
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
            categories = listOf("Turismo", "Naturaleza")
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
            categories = listOf("Turismo", "Naturaleza")
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
}
