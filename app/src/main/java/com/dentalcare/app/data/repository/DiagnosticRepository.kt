package com.dentalcare.app.data.repository

import android.net.Uri
import com.dentalcare.app.data.model.Diagnostic
import com.dentalcare.app.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiagnosticRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    fun getDiagnostics(): Flow<Resource<List<Diagnostic>>> = callbackFlow {
        trySend(Resource.Loading())
        
        val subscription = firestore.collection("diagnostics")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "An error occurred"))
                    return@addSnapshotListener
                }
                
                val diagnostics = snapshot?.documents?.mapNotNull { 
                    it.toObject(Diagnostic::class.java)
                } ?: emptyList()
                
                trySend(Resource.Success(diagnostics))
            }
        
        awaitClose { subscription.remove() }
    }
    
    fun getDiagnosticsByPatient(patientId: String): Flow<Resource<List<Diagnostic>>> = callbackFlow {
        trySend(Resource.Loading())
        
        val subscription = firestore.collection("diagnostics")
            .whereEqualTo("patientId", patientId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "An error occurred"))
                    return@addSnapshotListener
                }
                
                val diagnostics = snapshot?.documents?.mapNotNull { 
                    it.toObject(Diagnostic::class.java)
                } ?: emptyList()
                
                trySend(Resource.Success(diagnostics))
            }
        
        awaitClose { subscription.remove() }
    }
    
    fun getDiagnostic(diagnosticId: String): Flow<Resource<Diagnostic>> = flow {
        try {
            emit(Resource.Loading())
            val doc = firestore.collection("diagnostics").document(diagnosticId).get().await()
            val diagnostic = doc.toObject(Diagnostic::class.java)
            
            if (diagnostic != null) {
                emit(Resource.Success(diagnostic))
            } else {
                emit(Resource.Error("Diagnostic not found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
    
    suspend fun uploadImage(uri: Uri, diagnosticId: String): Result<String> {
        return try {
            val storageRef = storage.reference
                .child("diagnostics/$diagnosticId/${System.currentTimeMillis()}.jpg")
            val uploadTask = storageRef.putFile(uri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun addDiagnostic(diagnostic: Diagnostic): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            val docRef = firestore.collection("diagnostics").document()
            val newDiagnostic = diagnostic.copy(id = docRef.id)
            docRef.set(newDiagnostic).await()
            emit(Resource.Success(docRef.id))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
    
    fun updateDiagnostic(diagnostic: Diagnostic): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            firestore.collection("diagnostics")
                .document(diagnostic.id)
                .set(diagnostic.copy(updatedAt = System.currentTimeMillis()))
                .await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
    
    fun deleteDiagnostic(diagnosticId: String): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            firestore.collection("diagnostics").document(diagnosticId).delete().await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
}
