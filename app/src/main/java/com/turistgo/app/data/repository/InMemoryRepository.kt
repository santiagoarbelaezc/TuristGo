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
        Post("1", "Santuario de Las Lajas", "Ipiales, Nariño", "4.9", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/iglesia_s29dbh.jpg", 
            "Un santuario impresionante construido en el cañón del río Guáitara. Es una joya de la arquitectura gótica.", 
            "6:00 AM - 9:00 PM", 
            "Entrada libre",
            com.turistgo.app.domain.model.PostStatus.APPROVED
        ),
        Post("2", "San Andrés Islas", "San Andrés, Colombia", "4.8", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/playa_qg2ifb.jpg", 
            "Playa, brisa y mar en el mar de los siete colores. Perfecto para buceo y relajación.", 
            "Siempre abierto", 
            "Variable (Vuelos + Hospedaje)",
            com.turistgo.app.domain.model.PostStatus.APPROVED
        ),
        Post("3", "Piedra del Peñol", "Guatapé, Antioquia", "4.7", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/pe%C3%B1ol_jlujxo.jpg", 
            "Un monolito de 220 metros de altura con una de las mejores vistas del mundo desde su cima.", 
            "8:00 AM - 6:00 PM", 
            "$25.000 COP (Subida)",
            com.turistgo.app.domain.model.PostStatus.APPROVED
        ),
        Post("4", "Nevado del Ruiz", "Manizales, Caldas", "4.6", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/nevadoruiz_rc301x.jpg", 
            "Un volcán cubierto por glaciares. Ofrece paisajes únicos de páramo y nieve.", 
            "7:00 AM - 2:00 PM", 
            "Moderado ($50.000+ COP)",
            com.turistgo.app.domain.model.PostStatus.APPROVED
        )
    ))

    override fun getUsers(): Flow<List<User>> = users

    override suspend fun saveUser(user: User) {
        users.value = users.value + user
    }

    override suspend fun updateUser(user: User) {
        users.value = users.value.map { if (it.id == user.id) user else it }
    }

    override suspend fun getUserByEmail(email: String): User? = users.value.find { it.email == email }
    override suspend fun getUserById(userId: String): User? = users.value.find { it.id == userId }

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
