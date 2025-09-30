package com.example.futbolapp.repositories

import com.example.futbolapp.firebase.FirebaseFirestoreManager
import com.example.futbolapp.models.Improvement

class ImprovementRepository(
    private val firestoreManager: FirebaseFirestoreManager = FirebaseFirestoreManager()
) {

    suspend fun createImprovement(improvement: Improvement): String {
        val improvementId = firestoreManager.db.collection("improvements").document().id
        val improvementData = mapOf(
            "id" to improvementId,
            "teamId" to improvement.teamId,
            "type" to improvement.type,
            "description" to improvement.description,
            "cost" to improvement.cost,
            "duration" to improvement.duration,
            "effect" to improvement.effect,
            "isCompleted" to improvement.isCompleted,
            "createdAt" to System.currentTimeMillis()
        )
        firestoreManager.createOrUpdateImprovement(improvementId, improvementData)
        return improvementId
    }

    suspend fun updateImprovement(improvementId: String, updates: Map<String, Any>) {
        firestoreManager.createOrUpdateImprovement(improvementId, updates)
    }

    suspend fun getImprovementsForTeam(teamId: String): List<Improvement> {
        val improvementsData = firestoreManager.getImprovementsForTeam(teamId)
        return improvementsData.mapNotNull { mapToImprovement(it) }
    }

    suspend fun completeImprovement(improvementId: String) {
        val updates = mapOf("isCompleted" to true, "completedAt" to System.currentTimeMillis())
        firestoreManager.createOrUpdateImprovement(improvementId, updates)
    }

    private fun mapToImprovement(data: Map<String, Any>): Improvement {
        return Improvement(
            id = data["id"] as? String ?: "",
            teamId = data["teamId"] as? String ?: "",
            type = data["type"] as? String ?: "",
            description = data["description"] as? String ?: "",
            cost = (data["cost"] as? Number)?.toDouble() ?: 0.0,
            duration = (data["duration"] as? Number)?.toLong() ?: 0L,
            effect = data["effect"] as? String ?: "",
            isCompleted = data["isCompleted"] as? Boolean ?: false
        )
    }
}
