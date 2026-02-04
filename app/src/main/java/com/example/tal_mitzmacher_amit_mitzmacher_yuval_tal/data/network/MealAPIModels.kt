package com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data.network

import com.google.gson.annotations.SerializedName

data class MealResponse(
    @SerializedName("meals")
    val meals: List<MealDto>?
)

data class MealDto(
    @SerializedName("idMeal") val id: String,
    @SerializedName("strMeal") val name: String,
    @SerializedName("strCategory") val category: String,
    @SerializedName("strArea") val area: String,
    @SerializedName("strInstructions") val instructions: String,
    @SerializedName("strMealThumb") val imageUrl: String,
    @SerializedName("strYoutube") val youtubeUrl: String?,

    // --- הוספת שדות המצרכים והכמויות (עד 20) ---
    val strIngredient1: String?, val strIngredient2: String?, val strIngredient3: String?,
    val strIngredient4: String?, val strIngredient5: String?, val strIngredient6: String?,
    val strIngredient7: String?, val strIngredient8: String?, val strIngredient9: String?,
    val strIngredient10: String?, val strIngredient11: String?, val strIngredient12: String?,
    val strIngredient13: String?, val strIngredient14: String?, val strIngredient15: String?,
    val strIngredient16: String?, val strIngredient17: String?, val strIngredient18: String?,
    val strIngredient19: String?, val strIngredient20: String?,

    val strMeasure1: String?, val strMeasure2: String?, val strMeasure3: String?,
    val strMeasure4: String?, val strMeasure5: String?, val strMeasure6: String?,
    val strMeasure7: String?, val strMeasure8: String?, val strMeasure9: String?,
    val strMeasure10: String?, val strMeasure11: String?, val strMeasure12: String?,
    val strMeasure13: String?, val strMeasure14: String?, val strMeasure15: String?,
    val strMeasure16: String?, val strMeasure17: String?, val strMeasure18: String?,
    val strMeasure19: String?, val strMeasure20: String?
) {
    // פונקציה עזר שמאחדת את המצרכים והכמויות לרשימה אחת
    fun getFormattedIngredients(): String {
        val ingredientsList = mutableListOf<String>()

        // רשימה של כל זוגות (מצרך, כמות)
        val pairs = listOf(
            strIngredient1 to strMeasure1, strIngredient2 to strMeasure2,
            strIngredient3 to strMeasure3, strIngredient4 to strMeasure4,
            strIngredient5 to strMeasure5, strIngredient6 to strMeasure6,
            strIngredient7 to strMeasure7, strIngredient8 to strMeasure8,
            strIngredient9 to strMeasure9, strIngredient10 to strMeasure10,
            strIngredient11 to strMeasure11, strIngredient12 to strMeasure12,
            strIngredient13 to strMeasure13, strIngredient14 to strMeasure14,
            strIngredient15 to strMeasure15, strIngredient16 to strMeasure16,
            strIngredient17 to strMeasure17, strIngredient18 to strMeasure18,
            strIngredient19 to strMeasure19, strIngredient20 to strMeasure20
        )

        for ((ingredient, measure) in pairs) {
            // אם המצרך לא ריק, הוסף אותו לרשימה
            if (!ingredient.isNullOrBlank()) {
                val qty = if (!measure.isNullOrBlank()) " - $measure" else ""
                ingredientsList.add("$ingredient$qty")
            }
        }

        // מחזיר מחרוזת עם ירידת שורה בין כל מצרך
        return ingredientsList.joinToString("\n")
    }
}