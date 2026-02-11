package com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApiService {

    // Request 1: Search for recipes by name
    @GET("search.php")
    suspend fun searchRecipes(@Query("s") query: String): MealResponse

    // Request 2: Get all the info for a specific recipe using its ID.
    @GET("lookup.php")
    suspend fun getRecipeDetails(@Query("i") id: String): MealResponse

    // Request 3: Get one random recipe from the server.
    @GET("random.php")
    suspend fun getRandomRecipe(): MealResponse
}