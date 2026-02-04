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
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.databinding.FragmentRecipeDetailBinding
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.viewmodel.RecipeViewModel

class RecipeDetailFragment : Fragment() {

    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!

    // שימוש ב-viewModels() לקבלת ה-ViewModel המשותף
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

        // שליפת ה-ID של המתכון שנשלח ב-Bundle
        val recipeId = arguments?.getInt("recipeId") ?: -1

        // קריאה למתכון מהדאטהבייס דרך ה-ViewModel (שמזהה אוטומטית את המשתמש המחובר)
        viewModel.getRecipeById(recipeId).observe(viewLifecycleOwner) { recipe ->
            recipe?.let { originalRecipe ->

                // 1. הצגת הנתונים המקוריים (מה ששמור בדאטהבייס)
                binding.tvDetailTitle.text = originalRecipe.title
                binding.tvDetailIngredients.text = originalRecipe.ingredients
                binding.tvDetailInstructions.text = originalRecipe.instructions

                // טעינת תמונה עם Glide
                if (!originalRecipe.imgUri.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(originalRecipe.imgUri)
                        .placeholder(R.drawable.placeholder_food)
                        .error(R.drawable.placeholder_food)
                        .into(binding.ivDetailImage)
                } else {
                    binding.ivDetailImage.setImageResource(R.drawable.placeholder_food)
                }

                // 2. תרגום אוטומטי לעברית (אם המכשיר בעברית)
                // ה-Callback ירוץ ברגע שהתרגום מה-Agent יסתיים
                viewModel.translateFullRecipe(originalRecipe) { translatedRecipe ->
                    // בדיקה חשובה שהפרגמנט עדיין "חי" (נמנע מ-NullPointerException)
                    if (_binding != null && isAdded) {
                        binding.tvDetailTitle.text = translatedRecipe.title
                        binding.tvDetailIngredients.text = translatedRecipe.ingredients
                        binding.tvDetailInstructions.text = translatedRecipe.instructions
                    }
                }
            }
        }

        // ניווט למסך עריכה
        binding.btnEditRecipe.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("recipeId", recipeId)
            }
            findNavController().navigate(R.id.action_recipeDetailFragment_to_addEditRecipeFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}