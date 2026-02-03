package com.dentalcare.app.data.repository

import com.dentalcare.app.data.model.User
import com.dentalcare.app.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    fun signIn(email: String, password: String): Flow<Resource<User>> = flow {
        try {
            emit(Resource.Loading())
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID not found")
            
            val userDoc = firestore.collection("users").document(userId).get().await()
            val user = userDoc.toObject(User::class.java)
            
            if (user != null) {
                emit(Resource.Success(user))
            } else {
                emit(Resource.Error("User data not found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
    
    fun signUp(email: String, password: String, name: String): Flow<Resource<User>> = flow {
        try {
            emit(Resource.Loading())
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID not found")
            
            val user = User(
                id = userId,
                email = email,
                name = name
            )
            
            firestore.collection("users").document(userId).set(user).await()
            emit(Resource.Success(user))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
    
    fun signOut() {
        auth.signOut()
    }
    
    fun getCurrentUser(): Flow<Resource<User?>> = flow {
        try {
            emit(Resource.Loading())
            val userId = auth.currentUser?.uid
            
            if (userId != null) {
                val userDoc = firestore.collection("users").document(userId).get().await()
                val user = userDoc.toObject(User::class.java)
                emit(Resource.Success(user))
            } else {
                emit(Resource.Success(null))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
}
