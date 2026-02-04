package com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.R
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data.Recipe
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.databinding.FragmentAddEditRecipeBinding
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.viewmodel.RecipeViewModel

class AddEditRecipeFragment : Fragment() {

    private var _binding: FragmentAddEditRecipeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecipeViewModel by viewModels()
    private var selectedImageUri: Uri? = null
    private var recipeId: Int = -1

    private var isCurrentlyFavorite: Boolean = false

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            binding.imageViewSelected.setImageURI(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddEditRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // קבלת ה-ID של המתכון מה-Navigation (אם קיים)
        recipeId = arguments?.getInt("recipeId", -1) ?: -1

        if (recipeId != -1) {
            setupEditMode()
        }

        binding.btnPickImage.setOnClickListener { pickImageLauncher.launch("image/*") }
        binding.btnSaveRecipe.setOnClickListener { saveRecipe() }
    }

    private fun setupEditMode() {
        binding.tvAddRecipeTitle.text = getString(R.string.edit_recipe_title)

        viewModel.getRecipeById(recipeId).observe(viewLifecycleOwner) { recipe ->
            recipe?.let {
                binding.editTextTitle.setText(it.title)
                binding.editTextIngredients.setText(it.ingredients)
                binding.editTextInstructions.setText(it.instructions)

                isCurrentlyFavorite = it.isFavorite

                if (!it.imgUri.isNullOrEmpty()) {
                    selectedImageUri = Uri.parse(it.imgUri)
                    binding.imageViewSelected.setImageURI(selectedImageUri)
                }
            }
        }
    }

    private fun saveRecipe() {
        val title = binding.editTextTitle.text.toString().trim()
        val ingredients = binding.editTextIngredients.text.toString().trim()
        val instructions = binding.editTextInstructions.text.toString().trim()

        // בדיקה שכל השדות מלאים
        if (title.isEmpty() || ingredients.isEmpty() || instructions.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }

        // יצירת אובייקט המתכון עם שדה ה-userId החדש
        val recipe = Recipe(
            id = if (recipeId == -1) 0 else recipeId,
            title = title,
            ingredients = ingredients,
            instructions = instructions,
            imgUri = selectedImageUri?.toString(),
            isFavorite = isCurrentlyFavorite,
            userId = "" // ה-ViewModel ידאג להזריק כאן את ה-UID של המשתמש המחובר
        )

        if (recipeId == -1) {
            viewModel.insert(recipe)
            Toast.makeText(requireContext(), getString(R.string.recipe_saved), Toast.LENGTH_SHORT).show()
        } else {
            viewModel.update(recipe)
            Toast.makeText(requireContext(), getString(R.string.recipe_updated), Toast.LENGTH_SHORT).show()
        }

        // חזרה למסך הקודם
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}