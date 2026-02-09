package com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data.utils

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

class TranslationAgent {

    // --- הגדרת המתרגמים (נשאר אותו דבר) ---
    private val hebrewToEnglishOptions = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.HEBREW)
        .setTargetLanguage(TranslateLanguage.ENGLISH)
        .build()
    private val hebrewToEnglishTranslator = Translation.getClient(hebrewToEnglishOptions)

    private val englishToHebrewOptions = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.HEBREW)
        .build()
    private val englishToHebrewTranslator = Translation.getClient(englishToHebrewOptions)

    // --- הורדת מודלים (נשאר אותו דבר) ---
    suspend fun downloadModels() {
        val conditions = DownloadConditions.Builder().requireWifi().build()
        try {
            hebrewToEnglishTranslator.downloadModelIfNeeded(conditions).await()
            englishToHebrewTranslator.downloadModelIfNeeded(conditions).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // פונקציה לתרגום טקסט מעברית לאנגלית (שומרת על שורות)
    suspend fun translateToEnglish(text: String): String {
        return try {
            downloadModels()
            val resultBuilder = StringBuilder()
            // מפצל את הטקסט לפי ירידות שורה
            val lines = text.split("\n")

            for (line in lines) {
                if (line.trim().isEmpty()) {
                    // אם השורה ריקה, שומר על הרווח
                    resultBuilder.append("\n")
                } else {
                    // מתרגם רק את השורה הנוכחית
                    val translatedLine = hebrewToEnglishTranslator.translate(line).await()
                    resultBuilder.append(translatedLine).append("\n")
                }
            }
            // מחזיר את התוצאה ומוחק רווחים מיותרים בסוף
            return resultBuilder.toString().trimEnd()
        } catch (e: Exception) {
            text
        }
    }

    // פונקציה לתרגום טקסט מאנגלית לעברית (שומרת על שורות - קריטי למצרכים!)
    suspend fun translateToHebrew(text: String): String {
        return try {
            downloadModels()
            val resultBuilder = StringBuilder()
            // מפצל את הטקסט לפי ירידות שורה
            val lines = text.split("\n")

            for (line in lines) {
                if (line.trim().isEmpty()) {
                    // אם השורה ריקה, שומר על הרווח
                    resultBuilder.append("\n")
                } else {
                    // מתרגם רק את השורה הנוכחית
                    val translatedLine = englishToHebrewTranslator.translate(line).await()
                    resultBuilder.append(translatedLine).append("\n")
                }
            }
            return resultBuilder.toString().trimEnd()
        } catch (e: Exception) {
            text
        }
    }

    // --- תמיכה לאחור ---
    suspend fun translateQuery(text: String) = translateToEnglish(text)
}