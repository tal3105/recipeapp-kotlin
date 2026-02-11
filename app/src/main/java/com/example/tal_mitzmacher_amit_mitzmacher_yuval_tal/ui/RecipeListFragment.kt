package com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.R
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.data.Recipe
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.viewmodel.RecipeViewModel
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.databinding.FragmentRecipeListBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeListFragment : Fragment() {

    private var _binding: FragmentRecipeListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.ExitApp))
                    .setMessage(getString(R.string.ExitAppAlert))
                    .setNegativeButton(getString(R.string.ExitAppCancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(getString(R.string.ExitAppOut)) { _, _ ->
                        requireActivity().finish()
                    }
                    .show()
            }
        })

        val adapter = RecipeAdapter(
            onItemClick = { recipe ->
                val bundle = Bundle().apply { putInt("recipeId", recipe.id) }
                findNavController().navigate(R.id.action_recipeListFragment_to_recipeDetailFragment, bundle)
            },
            onItemLongClick = { recipe -> showDeleteConfirmation(recipe) }
        )

        binding.recyclerView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.btnGoToFavorites.setOnClickListener {
            findNavController().navigate(R.id.action_recipeListFragment_to_favoriteFragment)
        }

        binding.fabSearch.setOnClickListener {
            findNavController().navigate(R.id.action_recipeListFragment_to_searchFragment)
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_recipeListFragment_to_addEditRecipeFragment)
        }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(context, getString(R.string.logOut), Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_recipeListFragment_to_logInFragment)
        }

        //Swipe
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val recipe = adapter.currentList[position]
                    viewModel.updateFavoriteStatus(recipe.id, true)
                    Toast.makeText(context, getString(R.string.added_to_favorites), Toast.LENGTH_SHORT).show()
                }
                adapter.notifyItemChanged(position) //Returns the item to its place in the animation
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerView)

        viewModel.getAllRecipes().observe(viewLifecycleOwner) { recipes ->
            viewModel.translateRecipeList(recipes) { translatedRecipes ->
                adapter.submitList(translatedRecipes)

                if (translatedRecipes.isNullOrEmpty()) {
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.tvEmptyState.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showDeleteConfirmation(recipe: Recipe) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_recipe_title))
            .setMessage(getString(R.string.delete_recipe_message))
            .setPositiveButton(getString(R.string.yes_delete)) { _, _ ->
                viewModel.delete(recipe)
                Toast.makeText(requireContext(), getString(R.string.recipe_deleted), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}