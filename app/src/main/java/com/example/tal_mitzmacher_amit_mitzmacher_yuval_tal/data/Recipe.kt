package com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes_table")
data class Recipe (
    @PrimaryKey (true) val id: Int = 0,
            val title: String,
            val ingredients: String,
            val instructions: String,
            val imgUri: String?,
            val isFavorite: Boolean = false,
            val userId: String,
            val isFromApi: Boolean = false

    )
