package com.turistgo.app.domain.repository

import com.turistgo.app.domain.model.Post
import com.turistgo.app.domain.model.User
import com.turistgo.app.domain.model.Comment
import com.turistgo.app.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface AppDataRepository {
    // User related
    fun getUsers(): Flow<List<User>>
    suspend fun saveUser(user: User)
    suspend fun updateUser(user: User)
    suspend fun getUserByEmail(email: String): User?
    suspend fun getUserByUsername(username: String): User?
    suspend fun getUserById(userId: String): User?
    suspend fun deleteUser(userId: String)

    // Post related
    fun getPosts(status: com.turistgo.app.domain.model.PostStatus? = null): Flow<List<Post>>
    fun getPostsByAuthor(authorId: String): Flow<List<Post>>
    suspend fun savePost(post: Post)
    suspend fun updatePostStatus(postId: String, status: com.turistgo.app.domain.model.PostStatus)
    suspend fun getPostById(postId: String): Post?
    
    // Saved posts
    suspend fun toggleSavedPost(userId: String, postId: String)
    fun getSavedPosts(userId: String): Flow<List<Post>>
    suspend fun toggleLikedPost(userId: String, postId: String)
    fun getLikedPosts(userId: String): Flow<List<Post>>

    // Comments related
    fun getComments(postId: String): Flow<List<Comment>>
    suspend fun addComment(comment: Comment)

    // Notifications related
    fun getNotifications(userId: String): Flow<List<Notification>>
    suspend fun addNotification(notification: Notification)
    suspend fun markNotificationAsRead(notificationId: String)
}
