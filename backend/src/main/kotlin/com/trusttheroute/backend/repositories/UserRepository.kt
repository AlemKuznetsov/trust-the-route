package com.trusttheroute.backend.repositories

import com.trusttheroute.backend.models.Users
import com.trusttheroute.backend.models.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

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
                Users.insert {
                    it[Users.id] = newUserId
                    it[Users.email] = email
                    it[Users.passwordHash] = passwordHash
                    it[Users.name] = name
                }
                
                // Получить созданного пользователя
                val userRow = Users.select { Users.id eq newUserId }.first()
                User(
                    id = userRow[Users.id].toString(),
                    email = userRow[Users.email],
                    name = userRow[Users.name] ?: ""
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
                    name = row[Users.name] ?: ""
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
                        name = userRow[Users.name] ?: ""
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
}
