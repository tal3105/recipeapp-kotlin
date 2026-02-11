package com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.R
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data.Recipe
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.databinding.FragmentRecipeDetailBinding
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.viewmodel.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeDetailFragment : Fragment() {

    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!

    // שימוש ב-ViewModel הרגיל (בלי Factory מסובך, כי בקובץ שלך ה-ViewModel מקבל רק Application)
    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // קבלת ה-ID (מותאם לקוד שלך שעובד עם Bundle)
        val recipeId = arguments?.getInt("recipeId") ?: -1

        if (recipeId != -1) {
            viewModel.getRecipeById(recipeId).observe(viewLifecycleOwner) { originalRecipe ->
                originalRecipe?.let { recipe ->
                    updateUI(recipe) // מציג מיד את מה שיש (כנראה אנגלית)

                    // קריאה לתרגום (בהתאם לשפת המכשיר)
                    viewModel.translateFullRecipe(recipe) { finalRecipe ->
                        // בדיקה שהמסך עדיין קיים לפני עדכון
                        if (_binding != null && isAdded) {
                            updateUI(finalRecipe)
                        }
                    }
                }
            }
        }

        binding.btnEditRecipe.setOnClickListener {
            val bundle = Bundle().apply { putInt("recipeId", recipeId) }
            findNavController().navigate(R.id.action_recipeDetailFragment_to_addEditRecipeFragment, bundle)
        }
    }

    private fun updateUI(recipe: Recipe) {
        binding.tvDetailTitle.text = recipe.title
        binding.tvDetailIngredients.text = recipe.ingredients
        binding.tvDetailInstructions.text = recipe.instructions

        if (!recipe.imgUri.isNullOrEmpty()) {
            Glide.with(this)
                .load(recipe.imgUri)
                .placeholder(R.drawable.placeholder_food)
                .into(binding.ivDetailImage)
        } else {
            binding.ivDetailImage.setImageResource(R.drawable.placeholder_food)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}