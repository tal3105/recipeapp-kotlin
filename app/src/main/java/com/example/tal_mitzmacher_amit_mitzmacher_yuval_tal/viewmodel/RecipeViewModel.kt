package com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data.Recipe
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data.RecipeRepository
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data.utils.TranslationAgent
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.Locale

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecipeRepository = RecipeRepository(application)
    private val translationAgent = TranslationAgent()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // --- קבלת ה-UID של המשתמש הנוכחי ---
    private fun getUserId(): String = auth.currentUser?.uid ?: ""

    // --- שליפת נתונים לפי משתמש ---
    fun getAllRecipes(): LiveData<List<Recipe>> = repository.getAllRecipes(getUserId())
    fun getFavoriteRecipes(): LiveData<List<Recipe>> = repository.getFavoriteRecipes(getUserId())

    private val _searchResults = MutableLiveData<List<Recipe>>()
    val searchResults: LiveData<List<Recipe>> = _searchResults

    // --- פונקציית עזר לבדיקת שפת מכשיר ---
    private fun isDeviceInHebrew(): Boolean {
        val lang = Locale.getDefault().language
        return lang == "iw" || lang == "he"
    }

    // --- פונקציית החיפוש החכמה (API + תרגום) ---
    fun searchRecipes(query: String) {
        viewModelScope.launch {
            if (isDeviceInHebrew()) {
                val translatedQuery = translationAgent.translateQuery(query)
                val results = repository.searchApiRecipes(translatedQuery)
                val translatedResults = results.map { recipe ->
                    val hebrewTitle = translationAgent.translateToHebrew(recipe.title)
                    recipe.copy(title = hebrewTitle)
                }
                _searchResults.postValue(translatedResults)
            } else {
                val results = repository.searchApiRecipes(query)
                _searchResults.postValue(results)
            }
        }
    }

    // --- פונקציית התרגום למסך הפירוט - הפונקציה שהייתה חסרה! ---
    fun translateFullRecipe(recipe: Recipe, onResult: (Recipe) -> Unit) {
        viewModelScope.launch {
            if (isDeviceInHebrew()) {
                // תרחיש 1: המכשיר בעברית
                if (!containsHebrew(recipe.title)) {
                    // אם הכותרת באנגלית -> תרגם לעברית
                    val translatedRecipe = recipe.copy(
                        title = translationAgent.translateToHebrew(recipe.title),
                        instructions = translationAgent.translateToHebrew(recipe.instructions),
                        ingredients = translationAgent.translateToHebrew(recipe.ingredients)
                    )
                    onResult(translatedRecipe)
                } else {
                    // אם זה כבר בעברית -> אל תיגע
                    onResult(recipe)
                }
            } else {
                // תרחיש 2: המכשיר באנגלית
                if (containsHebrew(recipe.title)) {
                    // אם הכותרת בעברית -> תרגם לאנגלית
                    val translatedRecipe = recipe.copy(
                        title = translationAgent.translateToEnglish(recipe.title),
                        instructions = translationAgent.translateToEnglish(recipe.instructions),
                        ingredients = translationAgent.translateToEnglish(recipe.ingredients)
                    )
                    onResult(translatedRecipe)
                } else {
                    // אם זה כבר באנגלית -> אל תיגע
                    onResult(recipe)
                }
            }
        }
    }

    // --- פעולות בסיס נתונים מקומי עם הזרקת UID ---

    fun insert(recipe: Recipe) = viewModelScope.launch {
        val recipeWithUser = recipe.copy(userId = getUserId())
        repository.insert(recipeWithUser)
    }

    fun delete(recipe: Recipe) = viewModelScope.launch { repository.delete(recipe) }

    fun update(recipe: Recipe) = viewModelScope.launch {
        val recipeWithUser = recipe.copy(userId = getUserId())
        repository.update(recipeWithUser)
    }

    fun getRecipeById(id: Int): LiveData<Recipe> {
        return repository.getRecipeById(id, getUserId())
    }

    fun updateFavoriteStatus(recipeId: Int, isFav: Boolean) {
        viewModelScope.launch {
            repository.updateFavoriteStatus(recipeId, isFav, getUserId())
        }
    }
    private fun containsHebrew(text: String): Boolean {
        for (char in text) {
            // טווח היוניקוד של אותיות בעברית
            if (char in '\u0590'..'\u05FF') return true
        }
        return false
    }

}