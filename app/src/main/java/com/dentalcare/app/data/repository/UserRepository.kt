package com.dentalcare.app.data.repository

import com.dentalcare.app.data.model.User
import com.dentalcare.app.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getUsers(): Flow<Resource<List<User>>> = callbackFlow {
        trySend(Resource.Loading())
        
        val subscription = firestore.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "An error occurred"))
                    return@addSnapshotListener
                }
                
                val users = snapshot?.documents?.mapNotNull { 
                    it.toObject(User::class.java)
                } ?: emptyList()
                
                trySend(Resource.Success(users))
            }
        
        awaitClose { subscription.remove() }
    }
    
    fun getDentists(): Flow<Resource<List<User>>> = callbackFlow {
        trySend(Resource.Loading())
        
        val subscription = firestore.collection("users")
            .whereEqualTo("role", "DENTIST")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "An error occurred"))
                    return@addSnapshotListener
                }
                
                val dentists = snapshot?.documents?.mapNotNull { 
                    it.toObject(User::class.java)
                } ?: emptyList()
                
                trySend(Resource.Success(dentists))
            }
        
        awaitClose { subscription.remove() }
    }
    
    fun getUser(userId: String): Flow<Resource<User>> = flow {
        try {
            emit(Resource.Loading())
            val doc = firestore.collection("users").document(userId).get().await()
            val user = doc.toObject(User::class.java)
            
            if (user != null) {
                emit(Resource.Success(user))
            } else {
                emit(Resource.Error("User not found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
}
