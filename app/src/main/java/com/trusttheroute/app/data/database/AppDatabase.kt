package com.trusttheroute.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.trusttheroute.app.data.database.dao.AttractionDao
import com.trusttheroute.app.data.database.dao.RouteDao
import com.trusttheroute.app.data.database.entity.AttractionEntity
import com.trusttheroute.app.data.database.entity.RouteEntity

@Database(
    entities = [RouteEntity::class, AttractionEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routeDao(): RouteDao
    abstract fun attractionDao(): AttractionDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Добавляем новые колонки в таблицу routes
                database.execSQL("ALTER TABLE routes ADD COLUMN history TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE routes ADD COLUMN attractionsDescription TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE routes ADD COLUMN stops TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE routes ADD COLUMN duration TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE routes ADD COLUMN interval TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE routes ADD COLUMN startPoint TEXT NOT NULL DEFAULT ''")
            }
        }
        
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Обновляем структуру таблицы attractions для поддержки нескольких изображений
                // Создаем новую таблицу с правильной структурой
                database.execSQL("""
                    CREATE TABLE attractions_new (
                        id TEXT NOT NULL PRIMARY KEY,
                        routeId TEXT NOT NULL,
                        name TEXT NOT NULL,
                        description TEXT NOT NULL,
                        latitude REAL NOT NULL,
                        longitude REAL NOT NULL,
                        imageUrls TEXT NOT NULL DEFAULT '[]',
                        audioUrl TEXT NOT NULL DEFAULT '',
                        localImagePaths TEXT NOT NULL DEFAULT '[]',
                        localAudioPath TEXT,
                        `order` INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY(routeId) REFERENCES routes(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                
                // Копируем данные из старой таблицы в новую
                database.execSQL("""
                    INSERT INTO attractions_new (
                        id, routeId, name, description, latitude, longitude, 
                        imageUrls, audioUrl, localImagePaths, localAudioPath, `order`
                    )
                    SELECT 
                        id, routeId, name, description, latitude, longitude,
                        CASE 
                            WHEN imageUrl IS NOT NULL AND imageUrl != '' 
                            THEN '["' || imageUrl || '"]' 
                            ELSE '[]' 
                        END as imageUrls,
                        COALESCE(audioUrl, '') as audioUrl,
                        CASE 
                            WHEN localImagePath IS NOT NULL AND localImagePath != '' 
                            THEN '["' || localImagePath || '"]' 
                            ELSE '[]' 
                        END as localImagePaths,
                        localAudioPath,
                        `order`
                    FROM attractions
                """.trimIndent())
                
                // Удаляем старую таблицу
                database.execSQL("DROP TABLE attractions")
                
                // Переименовываем новую таблицу
                database.execSQL("ALTER TABLE attractions_new RENAME TO attractions")
                
                // Создаем индекс
                database.execSQL("CREATE INDEX IF NOT EXISTS index_attractions_routeId ON attractions(routeId)")
            }
        }
    }
}
