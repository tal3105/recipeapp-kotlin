package com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // וודאי שיש לך את התלות של fragment-ktx
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.R
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.databinding.FragmentSearchBinding
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.viewmodel.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RecipeAdapter(
            onItemClick = { recipe ->
                viewModel.insert(recipe)
                val message = context?.getString(R.string.added_to_home)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            },
            onItemLongClick = {
            }
        )
        binding.btnRandomRecipe.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            //clean the previous search
            adapter.submitList(emptyList())
            viewModel.getRandomRecipe()
        }

        binding.rvSearchResults.layoutManager = LinearLayoutManager(context)
        binding.rvSearchResults.adapter = adapter

        viewModel.searchResults.observe(viewLifecycleOwner) { recipes ->
            binding.progressBar.visibility = View.GONE
            if (recipes != null) {
                adapter.submitList(recipes)
            }
        }

        //Setting the search bar
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    binding.progressBar.visibility = View.VISIBLE
                    viewModel.searchRecipes(query) // sent the request to the API
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false // Don't search for every letter, just after submit
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}