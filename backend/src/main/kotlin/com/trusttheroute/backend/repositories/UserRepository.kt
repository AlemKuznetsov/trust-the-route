package com.trusttheroute.backend.repositories

import com.trusttheroute.backend.models.Users
import com.trusttheroute.backend.models.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.*
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock

class UserRepository {
    
    fun createUser(email: String, password: String, name: String): User? {
        return transaction {
            try {
                // Проверить, существует ли пользователь
                val existingUser = Users.select { Users.email eq email }.firstOrNull()
                if (existingUser != null) {
                    return@transaction null
                }
                
                // Хешировать пароль
                val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
                
                // Создать пользователя
                val newUserId = UUID.randomUUID()
                val currentTime = Clock.System.now()
                Users.insert {
                    it[Users.id] = newUserId
                    it[Users.email] = email
                    it[Users.passwordHash] = passwordHash
                    it[Users.name] = name
                    it[Users.createdAt] = currentTime
                    it[Users.updatedAt] = currentTime
                }
                
                // Получить созданного пользователя
                val userRow = Users.select { Users.id eq newUserId }.first()
                User(
                    id = userRow[Users.id].toString(),
                    email = userRow[Users.email],
                    name = userRow[Users.name] ?: "",
                    createdAt = userRow[Users.createdAt].toString()
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun createYandexUser(email: String, name: String): User? {
        return transaction {
            try {
                // Проверить, существует ли пользователь
                val existingUser = Users.select { Users.email eq email }.firstOrNull()
                if (existingUser != null) {
                    // Пользователь уже существует, возвращаем его
                    return@transaction User(
                        id = existingUser[Users.id].toString(),
                        email = existingUser[Users.email],
                        name = existingUser[Users.name] ?: name,
                        createdAt = existingUser[Users.createdAt].toString()
                    )
                }
                
                // Создать пользователя с пустым паролем (для YandexID пользователей)
                // Используем специальный хеш, который нельзя использовать для входа через пароль
                val passwordHash = BCrypt.hashpw(UUID.randomUUID().toString() + "_yandex", BCrypt.gensalt())
                
                // Создать пользователя
                val newUserId = UUID.randomUUID()
                val currentTime = Clock.System.now()
                Users.insert {
                    it[Users.id] = newUserId
                    it[Users.email] = email
                    it[Users.passwordHash] = passwordHash
                    it[Users.name] = name
                    it[Users.createdAt] = currentTime
                    it[Users.updatedAt] = currentTime
                }
                
                // Получить созданного пользователя
                val userRow = Users.select { Users.id eq newUserId }.first()
                User(
                    id = userRow[Users.id].toString(),
                    email = userRow[Users.email],
                    name = userRow[Users.name] ?: "",
                    createdAt = userRow[Users.createdAt].toString()
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    
    fun getUserByEmail(email: String): User? {
        return transaction {
            Users.select { Users.email eq email }.firstOrNull()?.let { row ->
                User(
                    id = row[Users.id].toString(),
                    email = row[Users.email],
                    name = row[Users.name] ?: "",
                    createdAt = row[Users.createdAt].toString()
                )
            }
        }
    }
    
    fun verifyPassword(email: String, password: String): User? {
        return transaction {
            val userRow = Users.select { Users.email eq email }.firstOrNull()
            if (userRow != null) {
                val passwordHash = userRow[Users.passwordHash]
                if (BCrypt.checkpw(password, passwordHash)) {
                    User(
                        id = userRow[Users.id].toString(),
                        email = userRow[Users.email],
                        name = userRow[Users.name] ?: "",
                        createdAt = userRow[Users.createdAt].toString()
                    )
                } else {
                    null
                }
            } else {
                null
            }
        }
    }
    
    fun userExists(email: String): Boolean {
        return transaction {
            Users.select { Users.email eq email }.count() > 0
        }
    }

    fun getUserById(userId: String): User? {
        return transaction {
            try {
                val uuid = UUID.fromString(userId)
                Users.select { Users.id eq uuid }.firstOrNull()?.let { row ->
                    User(
                        id = row[Users.id].toString(),
                        email = row[Users.email],
                        name = row[Users.name] ?: "",
                        createdAt = row[Users.createdAt].toString()
                    )
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    fun updateProfile(userId: String, name: String): User? {
        return transaction {
            try {
                val uuid = UUID.fromString(userId)
                Users.update({ Users.id eq uuid }) {
                    it[Users.name] = name
                    it[Users.updatedAt] = Clock.System.now()
                }
                getUserById(userId)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun changePassword(userId: String, oldPassword: String, newPassword: String): Boolean {
        return transaction {
            try {
                val uuid = UUID.fromString(userId)
                val userRow = Users.select { Users.id eq uuid }.firstOrNull()
                if (userRow != null) {
                    val passwordHash = userRow[Users.passwordHash]
                    if (BCrypt.checkpw(oldPassword, passwordHash)) {
                        val newPasswordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt())
                        Users.update({ Users.id eq uuid }) {
                            it[Users.passwordHash] = newPasswordHash
                            it[Users.updatedAt] = Clock.System.now()
                        }
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    fun deleteUser(userId: String): Boolean {
        return transaction {
            try {
                val uuid = UUID.fromString(userId)
                val userExists = Users.select { Users.id eq uuid }.count() > 0
                if (userExists) {
                    val query = "DELETE FROM ${Users.tableName} WHERE ${Users.id.name} = '${uuid}'"
                    exec(query)
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
