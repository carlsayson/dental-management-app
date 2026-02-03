package com.dentalcare.app.data.repository

import com.dentalcare.app.data.model.Appointment
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
class AppointmentRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getAppointments(): Flow<Resource<List<Appointment>>> = callbackFlow {
        trySend(Resource.Loading())
        
        val subscription = firestore.collection("appointments")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "An error occurred"))
                    return@addSnapshotListener
                }
                
                val appointments = snapshot?.documents?.mapNotNull { 
                    it.toObject(Appointment::class.java)
                } ?: emptyList()
                
                trySend(Resource.Success(appointments))
            }
        
        awaitClose { subscription.remove() }
    }
    
    fun addAppointment(appointment: Appointment): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            val docRef = firestore.collection("appointments").document()
            val newAppointment = appointment.copy(id = docRef.id)
            docRef.set(newAppointment).await()
            emit(Resource.Success(docRef.id))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
    
    fun updateAppointment(appointment: Appointment): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            firestore.collection("appointments")
                .document(appointment.id)
                .set(appointment.copy(updatedAt = System.currentTimeMillis()))
                .await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
    
    fun deleteAppointment(appointmentId: String): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            firestore.collection("appointments").document(appointmentId).delete().await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
}
