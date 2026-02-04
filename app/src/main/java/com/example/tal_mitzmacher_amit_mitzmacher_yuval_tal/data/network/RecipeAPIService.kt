package com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApiService {

    // בקשה 1: חיפוש מתכונים לפי שם (למשל: "Chicken")
    @GET("search.php")
    suspend fun searchRecipes(@Query("s") query: String): MealResponse

    // בקשה 2: קבלת פרטי מתכון לפי מזהה (ID)
    @GET("lookup.php")
    suspend fun getRecipeDetails(@Query("i") id: String): MealResponse

    // בקשה 3: קבלת מתכון אקראי (למשל למסך הבית)
    @GET("random.php")
    suspend fun getRandomRecipe(): MealResponse
}