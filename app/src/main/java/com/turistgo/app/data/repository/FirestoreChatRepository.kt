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
                snap?.let { snapshot ->
                    // Deserializar manualmente para evitar fallos con tipos complejos (Post, enums)
                    val messages = snapshot.documents.mapNotNull { doc ->
                        try {
                            ChatMessage(
                                id = doc.getString("id") ?: doc.id,
                                content = doc.getString("content") ?: "",
                                isFromUser = doc.getBoolean("isFromUser") ?: false,
                                timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                                isPlanResponse = doc.getBoolean("isPlanResponse") ?: false,
                                suggestedDestinations = emptyList() // Se reconstruye en memoria en el ViewModel
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(messages)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun saveMessages(messages: List<ChatMessage>) {
        val col = getChatMessagesCol()
        val batch = firestore.batch()
        messages.forEach { message ->
            val docRef = col.document(message.id)
            // Guardar SOLO campos primitivos — NO incluir suggestedDestinations (List<Post>)
            // ya que Firestore no puede serializar/deserializar correctamente enums anidados.
            val data = hashMapOf(
                "id" to message.id,
                "content" to message.content,
                "isFromUser" to message.isFromUser,
                "timestamp" to message.timestamp,
                "isPlanResponse" to message.isPlanResponse
            )
            batch.set(docRef, data)
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
