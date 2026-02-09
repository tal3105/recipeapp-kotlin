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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.util.Locale

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecipeRepository = RecipeRepository(application)
    private val translationAgent = TranslationAgent()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private fun getUserId(): String = auth.currentUser?.uid ?: ""

    fun getAllRecipes(): LiveData<List<Recipe>> = repository.getAllRecipes(getUserId())
    fun getFavoriteRecipes(): LiveData<List<Recipe>> = repository.getFavoriteRecipes(getUserId())
    fun getRecipeById(id: Int): LiveData<Recipe> = repository.getRecipeById(id, getUserId())

    private val _searchResults = MutableLiveData<List<Recipe>>()
    val searchResults: LiveData<List<Recipe>> = _searchResults

    // בדיקה האם המכשיר בעברית
    private fun isDeviceInHebrew(): Boolean {
        val lang = Locale.getDefault().language
        return lang == "iw" || lang == "he"
    }

    // פונקציית עזר: האם הטקסט מכיל עברית?
    private fun containsHebrew(text: String): Boolean {
        for (char in text) {
            if (char in '\u0590'..'\u05FF') return true
        }
        return false
    }

    // --- תרגום רשימה (למסך הבית) ---
    fun translateRecipeList(list: List<Recipe>, onResult: (List<Recipe>) -> Unit) {
        viewModelScope.launch {
            val deviceIsHebrew = isDeviceInHebrew()

            // שימוש ב-async לביצועים מהירים
            val translatedList = list.map { recipe ->
                async {
                    // מדלגים אם זה מתכון אישי (לא מה-API)
                    if (!recipe.isFromApi) {
                        return@async recipe
                    }

                    val titleHasHebrew = containsHebrew(recipe.title)

                    if (deviceIsHebrew && !titleHasHebrew) {
                        // מצב 1: טלפון בעברית, טקסט באנגלית -> תרגם לעברית
                        val tTitle = translationAgent.translateToHebrew(recipe.title)
                        recipe.copy(title = tTitle)
                    } else if (!deviceIsHebrew && titleHasHebrew) {
                        // מצב 2: טלפון באנגלית, טקסט בעברית -> תרגם לאנגלית
                        val tTitle = translationAgent.translateToEnglish(recipe.title)
                        recipe.copy(title = tTitle)
                    } else {
                        // אין צורך בתרגום (השפות תואמות)
                        recipe
                    }
                }
            }.awaitAll()

            onResult(translatedList)
        }
    }

    // --- תרגום מתכון בודד (למסך הפרטים) ---
    fun translateFullRecipe(recipe: Recipe, onResult: (Recipe) -> Unit) {
        viewModelScope.launch {
            // מדלגים אם זה מתכון אישי
            if (!recipe.isFromApi) {
                onResult(recipe)
                return@launch
            }

            val deviceIsHebrew = isDeviceInHebrew()
            val hasHebrew = containsHebrew(recipe.title)

            if (deviceIsHebrew && !hasHebrew) {
                // אנגלית -> עברית
                val tRecipe = recipe.copy(
                    title = translationAgent.translateToHebrew(recipe.title),
                    ingredients = translationAgent.translateToHebrew(recipe.ingredients),
                    instructions = translationAgent.translateToHebrew(recipe.instructions)
                )
                onResult(tRecipe)
            } else if (!deviceIsHebrew && hasHebrew) {
                // עברית -> אנגלית
                val tRecipe = recipe.copy(
                    title = translationAgent.translateToEnglish(recipe.title),
                    ingredients = translationAgent.translateToEnglish(recipe.ingredients),
                    instructions = translationAgent.translateToEnglish(recipe.instructions)
                )
                onResult(tRecipe)
            } else {
                // השאר כמו שהוא
                onResult(recipe)
            }
        }
    }

    // --- חיפוש מתכונים (נשאר ללא שינוי מהותי) ---
    fun searchRecipes(query: String) {
        viewModelScope.launch {
            // אם מחפשים בעברית, נתרגם את השאילתה לאנגלית עבור ה-API
            val finalQuery = if (isDeviceInHebrew() && containsHebrew(query)) {
                translationAgent.translateToEnglish(query)
            } else {
                query
            }

            val results = repository.searchApiRecipes(finalQuery)

            // מסמנים שהמתכונים הגיעו מה-API + מתרגמים כותרות לתצוגה אם צריך
            val processedResults = results.map { recipe ->
                var finalRecipe = recipe.copy(isFromApi = true)

                if (isDeviceInHebrew()) {
                    val hebrewTitle = translationAgent.translateToHebrew(recipe.title)
                    finalRecipe = finalRecipe.copy(title = hebrewTitle)
                }
                finalRecipe
            }
            _searchResults.postValue(processedResults)
        }
    }

    // --- פעולות DB ---
    fun insert(recipe: Recipe) = viewModelScope.launch {
        repository.insert(recipe.copy(userId = getUserId()))
    }

    fun delete(recipe: Recipe) = viewModelScope.launch { repository.delete(recipe) }

    fun update(recipe: Recipe) = viewModelScope.launch {
        repository.update(recipe.copy(userId = getUserId()))
    }

    fun updateFavoriteStatus(recipeId: Int, isFav: Boolean) {
        viewModelScope.launch {
            repository.updateFavoriteStatus(recipeId, isFav, getUserId())
        }
    }
}