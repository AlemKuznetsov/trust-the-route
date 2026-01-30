package com.trusttheroute.app.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.trusttheroute.app.data.database.AppDatabase
import com.trusttheroute.app.data.database.dao.RouteDao
import com.trusttheroute.app.data.database.dao.AttractionDao
import com.trusttheroute.app.data.local.RouteDataLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "trust_the_route_database"
        )
        .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
        .build()
    }

    @Provides
    fun provideRouteDao(database: AppDatabase): RouteDao {
        return database.routeDao()
    }

    @Provides
    fun provideAttractionDao(database: AppDatabase): AttractionDao {
        return database.attractionDao()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun provideRouteDataLoader(
        @ApplicationContext context: Context,
        routeDao: RouteDao,
        attractionDao: AttractionDao,
        gson: Gson
    ): RouteDataLoader {
        return RouteDataLoader(context, routeDao, attractionDao, gson)
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}
