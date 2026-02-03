package com.dentalcare.app.data.repository

import com.dentalcare.app.data.model.Appointment
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
class AppointmentRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getAppointments(): Flow<Resource<List<Appointment>>> = callbackFlow {
        trySend(Resource.Loading())
        
        val subscription = firestore.collection("appointments")
            .orderBy("date", Query.Direction.DESCENDING)
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
    
    fun getAppointmentsByPatient(patientId: String): Flow<Resource<List<Appointment>>> = callbackFlow {
        trySend(Resource.Loading())
        
        val subscription = firestore.collection("appointments")
            .whereEqualTo("patientId", patientId)
            .orderBy("date", Query.Direction.DESCENDING)
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
    
    fun getAppointment(appointmentId: String): Flow<Resource<Appointment>> = flow {
        try {
            emit(Resource.Loading())
            val doc = firestore.collection("appointments").document(appointmentId).get().await()
            val appointment = doc.toObject(Appointment::class.java)
            
            if (appointment != null) {
                emit(Resource.Success(appointment))
            } else {
                emit(Resource.Error("Appointment not found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
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
    
    fun checkDoubleBooking(doctorId: String, date: String, time: String, excludeId: String = ""): Flow<Resource<Boolean>> = flow {
        try {
            emit(Resource.Loading())
            val query = firestore.collection("appointments")
                .whereEqualTo("doctorId", doctorId)
                .whereEqualTo("date", date)
                .whereEqualTo("time", time)
                .get()
                .await()
            
            val hasConflict = query.documents.any { it.id != excludeId }
            emit(Resource.Success(hasConflict))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
}
