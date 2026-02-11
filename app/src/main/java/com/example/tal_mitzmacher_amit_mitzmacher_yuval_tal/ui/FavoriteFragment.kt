package com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.R
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data.Recipe
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.databinding.FragmentFavoriteBinding
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.viewmodel.RecipeViewModel

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RecipeAdapter(
            onItemClick = { recipe ->
                val bundle = Bundle().apply { putInt("recipeId", recipe.id) }
                findNavController().navigate(R.id.action_favoriteFragment_to_recipeDetailFragment, bundle)
            },
            onItemLongClick = { recipe ->
                showRemoveFavoriteDialog(recipe)
            }
        )

        binding.rvFavorites.adapter = adapter
        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())

        // Observe the favorite recipes from the database
        viewModel.getFavoriteRecipes().observe(viewLifecycleOwner) { favorites ->
            if (favorites.isNullOrEmpty()) {
                // Show empty state if there are no favorites
                binding.tvEmptyFavorites.visibility = View.VISIBLE
                binding.rvFavorites.visibility = View.GONE
                adapter.submitList(emptyList())
            } else {
                // Hide empty state and show RecyclerView
                binding.tvEmptyFavorites.visibility = View.GONE
                binding.rvFavorites.visibility = View.VISIBLE

                // Show original list immediately for a snappy UI
                adapter.submitList(favorites)

                // Translate the list titles automatically based on device language
                viewModel.translateRecipeList(favorites) { translatedList ->
                    // Check if fragment is still attached to avoid crashes
                    if (_binding != null) {
                        adapter.submitList(translatedList)
                    }
                }
            }
        }
    }

    private fun showRemoveFavoriteDialog(recipe: Recipe) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.remove_favorite_title))
        builder.setMessage(getString(R.string.remove_favorite_confirm_message, recipe.title))

        builder.setPositiveButton(getString(R.string.yes_remove)) { _, _ ->
            // Update favorite status in DB
            viewModel.updateFavoriteStatus(recipe.id, false)

            val message = getString(R.string.removed_from_favorites, recipe.title)
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}