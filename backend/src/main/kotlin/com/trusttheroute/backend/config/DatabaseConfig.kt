package com.trusttheroute.backend.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

object DatabaseConfig {
    private lateinit var dataSource: HikariDataSource
    
    fun init() {
        val dbPassword = System.getenv("DB_PASSWORD") ?: "your_password_here"
        
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = "jdbc:postgresql://localhost:5432/trust_the_route"
            username = "trust_user"
            password = dbPassword
            maximumPoolSize = 10
            minimumIdle = 2
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }
        
        dataSource = HikariDataSource(config)
        Database.connect(dataSource)
    }
    
    fun close() {
        if (::dataSource.isInitialized) {
            dataSource.close()
        }
    }
}
