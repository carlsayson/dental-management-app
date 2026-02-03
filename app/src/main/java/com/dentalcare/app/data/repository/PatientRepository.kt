package com.dentalcare.app.data.repository

import com.dentalcare.app.data.model.Patient
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
class PatientRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getPatients(): Flow<Resource<List<Patient>>> = callbackFlow {
        trySend(Resource.Loading())
        
        val subscription = firestore.collection("patients")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "An error occurred"))
                    return@addSnapshotListener
                }
                
                val patients = snapshot?.documents?.mapNotNull { 
                    it.toObject(Patient::class.java)
                } ?: emptyList()
                
                trySend(Resource.Success(patients))
            }
        
        awaitClose { subscription.remove() }
    }
    
    fun getPatient(patientId: String): Flow<Resource<Patient>> = flow {
        try {
            emit(Resource.Loading())
            val doc = firestore.collection("patients").document(patientId).get().await()
            val patient = doc.toObject(Patient::class.java)
            
            if (patient != null) {
                emit(Resource.Success(patient))
            } else {
                emit(Resource.Error("Patient not found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
    
    fun addPatient(patient: Patient): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            val docRef = firestore.collection("patients").document()
            val newPatient = patient.copy(id = docRef.id)
            docRef.set(newPatient).await()
            emit(Resource.Success(docRef.id))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
    
    fun updatePatient(patient: Patient): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            firestore.collection("patients")
                .document(patient.id)
                .set(patient.copy(updatedAt = System.currentTimeMillis()))
                .await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
    
    fun deletePatient(patientId: String): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            firestore.collection("patients").document(patientId).delete().await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
}
