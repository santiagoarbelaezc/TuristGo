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
        Post("1", "Santuario de Las Lajas", "Ipiales, Nariño", "4.9", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/iglesia_s29dbh.jpg", "Un santuario impresionante."),
        Post("2", "San Andrés Islas", "San Andrés, Colombia", "4.8", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/playa_qg2ifb.jpg", "Playa y sol."),
        Post("3", "Piedra del Peñol", "Guatapé, Antioquia", "4.7", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/pe%C3%B1ol_jlujxo.jpg", "La mejor vista."),
        Post("4", "Nevado del Ruiz", "Manizales, Caldas", "4.6", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/nevadoruiz_rc301x.jpg", "Nieve y frailejones.")
    ))

    override fun getUsers(): Flow<List<User>> = users

    override suspend fun saveUser(user: User) {
        users.value = users.value + user
    }

    override suspend fun updateUser(user: User) {
        users.value = users.value.map { if (it.id == user.id) user else it }
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.value.find { it.email == email }
    }

    override suspend fun getUserById(userId: String): User? {
        return users.value.find { it.id == userId }
    }

    override fun getPosts(): Flow<List<Post>> = posts

    override suspend fun savePost(post: Post) {
        posts.value = posts.value + post
    }

    override suspend fun getPostById(postId: String): Post? {
        return posts.value.find { it.id == postId }
    }
}
