package com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data.network.RetrofitClient

class RecipeRepository(application: Application) {

    private val recipeDao: RecipeDao
    private val apiService = RetrofitClient.apiService

    init {
        val db = AppDatabase.getDatabase(application)
        recipeDao = db.recipeDao()
    }

    fun getAllRecipes(userId: String): LiveData<List<Recipe>> = recipeDao.getAllRecipes(userId)

    fun getFavoriteRecipes(userId: String): LiveData<List<Recipe>> = recipeDao.getFavoriteRecipes(userId)

    suspend fun insert(recipe: Recipe) = recipeDao.insert(recipe)

    suspend fun delete(recipe: Recipe) = recipeDao.delete(recipe)

    suspend fun update(recipe: Recipe) = recipeDao.update(recipe)

    fun getRecipeById(id: Int, userId: String): LiveData<Recipe> = recipeDao.getRecipeById(id, userId)

    suspend fun updateFavoriteStatus(recipeId: Int, isFav: Boolean, userId: String) =
        recipeDao.updateFavoriteStatus(recipeId, isFav, userId)



    // --- חיפוש ב-API - כאן הייתה השגיאה ---
    suspend fun searchApiRecipes(query: String): List<Recipe> {
        return try {
            val response = apiService.searchRecipes(query)

            response.meals?.map { dto ->
                // חשוב: שולחים id = 0 כדי שה-Room ייצר ID אוטומטי כשנשמור את המתכון
                Recipe(
                    id = 0,
                    title = dto.name ?: "",
                    ingredients = dto.getFormattedIngredients() ?: "",
                    instructions = dto.instructions ?: "",
                    imgUri = dto.imageUrl,
                    isFavorite = false,
                    userId = "" // נשאר ריק עד לרגע השמירה ב-ViewModel
                )
            } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    suspend fun getRandomRecipe(): List<Recipe> {
        return try {
            val response = apiService.getRandomRecipe()

            response.meals?.map { dto ->
                Recipe(
                    id = 0,
                    title = dto.name ?: "",
                    ingredients = dto.getFormattedIngredients() ?: "",
                    instructions = dto.instructions ?: "",
                    imgUri = dto.imageUrl,
                    isFavorite = false,
                    userId = ""
                )
            } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}