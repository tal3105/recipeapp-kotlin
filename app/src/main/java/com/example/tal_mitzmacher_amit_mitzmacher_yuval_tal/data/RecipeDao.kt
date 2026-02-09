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

    // מביא את כל המתכונים של משתמש ספציפי
    @Query("SELECT * FROM recipes_table WHERE userId = :userId")
    fun getAllRecipes(userId: String): LiveData<List<Recipe>>

    // מוודא שהמתכון שייך למשתמש לפני שהוא שולף אותו לפי ID
    @Query("SELECT * FROM recipes_table WHERE id = :id AND userId = :userId")
    fun getRecipeById(id: Int, userId: String): LiveData<Recipe>



    // מביא רק את המועדפים של המשתמש המחובר
    @Query("SELECT * FROM recipes_table WHERE isFavorite = 1 AND userId = :userId")
    fun getFavoriteRecipes(userId: String): LiveData<List<Recipe>>

    @Query("UPDATE recipes_table SET isFavorite = :isFav WHERE id = :recipeId AND userId = :userId")
    suspend fun updateFavoriteStatus(recipeId: Int, isFav: Boolean, userId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: Recipe)

    @Delete
    suspend fun delete(recipe: Recipe)

    @Update
    suspend fun update(recipe: Recipe)
}