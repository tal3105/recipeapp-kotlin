package com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.R
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data.Recipe
import dagger.hilt.android.AndroidEntryPoint

class RecipeAdapter(private val onItemClick: (Recipe) -> Unit,
                    private val onItemLongClick: (Recipe) -> Unit ) :
    ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(RecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_item, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = getItem(position)
        holder.bind(recipe)

        holder.itemView.setOnClickListener {
            onItemClick(recipe)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClick(recipe)
            true
        }
    }

    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.textViewTitle)
        private val image: ImageView = view.findViewById(R.id.imageViewRecipe)

        fun bind(recipe: Recipe) {

            title.text = recipe.title

            if (!recipe.imgUri.isNullOrEmpty()) {
                com.bumptech.glide.Glide.with(itemView.context)
                    .load(recipe.imgUri)
                    .placeholder(R.drawable.placeholder_food)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(image)
            } else {
                image.setImageResource(R.drawable.placeholder_food)
            }
        }
    }

    class RecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    }
}