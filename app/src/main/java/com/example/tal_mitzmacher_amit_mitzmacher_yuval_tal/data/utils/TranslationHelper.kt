package com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data.utils

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

class TranslationAgent {

    // הגדרת המתרגם מעברית לאנגלית
    private val hebrewToEnglishOptions = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.HEBREW)
        .setTargetLanguage(TranslateLanguage.ENGLISH)
        .build()
    private val hebrewToEnglishTranslator = Translation.getClient(hebrewToEnglishOptions)

    // הגדרת המתרגם מאנגלית לעברית
    private val englishToHebrewOptions = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.HEBREW)
        .build()
    private val englishToHebrewTranslator = Translation.getClient(englishToHebrewOptions)

    // פונקציה שמורידה את המודלים הדרושים (קורית פעם אחת)
    suspend fun downloadModels() {
        val conditions = DownloadConditions.Builder().requireWifi().build()
        try {
            hebrewToEnglishTranslator.downloadModelIfNeeded(conditions).await()
            englishToHebrewTranslator.downloadModelIfNeeded(conditions).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // פונקציה לתרגום טקסט מעברית לאנגלית
    // (משמשת גם לחיפוש וגם להמרת מתכונים)
    suspend fun translateToEnglish(text: String): String {
        return try {
            downloadModels()
            hebrewToEnglishTranslator.translate(text).await()
        } catch (e: Exception) {
            text // במקרה של שגיאה מחזיר את המקור
        }
    }

    // פונקציה לתרגום טקסט מאנגלית לעברית
    // (משמשת לתרגום מתכונים וכותרות)
    suspend fun translateToHebrew(text: String): String {
        return try {
            downloadModels()
            englishToHebrewTranslator.translate(text).await()
        } catch (e: Exception) {
            text
        }
    }

    // --- תמיכה לאחור ---
    // השארתי את הפונקציה הזו למקרה שעדיין לא עדכנת את ה-ViewModel,
    // היא פשוט קוראת לפונקציה החדשה.
    suspend fun translateQuery(text: String) = translateToEnglish(text)
}