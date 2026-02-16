package com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data.utils

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

class TranslationAgent {

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

    suspend fun downloadModels() {
        val conditions = DownloadConditions.Builder().requireWifi().build()
        try {
            hebrewToEnglishTranslator.downloadModelIfNeeded(conditions).await()
            englishToHebrewTranslator.downloadModelIfNeeded(conditions).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    suspend fun translateToEnglish(text: String): String {
        return try {
            downloadModels()
            val resultBuilder = StringBuilder()
            val lines = text.split("\n")

            for (line in lines) {
                if (line.trim().isEmpty()) {
                    resultBuilder.append("\n")
                } else {
                    // Translate each line and add it to the result
                    val translatedLine = hebrewToEnglishTranslator.translate(line).await()
                    resultBuilder.append(translatedLine).append("\n")
                }
            }
            return resultBuilder.toString().trimEnd()
        } catch (e: Exception) {
            text  // If translation fails, return original text so the app doesn't crash
        }
    }

    suspend fun translateToHebrew(text: String): String {
        return try {
            downloadModels()
            val resultBuilder = StringBuilder()
            val lines = text.split("\n")

            for (line in lines) {
                if (line.trim().isEmpty()) {
                    resultBuilder.append("\n")
                } else {
                    val translatedLine = englishToHebrewTranslator.translate(line).await()
                    resultBuilder.append(translatedLine).append("\n")
                }
            }
            return resultBuilder.toString().trimEnd()
        } catch (e: Exception) {
            text
        }
    }

    // Helper for search queries - converting user input to English for the API
    suspend fun translateQuery(text: String) = translateToEnglish(text)
}