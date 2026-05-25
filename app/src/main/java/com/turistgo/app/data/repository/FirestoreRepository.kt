package com.turistgo.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.turistgo.app.domain.model.*
import com.turistgo.app.domain.repository.AppDataRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : AppDataRepository {

    private val usersCol = firestore.collection("users")
    private val postsCol = firestore.collection("posts")
    private val commentsCol = firestore.collection("comments")
    private val notificationsCol = firestore.collection("notifications")

    // ── Users ──────────────────────────────────────────────────────────────

    override fun getUsers(): Flow<List<User>> = callbackFlow {
        val listener = usersCol.addSnapshotListener { snap, _ ->
            snap?.let { trySend(it.toObjects(User::class.java)) }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun saveUser(user: User) {
        usersCol.document(user.id).set(user).await()
    }

    override suspend fun updateUser(user: User) {
        usersCol.document(user.id).set(user).await()
    }

    override suspend fun getUserByEmail(email: String): User? {
        return usersCol.whereEqualTo("email", email).get().await()
            .toObjects(User::class.java).firstOrNull()
    }

    override suspend fun getUserByUsername(username: String): User? {
        return usersCol.whereEqualTo("username", username).get().await()
            .toObjects(User::class.java).firstOrNull()
    }

    override suspend fun getUserById(userId: String): User? {
        return usersCol.document(userId).get().await().toObject(User::class.java)
    }

    override suspend fun deleteUser(userId: String) {
        usersCol.document(userId).delete().await()
    }

    override suspend fun updateFcmToken(userId: String, token: String) {
        try {
            usersCol.document(userId).update("fcmToken", token).await()
        } catch (e: Exception) {
            // Fallback if document doesn't have other fields or fails update (e.g. merge)
            usersCol.document(userId).set(mapOf("fcmToken" to token), com.google.firebase.firestore.SetOptions.merge()).await()
        }
    }


    // ── Posts ──────────────────────────────────────────────────────────────

    override fun getPosts(status: PostStatus?): Flow<List<Post>> = callbackFlow {
        val query = if (status != null)
            postsCol.whereEqualTo("status", status.name)
        else
            postsCol
        val listener = query.addSnapshotListener { snap, _ ->
            snap?.let { trySend(it.toObjects(Post::class.java)) }
        }
        awaitClose { listener.remove() }
    }

    override fun getPostsByAuthor(authorId: String): Flow<List<Post>> = callbackFlow {
        val listener = postsCol.whereEqualTo("authorId", authorId)
            .addSnapshotListener { snap, _ ->
                snap?.let { trySend(it.toObjects(Post::class.java)) }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun savePost(post: Post) {
        val id = if (post.id.isEmpty()) postsCol.document().id else post.id
        val docRef = postsCol.document(id)
        val exists = docRef.get().await().exists()
        docRef.set(post.copy(id = id)).await()
        if (!exists && post.authorId.isNotEmpty()) {
            incrementUserPoints(post.authorId, 1)
            saveActivityLog(
                ActivityLog(
                    id = java.util.UUID.randomUUID().toString(),
                    type = "PUBLISH_POST",
                    userId = post.authorId,
                    userName = post.authorName,
                    details = "Creó la publicación: ${post.name}",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun updatePostStatus(postId: String, status: PostStatus) {
        postsCol.document(postId).update("status", status.name).await()
    }

    override suspend fun getPostById(postId: String): Post? {
        return postsCol.document(postId).get().await().toObject(Post::class.java)
    }

    override suspend fun deletePost(postId: String) {
        postsCol.document(postId).delete().await()
    }

    // ── Saved & Liked Posts ────────────────────────────────────────────────

    override suspend fun toggleSavedPost(userId: String, postId: String) {
        val user = getUserById(userId) ?: return
        val newIds = if (user.savedPostIds.contains(postId))
            user.savedPostIds - postId
        else
            user.savedPostIds + postId
        usersCol.document(userId).update("savedPostIds", newIds).await()
    }

    override fun getSavedPosts(userId: String): Flow<List<Post>> =
        combine(getUsers(), getPosts()) { users, posts ->
            val saved = users.find { it.id == userId }?.savedPostIds ?: emptyList()
            posts.filter { it.id in saved && it.status == PostStatus.APPROVED }
        }

    override suspend fun toggleLikedPost(userId: String, postId: String) {
        val user = getUserById(userId) ?: return
        val newIds = if (user.likedPostIds.contains(postId))
            user.likedPostIds - postId
        else
            user.likedPostIds + postId
        usersCol.document(userId).update("likedPostIds", newIds).await()
    }

    override fun getLikedPosts(userId: String): Flow<List<Post>> =
        combine(getUsers(), getPosts()) { users, posts ->
            val liked = users.find { it.id == userId }?.likedPostIds ?: emptyList()
            posts.filter { it.id in liked && it.status == PostStatus.APPROVED }
        }

    // ── Following ──────────────────────────────────────────────────────────

    override suspend fun toggleFollow(currentUserId: String, targetUserId: String) {
        val current = getUserById(currentUserId) ?: return
        val target = getUserById(targetUserId) ?: return
        val isFollowing = current.followingIds.contains(targetUserId)
        usersCol.document(currentUserId).update(
            "followingIds",
            if (isFollowing) current.followingIds - targetUserId
            else current.followingIds + targetUserId
        ).await()
        usersCol.document(targetUserId).update(
            "followerIds",
            if (isFollowing) target.followerIds - currentUserId
            else target.followerIds + currentUserId
        ).await()
    }

    override suspend fun sendFollowRequest(senderId: String, senderName: String, targetUserId: String) {
        val sender = getUserById(senderId) ?: return
        if (!sender.pendingFollowRequestIds.contains(targetUserId)) {
            usersCol.document(senderId).update(
                "pendingFollowRequestIds",
                sender.pendingFollowRequestIds + targetUserId
            ).await()
        }
        addNotification(
            Notification(
                id = java.util.UUID.randomUUID().toString(),
                userId = targetUserId,
                title = "Solicitud de seguimiento",
                message = "$senderName quiere seguirte en TuristGo.",
                type = NotificationType.FOLLOW_REQUEST,
                senderId = senderId,
                senderName = senderName
            )
        )
    }

    override suspend fun handleFollowRequest(notificationId: String, accepted: Boolean) {
        val notification = notificationsCol.document(notificationId).get().await()
            .toObject(Notification::class.java) ?: return
        val senderId = notification.senderId ?: return
        processFollowRequest(senderId, notification.userId, accepted, notificationId)
    }

    override suspend fun handleFollowRequestByUserId(currentUserId: String, senderId: String, accepted: Boolean) {
        val notification = notificationsCol
            .whereEqualTo("userId", currentUserId)
            .whereEqualTo("senderId", senderId)
            .whereEqualTo("type", NotificationType.FOLLOW_REQUEST.name)
            .get().await()
            .toObjects(Notification::class.java).firstOrNull()
        processFollowRequest(senderId, currentUserId, accepted, notification?.id)
    }

    private suspend fun processFollowRequest(
        senderId: String, targetId: String,
        accepted: Boolean, notificationId: String?
    ) {
        val sender = getUserById(senderId) ?: return
        usersCol.document(senderId).update(
            "pendingFollowRequestIds",
            sender.pendingFollowRequestIds - targetId
        ).await()
        if (accepted) {
            toggleFollow(senderId, targetId)
            val targetName = getUserById(targetId)?.username ?: "Alguien"
            addNotification(
                Notification(
                    id = java.util.UUID.randomUUID().toString(),
                    userId = senderId,
                    title = "¡Ahora son amigos!",
                    message = "$targetName aceptó tu solicitud.",
                    type = NotificationType.FOLLOW_ACCEPTED,
                    senderId = targetId
                )
            )
        }
        notificationId?.let { markNotificationAsRead(it) }
    }

    // ── Comments ───────────────────────────────────────────────────────────

    override fun getComments(postId: String): Flow<List<Comment>> = callbackFlow {
        val listener = commentsCol.whereEqualTo("postId", postId)
            .addSnapshotListener { snap, _ ->
                snap?.let { trySend(it.toObjects(Comment::class.java)) }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addComment(comment: Comment) {
        val id = if (comment.id.isEmpty()) commentsCol.document().id else comment.id
        commentsCol.document(id).set(comment.copy(id = id)).await()
        val post = getPostById(comment.postId)
        post?.let {
            postsCol.document(it.id).update("commentCount", it.commentCount + 1).await()
        }
        if (comment.authorId.isNotEmpty()) {
            incrementUserPoints(comment.authorId, 1)
        }
    }

    // ── Notifications ──────────────────────────────────────────────────────

    override fun getNotifications(userId: String): Flow<List<Notification>> = callbackFlow {
        val listener = notificationsCol.whereEqualTo("userId", userId)
            .addSnapshotListener { snap, _ ->
                snap?.let {
                    trySend(
                        it.toObjects(Notification::class.java)
                            .sortedByDescending { n -> n.timestamp }
                    )
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addNotification(notification: Notification) {
        notificationsCol.document(notification.id).set(notification).await()
    }

    override suspend fun markNotificationAsRead(notificationId: String) {
        notificationsCol.document(notificationId).update("isRead", true).await()
    }

    private suspend fun incrementUserPoints(userId: String, pointsToAdd: Int) {
        try {
            usersCol.document(userId).update(
                "points", 
                com.google.firebase.firestore.FieldValue.increment(pointsToAdd.toLong())
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val activityLogsCol = firestore.collection("activity_logs")

    override fun getActivityLogs(): Flow<List<ActivityLog>> = callbackFlow {
        val listener = activityLogsCol.addSnapshotListener { snap, _ ->
            snap?.let {
                trySend(
                    it.toObjects(ActivityLog::class.java)
                        .sortedByDescending { log -> log.timestamp }
                )
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun saveActivityLog(log: ActivityLog) {
        try {
            val id = log.id.ifEmpty { activityLogsCol.document().id }
            activityLogsCol.document(id).set(log.copy(id = id)).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
