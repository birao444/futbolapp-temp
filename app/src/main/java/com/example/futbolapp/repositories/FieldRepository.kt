package com.example.futbolapp.repositories

import com.example.futbolapp.firebase.FirebaseFirestoreManager
import com.example.futbolapp.models.Field

class FieldRepository(
    private val firestoreManager: FirebaseFirestoreManager = FirebaseFirestoreManager()
) {

    suspend fun createField(field: Field): String {
        val fieldId = firestoreManager.db.collection("fields").document().id
        val fieldData = mapOf(
            "id" to fieldId,
            "name" to field.name,
            "location" to field.location,
            "capacity" to field.capacity,
            "surface" to field.surface,
            "dimensions" to field.dimensions,
            "facilities" to field.facilities,
            "createdAt" to System.currentTimeMillis()
        )
        firestoreManager.createOrUpdateField(fieldId, fieldData)
        return fieldId
    }

    suspend fun updateField(fieldId: String, updates: Map<String, Any>) {
        firestoreManager.createOrUpdateField(fieldId, updates)
    }

    suspend fun getField(fieldId: String): Field? {
        // This would need to be implemented in FirebaseFirestoreManager
        // For now, return null
        return null
    }

    suspend fun getAllFields(): List<Field> {
        val fieldsData = firestoreManager.getFields()
        return fieldsData.mapNotNull { mapToField(it) }
    }

    suspend fun deleteField(fieldId: String) {
        // This would need to be implemented in FirebaseFirestoreManager
        // For now, do nothing
    }

    private fun mapToField(data: Map<String, Any>): Field {
        return Field(
            id = data["id"] as? String ?: "",
            name = data["name"] as? String ?: "",
            location = data["location"] as? String ?: "",
            capacity = (data["capacity"] as? Number)?.toInt() ?: 0,
            surface = data["surface"] as? String ?: "",
            dimensions = data["dimensions"] as? String ?: "",
            facilities = (data["facilities"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
        )
    }
}
