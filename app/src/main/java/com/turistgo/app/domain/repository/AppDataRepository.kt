package com.turistgo.app.domain.repository

import com.turistgo.app.domain.model.Post
import com.turistgo.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AppDataRepository {
    // User related
    fun getUsers(): Flow<List<User>>
    suspend fun saveUser(user: User)
    suspend fun updateUser(user: User)
    suspend fun getUserByEmail(email: String): User?
    suspend fun getUserById(userId: String): User?

    // Post related
    fun getPosts(status: com.turistgo.app.domain.model.PostStatus? = null): Flow<List<Post>>
    fun getPostsByAuthor(authorId: String): Flow<List<Post>>
    suspend fun savePost(post: Post)
    suspend fun updatePostStatus(postId: String, status: com.turistgo.app.domain.model.PostStatus)
    suspend fun getPostById(postId: String): Post?
}
