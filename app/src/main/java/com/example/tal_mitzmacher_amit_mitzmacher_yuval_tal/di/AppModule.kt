package com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.di

import android.app.Application
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data.RecipeRepository
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data.utils.TranslationAgent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//Hilt Module.
//tells Hilt how to create the objects we need
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    //tells Hilt how to provide the TranslationAgent
    @Provides
    @Singleton
    fun provideTranslationAgent(): TranslationAgent {
        return TranslationAgent()
    }

    //provides the Repository
    @Provides
    @Singleton
    fun provideRecipeRepository(app: Application): RecipeRepository {
        return RecipeRepository(app)
    }
}