package com.turistgo.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.turistgo.app.domain.model.ChatMessage
import com.turistgo.app.domain.repository.ChatRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ChatRepository {

    private fun getChatMessagesCol() = firestore
        .collection("users")
        .document(firebaseAuth.currentUser?.uid ?: "anonymous")
        .collection("chat_messages")

    override fun getMessages(): Flow<List<ChatMessage>> = callbackFlow {
        val listener = getChatMessagesCol()
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, _ ->
                snap?.let { trySend(it.toObjects(ChatMessage::class.java)) }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun saveMessages(messages: List<ChatMessage>) {
        val col = getChatMessagesCol()
        val batch = firestore.batch()
        messages.forEach { message ->
            val docRef = col.document(message.id)
            batch.set(docRef, message)
        }
        batch.commit().await()
    }

    override suspend fun clearMessages() {
        val col = getChatMessagesCol()
        val snap = col.get().await()
        if (!snap.isEmpty) {
            val batch = firestore.batch()
            snap.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.commit().await()
        }
    }
}
