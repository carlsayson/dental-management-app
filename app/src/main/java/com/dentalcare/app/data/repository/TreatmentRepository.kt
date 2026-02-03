package com.dentalcare.app.data.repository

import com.dentalcare.app.data.model.Treatment
import com.dentalcare.app.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TreatmentRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getTreatments(): Flow<Resource<List<Treatment>>> = callbackFlow {
        trySend(Resource.Loading())
        
        val subscription = firestore.collection("treatments")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "An error occurred"))
                    return@addSnapshotListener
                }
                
                val treatments = snapshot?.documents?.mapNotNull { 
                    it.toObject(Treatment::class.java)
                } ?: emptyList()
                
                trySend(Resource.Success(treatments))
            }
        
        awaitClose { subscription.remove() }
    }
    
    fun getTreatmentsByPatient(patientId: String): Flow<Resource<List<Treatment>>> = callbackFlow {
        trySend(Resource.Loading())
        
        val subscription = firestore.collection("treatments")
            .whereEqualTo("patientId", patientId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "An error occurred"))
                    return@addSnapshotListener
                }
                
                val treatments = snapshot?.documents?.mapNotNull { 
                    it.toObject(Treatment::class.java)
                } ?: emptyList()
                
                trySend(Resource.Success(treatments))
            }
        
        awaitClose { subscription.remove() }
    }
    
    fun getTreatment(treatmentId: String): Flow<Resource<Treatment>> = flow {
        try {
            emit(Resource.Loading())
            val doc = firestore.collection("treatments").document(treatmentId).get().await()
            val treatment = doc.toObject(Treatment::class.java)
            
            if (treatment != null) {
                emit(Resource.Success(treatment))
            } else {
                emit(Resource.Error("Treatment not found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
    
    fun addTreatment(treatment: Treatment): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            val docRef = firestore.collection("treatments").document()
            val newTreatment = treatment.copy(id = docRef.id)
            docRef.set(newTreatment).await()
            emit(Resource.Success(docRef.id))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
    
    fun updateTreatment(treatment: Treatment): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            firestore.collection("treatments")
                .document(treatment.id)
                .set(treatment.copy(updatedAt = System.currentTimeMillis()))
                .await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
    
    fun deleteTreatment(treatmentId: String): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            firestore.collection("treatments").document(treatmentId).delete().await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
}
