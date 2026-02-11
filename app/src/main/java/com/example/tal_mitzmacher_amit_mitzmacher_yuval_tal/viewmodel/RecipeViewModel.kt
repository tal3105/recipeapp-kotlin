package com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data.Recipe
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data.RecipeRepository
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data.utils.TranslationAgent
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: RecipeRepository,
    private val translationAgent: TranslationAgent
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private fun getUserId(): String = auth.currentUser?.uid ?: ""

    fun getAllRecipes(): LiveData<List<Recipe>> = repository.getAllRecipes(getUserId())
    fun getFavoriteRecipes(): LiveData<List<Recipe>> = repository.getFavoriteRecipes(getUserId())
    fun getRecipeById(id: Int): LiveData<Recipe> = repository.getRecipeById(id, getUserId())

    private val _searchResults = MutableLiveData<List<Recipe>>()
    val searchResults: LiveData<List<Recipe>> = _searchResults

    private var lastApiResults: List<Recipe> = emptyList()

    // Check if the device language is Hebrew
    private fun isDeviceInHebrew(): Boolean {
        val lang = Locale.getDefault().language
        return lang == "iw" || lang == "he"
    }

    // Helper function: Does the text contain Hebrew?
    private fun containsHebrew(text: String): Boolean {
        for (char in text) {
            if (char in '\u0590'..'\u05FF') return true
        }
        return false
    }

    // --- Translate recipe list (for Home screen) ---
    fun translateRecipeList(list: List<Recipe>, onResult: (List<Recipe>) -> Unit) {
        viewModelScope.launch {
            val deviceIsHebrew = isDeviceInHebrew()

            // Use async for faster performance
            val translatedList = list.map { recipe ->
                async {
                    // Skip if it is a personal recipe (not from API)
                    if (!recipe.isFromApi) {
                        return@async recipe
                    }

                    val titleHasHebrew = containsHebrew(recipe.title)

                    if (deviceIsHebrew && !titleHasHebrew) {
                        // Case 1: Device is Hebrew, text is English -> translate to Hebrew
                        val tTitle = translationAgent.translateToHebrew(recipe.title)
                        recipe.copy(title = tTitle)
                    } else if (!deviceIsHebrew && titleHasHebrew) {
                        // Case 2: Device is English, text is Hebrew -> translate to English
                        val tTitle = translationAgent.translateToEnglish(recipe.title)
                        recipe.copy(title = tTitle)
                    } else {
                        // No translation needed (languages match)
                        recipe
                    }
                }
            }.awaitAll()

            onResult(translatedList)
        }
    }

    // --- Translate a single recipe (for Details screen) ---
    fun translateFullRecipe(recipe: Recipe, onResult: (Recipe) -> Unit) {
        viewModelScope.launch {
            // Skip if it is a personal recipe
            if (!recipe.isFromApi) {
                onResult(recipe)
                return@launch
            }

            val deviceIsHebrew = isDeviceInHebrew()

            // Check each field independently to avoid partial translation
            val titleHasHebrew = containsHebrew(recipe.title)
            val instructionsHaveHebrew = containsHebrew(recipe.instructions)
            val ingredientsHaveHebrew = containsHebrew(recipe.ingredients)

            var tTitle = recipe.title
            var tInstructions = recipe.instructions
            var tIngredients = recipe.ingredients

            if (deviceIsHebrew) {
                // Device is Hebrew -> translate to Hebrew what is missing
                if (!titleHasHebrew) tTitle = translationAgent.translateToHebrew(recipe.title)
                if (!instructionsHaveHebrew) tInstructions = translationAgent.translateToHebrew(recipe.instructions)
                if (!ingredientsHaveHebrew) tIngredients = translationAgent.translateToHebrew(recipe.ingredients)
            } else {
                // Device is English -> translate to English what is in Hebrew
                if (titleHasHebrew) tTitle = translationAgent.translateToEnglish(recipe.title)
                if (instructionsHaveHebrew) tInstructions = translationAgent.translateToEnglish(recipe.instructions)
                if (ingredientsHaveHebrew) tIngredients = translationAgent.translateToEnglish(recipe.ingredients)
            }

            val finalRecipe = recipe.copy(
                title = tTitle,
                instructions = tInstructions,
                ingredients = tIngredients
            )
            onResult(finalRecipe)
        }
    }

    // --- Search recipes ---
    fun searchRecipes(query: String) {
        viewModelScope.launch {
            // If searching in Hebrew, translate the query to English for the API
            val finalQuery = if (isDeviceInHebrew() && containsHebrew(query)) {
                translationAgent.translateToEnglish(query)
            } else {
                query
            }

            val results = repository.searchApiRecipes(finalQuery)

            // Mark recipes as from API and translate titles if needed
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

    // --- מתכון אקראי ---
    fun getRandomRecipe() {
        viewModelScope.launch {
            val results = repository.getRandomRecipe()

            // 1. Save the pure ORIGINAL English results in memory
            lastApiResults = results.map { it.copy(isFromApi = true) }

            // 2. Translate only the copy we send to the UI
            val processedResults = lastApiResults.map { recipe ->
                if (isDeviceInHebrew()) {
                    recipe.copy(title = translationAgent.translateToHebrew(recipe.title))
                } else {
                    recipe
                }
            }
            _searchResults.postValue(processedResults)
        }
    }

    // --- DB operations ---
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