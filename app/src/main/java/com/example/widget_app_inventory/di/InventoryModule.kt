package com.example.widget_app_inventory.di

import android.content.Context
import com.example.widget_app_inventory.data.InventoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InventoryModule {

    @Provides
    @Singleton
    fun provideInventoryRepository(@ApplicationContext context: Context): InventoryRepository {
        return InventoryRepository(context)
    }
}
