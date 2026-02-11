package com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecipeDao {

    //Get all recipes for a specific user
    @Query("SELECT * FROM recipes_table WHERE userId = :userId")
    fun getAllRecipes(userId: String): LiveData<List<Recipe>>

    // Find a specific recipe by its ID
    @Query("SELECT * FROM recipes_table WHERE id = :id AND userId = :userId")
    fun getRecipeById(id: Int, userId: String): LiveData<Recipe>



    // Get only the recipes that the user marked as favorites
    @Query("SELECT * FROM recipes_table WHERE isFavorite = 1 AND userId = :userId")
    fun getFavoriteRecipes(userId: String): LiveData<List<Recipe>>

    // toggle the favorite heart button
    @Query("UPDATE recipes_table SET isFavorite = :isFav WHERE id = :recipeId AND userId = :userId")
    suspend fun updateFavoriteStatus(recipeId: Int, isFav: Boolean, userId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: Recipe)

    @Delete
    suspend fun delete(recipe: Recipe)

    @Update
    suspend fun update(recipe: Recipe)
}