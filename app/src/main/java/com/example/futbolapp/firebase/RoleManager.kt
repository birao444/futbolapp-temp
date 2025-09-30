package com.example.futbolapp.firebase

import com.example.futbolapp.models.Permission
import com.example.futbolapp.models.TeamMember
import com.example.futbolapp.models.UserRole
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RoleManager {
    private val db = FirebaseFirestore.getInstance()
    private val teamMembersCollection = db.collection("team_members")

    /**
     * Asignar un rol a un usuario en un equipo específico
     */
    suspend fun assignRole(
        userId: String,
        teamId: String,
        role: UserRole,
        name: String,
        email: String
    ): Result<TeamMember> {
        return try {
            val teamMember = TeamMember(
                userId = userId,
                teamId = teamId,
                role = role,
                name = name,
                email = email,
                joinedAt = System.currentTimeMillis()
            )

            val docId = "$userId-$teamId"
            teamMembersCollection.document(docId).set(
                mapOf(
                    "userId" to teamMember.userId,
                    "teamId" to teamMember.teamId,
                    "role" to teamMember.role.name,
                    "name" to teamMember.name,
                    "email" to teamMember.email,
                    "joinedAt" to teamMember.joinedAt
                )
            ).await()

            Result.success(teamMember)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener el rol de un usuario en un equipo específico
     */
    suspend fun getUserRole(userId: String, teamId: String): UserRole? {
        return try {
            val docId = "$userId-$teamId"
            val document = teamMembersCollection.document(docId).get().await()
            
            if (document.exists()) {
                val roleString = document.getString("role")
                roleString?.let { UserRole.valueOf(it) }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Obtener información completa del miembro del equipo
     */
    suspend fun getTeamMember(userId: String, teamId: String): TeamMember? {
        return try {
            val docId = "$userId-$teamId"
            val document = teamMembersCollection.document(docId).get().await()
            
            if (document.exists()) {
                TeamMember(
                    userId = document.getString("userId") ?: "",
                    teamId = document.getString("teamId") ?: "",
                    role = UserRole.valueOf(document.getString("role") ?: "JUGADOR"),
                    name = document.getString("name") ?: "",
                    email = document.getString("email") ?: "",
                    joinedAt = document.getLong("joinedAt") ?: System.currentTimeMillis()
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Obtener todos los miembros de un equipo
     */
    suspend fun getTeamMembers(teamId: String): List<TeamMember> {
        return try {
            val query = teamMembersCollection.whereEqualTo("teamId", teamId)
            val result = query.get().await()
            
            result.documents.mapNotNull { doc ->
                try {
                    TeamMember(
                        userId = doc.getString("userId") ?: "",
                        teamId = doc.getString("teamId") ?: "",
                        role = UserRole.valueOf(doc.getString("role") ?: "JUGADOR"),
                        name = doc.getString("name") ?: "",
                        email = doc.getString("email") ?: "",
                        joinedAt = doc.getLong("joinedAt") ?: System.currentTimeMillis()
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Obtener miembros por rol específico
     */
    suspend fun getMembersByRole(teamId: String, role: UserRole): List<TeamMember> {
        return try {
            val query = teamMembersCollection
                .whereEqualTo("teamId", teamId)
                .whereEqualTo("role", role.name)
            val result = query.get().await()
            
            result.documents.mapNotNull { doc ->
                try {
                    TeamMember(
                        userId = doc.getString("userId") ?: "",
                        teamId = doc.getString("teamId") ?: "",
                        role = UserRole.valueOf(doc.getString("role") ?: "JUGADOR"),
                        name = doc.getString("name") ?: "",
                        email = doc.getString("email") ?: "",
                        joinedAt = doc.getLong("joinedAt") ?: System.currentTimeMillis()
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Cambiar el rol de un usuario
     */
    suspend fun updateRole(userId: String, teamId: String, newRole: UserRole): Result<Unit> {
        return try {
            val docId = "$userId-$teamId"
            teamMembersCollection.document(docId).update("role", newRole.name).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Eliminar un miembro del equipo
     */
    suspend fun removeMember(userId: String, teamId: String): Result<Unit> {
        return try {
            val docId = "$userId-$teamId"
            teamMembersCollection.document(docId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Verificar si un usuario tiene un permiso específico en un equipo
     */
    suspend fun hasPermission(userId: String, teamId: String, permission: Permission): Boolean {
        val role = getUserRole(userId, teamId) ?: return false
        return role.hasPermission(permission)
    }

    /**
     * Verificar si un usuario es el entrenador del equipo
     */
    suspend fun isCoach(userId: String, teamId: String): Boolean {
        val role = getUserRole(userId, teamId)
        return role == UserRole.ENTRENADOR
    }

    /**
     * Obtener todos los equipos donde un usuario es miembro
     */
    suspend fun getUserTeams(userId: String): List<TeamMember> {
        return try {
            val query = teamMembersCollection.whereEqualTo("userId", userId)
            val result = query.get().await()
            
            result.documents.mapNotNull { doc ->
                try {
                    TeamMember(
                        userId = doc.getString("userId") ?: "",
                        teamId = doc.getString("teamId") ?: "",
                        role = UserRole.valueOf(doc.getString("role") ?: "JUGADOR"),
                        name = doc.getString("name") ?: "",
                        email = doc.getString("email") ?: "",
                        joinedAt = doc.getLong("joinedAt") ?: System.currentTimeMillis()
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
