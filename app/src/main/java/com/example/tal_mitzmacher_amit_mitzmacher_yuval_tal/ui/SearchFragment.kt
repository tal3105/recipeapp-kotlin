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
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.databinding.FragmentSearchBinding
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.viewmodel.RecipeViewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    // שימוש באותו ViewModel של כל האפליקציה
    private val viewModel: RecipeViewModel by viewModels() // או activityViewModels() אם את רוצה לשתף מידע

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // הגדרת האדפטר (משתמשים באותו אדפטר כמו במסך הראשי!)
        val adapter = RecipeAdapter(
            onItemClick = { recipe ->
                // בלחיצה על תוצאה מהאינטרנט -> נשמור אותה לדאטהבייס המקומי
                viewModel.insert(recipe)
                Toast.makeText(context, "${recipe.title} נשמר למועדפים!", Toast.LENGTH_SHORT).show()
            },
            onItemLongClick = {
                // אפשר להשאיר ריק או להוסיף פעולה אחרת
            }
        )

        binding.rvSearchResults.layoutManager = LinearLayoutManager(context)
        binding.rvSearchResults.adapter = adapter

        // האזנה לתוצאות החיפוש מה-ViewModel
        viewModel.searchResults.observe(viewLifecycleOwner) { recipes ->
            binding.progressBar.visibility = View.GONE
            if (recipes != null) {
                adapter.submitList(recipes)
            }
        }

        // הגדרת שורת החיפוש
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    binding.progressBar.visibility = View.VISIBLE
                    viewModel.searchRecipes(query) // שליחת הבקשה ל-API
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false // לא מחפשים על כל אות, רק ב-Submit
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}