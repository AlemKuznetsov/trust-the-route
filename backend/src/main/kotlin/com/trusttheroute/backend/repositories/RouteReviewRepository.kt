package com.trusttheroute.backend.repositories

import com.trusttheroute.backend.config.DatabaseConfig
import java.sql.Timestamp
import java.time.Instant

data class RouteReview(
    val id: Int,
    val routeId: String,
    val userId: String,
    val userName: String?,
    val rating: Int,
    val review: String?,
    val visitedAttractions: List<String>,
    val createdAt: Long
)

class RouteReviewRepository {
    
    fun saveReview(
        routeId: String,
        userId: String,
        rating: Int,
        review: String?,
        visitedAttractions: List<String>
    ): Boolean {
        return try {
            println("DEBUG: RouteReviewRepository.saveReview called")
            println("DEBUG: routeId=$routeId, userId=$userId, rating=$rating, review=${review?.take(50)}")
            
            val sql = """
                INSERT INTO route_reviews (route_id, user_id, rating, review, visited_attractions, created_at)
                VALUES (?, ?, ?, ?, ?, ?)
            """.trimIndent()
            
            DatabaseConfig.getConnection().use { connection ->
                connection.autoCommit = false
                try {
                    connection.prepareStatement(sql).use { stmt ->
                        stmt.setString(1, routeId)
                        // userId теперь UUID, нужно использовать setObject для UUID
                        println("DEBUG: Setting parameters...")
                        stmt.setObject(2, java.util.UUID.fromString(userId), java.sql.Types.OTHER)
                        stmt.setInt(3, rating)
                        stmt.setString(4, review)
                        stmt.setString(5, visitedAttractions.joinToString(","))
                        stmt.setTimestamp(6, Timestamp.from(Instant.now()))
                        
                        println("DEBUG: Executing INSERT...")
                        val rowsAffected = stmt.executeUpdate()
                        println("DEBUG: Rows affected: $rowsAffected")
                        
                        connection.commit()
                        println("DEBUG: Transaction committed")
                        
                        rowsAffected > 0
                    }
                } catch (e: Exception) {
                    connection.rollback()
                    println("ERROR: Transaction rolled back: ${e.message}")
                    throw e
                }
            }
        } catch (e: Exception) {
            println("ERROR: Exception in saveReview: ${e.message}")
            e.printStackTrace()
            false
        }
    }
    
           fun getReviewsByRouteId(routeId: String, limit: Int = 10): List<RouteReview> {
               return try {
                   println("DEBUG: RouteReviewRepository.getReviewsByRouteId called with routeId: $routeId, limit: $limit")
                   val sql = """
                       SELECT
                           rr.id,
                           rr.route_id,
                           rr.user_id,
                           u.name as user_name,
                           rr.rating,
                           rr.review,
                           rr.visited_attractions,
                           EXTRACT(EPOCH FROM rr.created_at)::bigint as created_at
                       FROM route_reviews rr
                       LEFT JOIN users u ON rr.user_id = u.id
                       WHERE rr.route_id = ?
                       ORDER BY rr.created_at DESC
                       LIMIT ?
                   """.trimIndent()

                   DatabaseConfig.getConnection().use { connection ->
                       connection.prepareStatement(sql).use { stmt ->
                           stmt.setString(1, routeId)
                           stmt.setInt(2, limit)

                           val reviews = mutableListOf<RouteReview>()
                           stmt.executeQuery().use { rs ->
                               var count = 0
                               while (rs.next()) {
                                   count++
                                   val visitedAttractionsStr = rs.getString("visited_attractions") ?: ""
                                   // UUID в PostgreSQL возвращается как строка через getObject или getString
                                   val userIdObj = rs.getObject("user_id")
                                   val userIdStr = if (userIdObj is java.util.UUID) {
                                       userIdObj.toString()
                                   } else {
                                       userIdObj.toString()
                                   }
                                   
                                   val review = RouteReview(
                                       id = rs.getInt("id"),
                                       routeId = rs.getString("route_id"),
                                       userId = userIdStr,
                                       userName = rs.getString("user_name"),
                                       rating = rs.getInt("rating"),
                                       review = rs.getString("review"),
                                       visitedAttractions = if (visitedAttractionsStr.isBlank()) {
                                           emptyList()
                                       } else {
                                           visitedAttractionsStr.split(",")
                                       },
                                       createdAt = rs.getLong("created_at") * 1000 // Конвертируем в миллисекунды
                                   )
                                   println("DEBUG: Found review: id=${review.id}, routeId=${review.routeId}, rating=${review.rating}, review=${review.review?.take(50)}")
                                   reviews.add(review)
                               }
                               println("DEBUG: Processed $count reviews from database")
                           }
                           println("DEBUG: Returning ${reviews.size} reviews")
                           reviews
                       }
                   }
               } catch (e: Exception) {
                   println("ERROR: Exception in getReviewsByRouteId: ${e.message}")
                   e.printStackTrace()
                   emptyList()
               }
    }
    
    fun getAverageRating(routeId: String): Double {
        return try {
            val sql = """
                SELECT AVG(rating) as avg_rating
                FROM route_reviews
                WHERE route_id = ?
            """.trimIndent()
            
            DatabaseConfig.getConnection().use { connection ->
                connection.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, routeId)
                    
                    stmt.executeQuery().use { rs ->
                        if (rs.next()) {
                            val avg = rs.getDouble("avg_rating")
                            if (rs.wasNull()) 0.0 else avg
                        } else {
                            0.0
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }
}
